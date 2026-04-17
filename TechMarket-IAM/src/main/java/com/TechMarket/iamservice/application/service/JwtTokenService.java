package com.techmarket.iamservice.application.service;

import com.techmarket.iamservice.application.exception.AuthServiceException;
import com.techmarket.iamservice.config.security.TokenProperties;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;

@Service
public class JwtTokenService {

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    private final TokenProperties tokenProperties;

    public JwtTokenService(
            JwtEncoder jwtEncoder, JwtDecoder jwtDecoder, TokenProperties tokenProperties) {
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
        this.tokenProperties = tokenProperties;
    }

    public GeneratedToken generateAccessToken(
            Long userId,
            String username,
            String tenantId,
            List<String> roles,
            List<String> authorizationScopes,
            List<String> userScopes) {
        Instant issuedAt = Instant.now();
        Instant expiresAt =
                issuedAt.plus(
                        Duration.ofMinutes(Math.max(1, tokenProperties.accessTokenMinutes())));
        String tokenId = UUID.randomUUID().toString();

        JwtClaimsSet claims =
                JwtClaimsSet.builder()
                        .id(tokenId)
                        .subject(userId.toString())
                        .issuer(tokenProperties.issuer())
                        .issuedAt(issuedAt)
                        .expiresAt(expiresAt)
                        .claim("token_type", "access")
                        .claim("tenant_id", tenantId)
                        .claim("username", username)
                        .claim("roles", roles)
                        .claim("scope", String.join(" ", authorizationScopes))
                        .claim("scopes", userScopes)
                        .build();

        String tokenValue =
                jwtEncoder
                        .encode(
                                JwtEncoderParameters.from(
                                        JwsHeader.with(MacAlgorithm.HS256).build(), claims))
                        .getTokenValue();

        return new GeneratedToken(tokenValue, tokenId, expiresAt);
    }

    public GeneratedToken generateRefreshToken(Long userId, String tenantId) {
        Instant issuedAt = Instant.now();
        Instant expiresAt =
                issuedAt.plus(Duration.ofDays(Math.max(1, tokenProperties.refreshTokenDays())));
        String tokenId = UUID.randomUUID().toString();

        JwtClaimsSet claims =
                JwtClaimsSet.builder()
                        .id(tokenId)
                        .subject(userId.toString())
                        .issuer(tokenProperties.issuer())
                        .issuedAt(issuedAt)
                        .expiresAt(expiresAt)
                        .claim("token_type", "refresh")
                        .claim("tenant_id", tenantId)
                        .build();

        String tokenValue =
                jwtEncoder
                        .encode(
                                JwtEncoderParameters.from(
                                        JwsHeader.with(MacAlgorithm.HS256).build(), claims))
                        .getTokenValue();

        return new GeneratedToken(tokenValue, tokenId, expiresAt);
    }

    public RefreshTokenClaims parseRefreshToken(String refreshToken) {
        try {
            Jwt jwt = jwtDecoder.decode(refreshToken);
            String tokenType = jwt.getClaimAsString("token_type");
            if (!"refresh".equals(tokenType)) {
                throw new AuthServiceException(
                        "IAM_INVALID_REFRESH_TOKEN",
                        HttpStatus.UNAUTHORIZED,
                        "Provided token is not a refresh token");
            }

            String tokenId = jwt.getId();
            String tenantId = jwt.getClaimAsString("tenant_id");
            Long userId = Long.valueOf(jwt.getSubject());

            if (tokenId == null || tenantId == null || userId == null) {
                throw new AuthServiceException(
                        "IAM_INVALID_REFRESH_TOKEN",
                        HttpStatus.UNAUTHORIZED,
                        "Refresh token does not contain required claims");
            }

            try {
                tenantId = UUID.fromString(tenantId).toString();
            } catch (IllegalArgumentException ex) {
                throw new AuthServiceException(
                        "IAM_INVALID_REFRESH_TOKEN",
                        HttpStatus.UNAUTHORIZED,
                        "Refresh token tenant_id claim is invalid");
            }

            return new RefreshTokenClaims(tokenId, userId, tenantId);
        } catch (JwtException | IllegalArgumentException ex) {
            throw new AuthServiceException(
                    "IAM_INVALID_REFRESH_TOKEN",
                    HttpStatus.UNAUTHORIZED,
                    "Refresh token is invalid or expired");
        }
    }

    public AccessTokenClaims parseAccessToken(String accessToken) {
        try {
            Jwt jwt = jwtDecoder.decode(accessToken);
            String tokenType = jwt.getClaimAsString("token_type");
            if (!"access".equals(tokenType)) {
                throw new AuthServiceException(
                        "IAM_INVALID_ACCESS_TOKEN",
                        HttpStatus.UNAUTHORIZED,
                        "Provided token is not an access token");
            }

            String tokenId = jwt.getId();
            String tenantId = jwt.getClaimAsString("tenant_id");
            Long userId = Long.valueOf(jwt.getSubject());
            Instant expiresAt = jwt.getExpiresAt();

            if (tokenId == null || tenantId == null || userId == null || expiresAt == null) {
                throw new AuthServiceException(
                        "IAM_INVALID_ACCESS_TOKEN",
                        HttpStatus.UNAUTHORIZED,
                        "Access token does not contain required claims");
            }

            try {
                tenantId = UUID.fromString(tenantId).toString();
            } catch (IllegalArgumentException ex) {
                throw new AuthServiceException(
                        "IAM_INVALID_ACCESS_TOKEN",
                        HttpStatus.UNAUTHORIZED,
                        "Access token tenant_id claim is invalid");
            }

            return new AccessTokenClaims(tokenId, userId, tenantId, expiresAt);
        } catch (JwtException | IllegalArgumentException ex) {
            throw new AuthServiceException(
                    "IAM_INVALID_ACCESS_TOKEN",
                    HttpStatus.UNAUTHORIZED,
                    "Access token is invalid or expired");
        }
    }

    public record GeneratedToken(String value, String tokenId, Instant expiresAt) {}

    public record RefreshTokenClaims(String tokenId, Long userId, String tenantId) {}

    public record AccessTokenClaims(
            String tokenId, Long userId, String tenantId, Instant expiresAt) {}
}
