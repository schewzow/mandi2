package de.conti.tires.mandi.container.security;

import de.conti.tires.mandi.backend.laboratory.LaboratoryEntity;
import de.conti.tires.mandi.backend.laboratory.LaboratoryRepository;
import de.conti.tires.mandi.backend.user.*;
import de.conti.tires.mandi.container.security.jwt.AuthEntryPointJwt;
import de.conti.tires.mandi.container.security.jwt.AuthTokenFilter;
import de.conti.tires.mandi.container.security.services.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.time.LocalDateTime;
import java.util.Set;

@Configuration
@EnableWebSecurity
//@EnableMethodSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {
    @Value("${BASE_URL}")
    private String baseUrl;

    private final UserDetailsServiceImpl userDetailsService;
    private final AuthEntryPointJwt unauthorizedHandler;

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }


    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);

        //authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }


//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
//        return authConfig.getAuthenticationManager();
//    }

    @Bean
    public AuthenticationManager authenticationManager(
            DaoAuthenticationProvider provider) {

        return new ProviderManager(provider);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

//    @Bean
//    CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration configuration = new CorsConfiguration();
//
//
//        configuration.setAllowedOrigins(Arrays.asList("http://localhost:8080", "http://localhost:5173", "https://forand.eu"));
//        configuration.setAllowedHeaders(Arrays.asList("*"));
//        configuration.setAllowedMethods(Arrays.asList("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
//        configuration.setAllowCredentials(true);
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", configuration);
//        return source;
//    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        //System.out.println("Base URL: " + baseUrl);

        http.csrf(csrf -> csrf.disable())
                .cors(cors -> {})
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth ->
                                auth
                                        .requestMatchers("/h2-console/**").permitAll()
                                        //.requestMatchers(baseUrl +"/api/user/**").authenticated()
                                        .requestMatchers(baseUrl +"/api/users/**").authenticated()
                                        .requestMatchers(baseUrl +"/api/labs/**").authenticated()
                                        .requestMatchers(baseUrl +"/api/autoexpenses/**").authenticated()
                                // this must be the last
                                .requestMatchers(baseUrl + "/**").permitAll()
                );

        http.authenticationProvider(authenticationProvider());

        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        http.headers(headers -> headers.frameOptions(
                frameOptions -> frameOptions.sameOrigin()));

        // Add CORS filters
        //http.cors(Customizer.withDefaults());

        return http.build();
    }

//    @Bean
//    public WebSecurityCustomizer webSecurityCustomizer() {
//        return (web -> web.ignoring().requestMatchers(baseUrl +"/v2/api-docs",
//                baseUrl +"/configuration/ui",
//                baseUrl +"/swagger-resources/**",
//                baseUrl +"/configuration/security",
//                baseUrl +"/swagger-ui.html",
//                baseUrl +"/webjars/**"));
//    }

//    @Bean
//    public CommandLineRunner initData(PasswordEncoder passwordEncoder) {
//        return args -> {
//            String pass = passwordEncoder.encode("");
//            System.out.println("Password: " + pass);
//        };
//    }

    @Bean
    public CommandLineRunner initData(RoleRepository roleRepository, UserRepository userRepository,
                                      PasswordEncoder passwordEncoder, LaboratoryRepository laboratoryRepository) {
        return args -> {
            // Create users if not already present
            if (!userRepository.existsByUserName("user")) {
                UserEntity user1 = new UserEntity(
                        "user",
                        passwordEncoder.encode("p"),
                        "user",
                        "UserLastName",
                        "user@localhost");
                userRepository.save(user1);
            }

            if (!userRepository.existsByUserName("seller1")) {
                UserEntity seller1 = new UserEntity(
                        "seller1",
                        passwordEncoder.encode("s"),
                        "seller",
                        "SellerLastName",
                        "seller1@localhost");
                userRepository.save(seller1);
            }

            if (!userRepository.existsByUserName("admin")) {
                UserEntity admin = new UserEntity(
                        "admin",
                        passwordEncoder.encode("a"),
                        "Admin",
                        "Boss",
                        "admin@localhost");
                userRepository.save(admin);
            }

            // Retrieve or create roles
            roleRepository.findByRoleName(AppRole.USER)
                    .orElseGet(() -> {
                        RoleEntity newUserRole = new RoleEntity(AppRole.USER);
                        return roleRepository.save(newUserRole);
                    });

            roleRepository.findByRoleName(AppRole.SELLER)
                    .orElseGet(() -> {
                        RoleEntity newSellerRole = new RoleEntity(AppRole.SELLER);
                        return roleRepository.save(newSellerRole);
                    });

            roleRepository.findByRoleName(AppRole.ADMIN)
                    .orElseGet(() -> {
                        RoleEntity newAdminRole = new RoleEntity(AppRole.ADMIN);
                        return roleRepository.save(newAdminRole);
                    });

            // Update roles for existing users
            userRepository.findByUserName("user1").ifPresent(user -> {
                user.setRoles(Set.of(roleRepository.findByRoleName(AppRole.USER).orElseThrow()));
                //user.setLanguage("de-DE");
                user.setLanguage("en-US");
                userRepository.save(user);
            });

            userRepository.findByUserName("seller1").ifPresent(seller -> {
                seller.setRoles(Set.of(roleRepository.findByRoleName(AppRole.SELLER).orElseThrow()));
                userRepository.save(seller);
            });

            userRepository.findByUserName("admin").ifPresent(admin -> {
                admin.setRoles(Set.of(
                        roleRepository.findByRoleName(AppRole.USER).orElseThrow(),
                        roleRepository.findByRoleName(AppRole.SELLER).orElseThrow(),
                        roleRepository.findByRoleName(AppRole.ADMIN).orElseThrow())
                );
                userRepository.save(admin);
            });

            for(int k = 0; k < 100; k++) {
                UserEntity user = new UserEntity(
                        "x_" + k,
                        passwordEncoder.encode("p"),
                        "x_fn_" + k,
                        "x_ln_" + k,
                        "user@localhost_" + k);
                userRepository.save(user);
            }

            UserEntity user = new UserEntity(
                    "x_" + 99999,
                    passwordEncoder.encode("p"),
                    "x_fn_" + 0,
                    "x_ln_" + 0,
                    "user@localhost_" + 0);
            userRepository.save(user);

            UserEntity admin = userRepository.findByUserName("admin").orElse(null);

            if (!laboratoryRepository.existsByName("Stöcken Mixing Lab")) {
                LaboratoryEntity entity = new LaboratoryEntity();
                entity.setName("Stöcken Mixing Lab");
                entity.setShortName("CU-MIX");
                entity.setLabDate(LocalDateTime.of(2021, 1, 1, 0, 0));
                entity.setLabSwitchOn(true);
                entity.setLabSwitchOff(false);
                entity.setLabUser(admin);
                laboratoryRepository.save(entity);
            }

            if (!laboratoryRepository.existsByName("GEP Mixing Lab")) {
                LaboratoryEntity entity = new LaboratoryEntity();
                entity.setName("GEP Mixing Lab");
                entity.setShortName("GEP-MIX");
                entity.setLabDate(LocalDateTime.of(2021, 1, 1, 0, 0));
                entity.setLabSwitchOn(true);
                entity.setLabSwitchOff(false);
                entity.setLabUser(admin);
                laboratoryRepository.save(entity);
            }
        };
    }

}