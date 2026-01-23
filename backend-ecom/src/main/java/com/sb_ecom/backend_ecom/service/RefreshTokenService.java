package com.sb_ecom.backend_ecom.service;

import com.sb_ecom.backend_ecom.exceptions.TokenRefreshException;
import com.sb_ecom.backend_ecom.model.RefreshToken;
import com.sb_ecom.backend_ecom.model.User;
import com.sb_ecom.backend_ecom.repo.RefreshTokenRepository;
import com.sb_ecom.backend_ecom.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    @Value("${spring.app.jwtRefreshExpirationMs}")
    private Long refreshTokenDurationMs;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Transactional
    public RefreshToken createRefreshToken(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        // Check if existing refresh token exists for the user
        Optional<RefreshToken> existingToken = refreshTokenRepository.findByUser(user);
        
        if (existingToken.isPresent()) {
            // Update existing token instead of delete + create
            RefreshToken tokenToUpdate = existingToken.get();
            tokenToUpdate.setToken(UUID.randomUUID().toString());
            tokenToUpdate.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
            return refreshTokenRepository.save(tokenToUpdate);
        }

        // Create new token if none exists
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .expiryDate(Instant.now().plusMillis(refreshTokenDurationMs))
                .token(UUID.randomUUID().toString())
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException(token.getToken(), 
                "Refresh token was expired. Please make a new signin request");
        }
        return token;
    }

    @Transactional
    public int deleteByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        return refreshTokenRepository.deleteByUser(user);
    }

    @Transactional
    public void deleteByUser(User user) {
        refreshTokenRepository.findByUser(user).ifPresent(refreshTokenRepository::delete);
    }
}

