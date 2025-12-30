package de.conti.tires.mandi.backend.user;

import de.conti.tires.mandi.backend.core.exception.GenericException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {
    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Value("${spring.app.jwtRefreshExpirationMs}") // Default 24h if not set
    private Long refreshTokenDurationMs;

    public Optional<RefreshTokenEntity> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshTokenEntity verifyExpiration(RefreshTokenEntity token) {
        if (token.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);
            throw new GenericException(HttpStatus.UNAUTHORIZED, "Refresh token was expired. Please make a new login request");
        }

        return token;
    }

    @Transactional
    public void deleteByUserId(UUID userUuid) {
        userRepository.findById(userUuid).ifPresent(refreshTokenRepository::deleteByUser);
    }

    @Transactional
    public RefreshTokenEntity createRefreshToken(UUID userUuid) {

        UserEntity user = userRepository.findById(userUuid)
                .orElseThrow(() -> new RuntimeException("User not found with uuid: " + userUuid));

        // 1. Delete existing token for this user to avoid unique constraint violation
        refreshTokenRepository.deleteByUser(user);

        RefreshTokenEntity refreshToken = new RefreshTokenEntity();

        refreshToken.setUser(user);
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshToken.setToken(UUID.randomUUID().toString());

        refreshToken = refreshTokenRepository.save(refreshToken);
        return refreshToken;
    }
}
