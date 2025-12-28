package com.example.demo.auth.refreshToken;

import com.example.demo.exceptions.customHandlers.RefreshTokenExpired;
import com.example.demo.exceptions.customHandlers.RefreshTokenReuseDetected;
import com.example.demo.exceptions.customHandlers.ResourseNotFound;
import com.example.demo.user.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

//    @Value("${REFRESH_TOKEN_VALIDITY_DAYS}")
    private final long refreshTokenValidity = 7;


    public String createRefreshToken(User user){

        String rawToken = generateRawToken();
        String hashedToken = hashToken(rawToken);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setTokenHash(hashedToken);
        refreshToken.setExpiresAt(
                Instant.now().plusSeconds(refreshTokenValidity * 24 * 60 * 60)
        );
        refreshTokenRepository.save(refreshToken);
        return rawToken;
    }

    public RefreshToken validateRefreshToken(String rawToken) throws ResourseNotFound, RefreshTokenExpired, RefreshTokenReuseDetected {

        String hashedToken = hashToken(rawToken);

        RefreshToken refreshToken = refreshTokenRepository.findByTokenHash(hashedToken)
                .orElseThrow(() -> new ResourseNotFound("Refresh Token "));

        if (refreshToken.isRevoked() || refreshToken.getLastUsedAt() != null){
            revokeAllUserTokens(refreshToken.getUser().getId());
            throw new RefreshTokenReuseDetected();
        }

        if(refreshToken.getExpiresAt().isBefore(Instant.now())){
            throw new RefreshTokenExpired();
        }

        refreshToken.setLastUsedAt(Instant.now());
        refreshTokenRepository.save(refreshToken);

        return refreshToken;
    }

    public String rotateRefreshToken(RefreshToken oldToken){
        revokeToken(oldToken);

        return createRefreshToken(oldToken.getUser());
    }

    public void revokeToken(RefreshToken token){
        token.setRevoked(true);
        refreshTokenRepository.save(token);
    }

    public void revokeAllUserTokens(Long userId) {
        refreshTokenRepository.deleteAllByUserId(userId);
    }

    private String generateRawToken(){
        return UUID.randomUUID().toString();
    }

    private String hashToken(String token){
        try {

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return byteToHex(hash);

        }catch (Exception ex){
            throw new IllegalStateException("Could not hash refresh token",ex);
        }
    }

    private String byteToHex(byte[] bytes){
        StringBuilder hex = new StringBuilder();
        for (byte b : bytes){
            hex.append(String.format("%02x", b));
        }
        return hex.toString();
    }

}
