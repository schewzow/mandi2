package de.conti.tires.mandi.backend.user;

import de.conti.tires.mandi.backend.core.exception.BadCredentialsException;
import de.conti.tires.mandi.backend.core.exception.GenericException;
import de.conti.tires.mandi.container.security.jwt.JwtUtils;
import de.conti.tires.mandi.container.security.request.LoginRequest;
import de.conti.tires.mandi.container.security.response.MessageResponse;
import de.conti.tires.mandi.container.security.response.UserInfoResponse;
import de.conti.tires.mandi.container.security.services.UserDetailsImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("${BASE_URL}/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication;

        try {
            authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        } catch (AuthenticationException exception) {
            throw new BadCredentialsException();
//            Map<String, Object> map = new HashMap<>();
//            map.put("message", "Bad credentials");
//            map.put("status", false);
//            return new ResponseEntity<Object>(map, HttpStatus.NOT_FOUND);
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        assert userDetails != null;
        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);

        // 2. Generate and Save Refresh Token in DB
        RefreshTokenEntity refreshToken = refreshTokenService.createRefreshToken(userDetails.getUuid());

        // 3. Generate Refresh Token Cookie for the Browser
        ResponseCookie jwtRefreshCookie = jwtUtils.generateRefreshJwtCookie(refreshToken.getToken());


        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        UserInfoResponse response = new UserInfoResponse(userDetails.getUuid(),
                userDetails.getUsername(), roles, jwtCookie.toString(), userDetails.getLanguage());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .header(HttpHeaders.SET_COOKIE, jwtRefreshCookie.toString())
                .body(response);
    }

//    @PostMapping("/signup")
//    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
//        if (userRepository.existsByUserName(signUpRequest.getUsername())) {
//            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
//        }
//

    /// /        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
    /// /            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
    /// /        }
//
//        // Create new user's account
//        UserEntity user = new UserEntity(signUpRequest.getUsername(),
//                //signUpRequest.getEmail(),
//                encoder.encode(signUpRequest.getPassword()));
//
//        Set<String> strRoles = signUpRequest.getRole();
//        Set<RoleEntity> roles = new HashSet<>();
//
//        if (strRoles == null) {
//            RoleEntity userRole = roleRepository.findByRoleName(AppRole.USER)
//                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
//            roles.add(userRole);
//        } else {
//            strRoles.forEach(role -> {
//                switch (role) {
//                    case "admin":
//                        RoleEntity adminRole = roleRepository.findByRoleName(AppRole.ADMIN)
//                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
//                        roles.add(adminRole);
//
//                        break;
//                    case "seller":
//                        RoleEntity modRole = roleRepository.findByRoleName(AppRole.SELLER)
//                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
//                        roles.add(modRole);
//
//                        break;
//                    default:
//                        RoleEntity userRole = roleRepository.findByRoleName(AppRole.USER)
//                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
//                        roles.add(userRole);
//                }
//            });
//        }
//
//        user.setRoles(roles);
//        userRepository.save(user);
//
//        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
//    }

//    @GetMapping("/username")
//    public String currentUserName(Authentication authentication){
//        if (authentication != null)
//            return authentication.getName();
//        else
//            return "";
//    }
    @GetMapping("/user")
    public ResponseEntity<?> getUserDetails(Authentication authentication) {

        if (authentication == null) {
            return null; //ResponseEntity.badRequest().body(new MessageResponse("Error: User is not logged in!"));
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        //System.out.println(authentication.isAuthenticated());

        assert userDetails != null;
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        UserInfoResponse response = new UserInfoResponse(userDetails.getUuid(),
                userDetails.getUsername(), roles, userDetails.getLanguage());

        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/signout")
    public ResponseEntity<?> signoutUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl userDetails) {
            // Delete the refresh token from DB for this specific user
            refreshTokenService.deleteByUserId(userDetails.getUuid());
        }

        ResponseCookie cookie = jwtUtils.getCleanJwtCookie();
        ResponseCookie jwtRefreshCookie = jwtUtils.getCleanRefreshJwtCookie();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .header(HttpHeaders.SET_COOKIE, jwtRefreshCookie.toString())
                .body(new MessageResponse("You've been signed out!"));
    }

    @PostMapping("/refreshtoken")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        // 1. Get Refresh Token from Cookie
        String refreshToken = jwtUtils.getRefreshTokenFromCookies(request);
        // 2. Find in DB and check expiration
        return refreshTokenService.findByToken(refreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshTokenEntity::getUser)
                .map(user -> {
                    // 3. Generate a NEW Access Token Cookie
                    ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(UserDetailsImpl.build(user));
                    return ResponseEntity.ok()
                            .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                            .body(new MessageResponse("Token refreshed successfully!"));
                })
                .orElseThrow(() -> new GenericException(HttpStatus.UNAUTHORIZED, "Refresh token is not in database!"));
    }
}
