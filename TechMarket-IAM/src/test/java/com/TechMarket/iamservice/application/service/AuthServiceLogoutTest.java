package com.techmarket.iamservice.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.techmarket.iamservice.application.dto.RefreshTokenRequest;
import com.techmarket.iamservice.application.model.IamConstants;
import com.techmarket.iamservice.infrastructure.persistence.entity.RefreshTokenEntity;
import com.techmarket.iamservice.infrastructure.persistence.repository.RefreshTokenRepository;
import com.techmarket.iamservice.infrastructure.persistence.repository.TenantRoleRepository;
import com.techmarket.iamservice.infrastructure.persistence.repository.TenantUserRepository;
import com.techmarket.iamservice.infrastructure.persistence.repository.UserCredentialRepository;
import com.techmarket.iamservice.infrastructure.persistence.repository.UserScopeRepository;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

@ExtendWith(MockitoExtension.class)
class AuthServiceLogoutTest {

    @Mock private TenantUserRepository tenantUserRepository;
    @Mock private UserCredentialRepository userCredentialRepository;
    @Mock private RefreshTokenRepository refreshTokenRepository;
    @Mock private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;
    @Mock private JwtTokenService jwtTokenService;
    @Mock private AccessTokenRevocationService accessTokenRevocationService;
    @Mock private UserScopeRepository userScopeRepository;
    @Mock private TenantRoleRepository tenantRoleRepository;
    @Mock private AuditTrailService auditTrailService;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService =
                new AuthService(
                        tenantUserRepository,
                        userCredentialRepository,
                        refreshTokenRepository,
                        passwordEncoder,
                        jwtTokenService,
                        accessTokenRevocationService,
                        userScopeRepository,
                        tenantRoleRepository,
                        auditTrailService);
    }

    @Test
    void shouldRevokeRefreshAndAccessTokensForAuthenticatedUser() {
        String tenantId = IamConstants.GLOBAL_TENANT_ID;
        RefreshTokenEntity persistedToken =
                new RefreshTokenEntity(
                        "refresh-id", 77L, tenantId, LocalDateTime.of(2026, 5, 11, 12, 0, 0));

        when(jwtTokenService.parseRefreshToken("refresh-token"))
                .thenReturn(new JwtTokenService.RefreshTokenClaims("refresh-id", 77L, tenantId));
        when(refreshTokenRepository.findByTokenIdAndTenantIdAndRevokedFalse("refresh-id", tenantId))
                .thenReturn(Optional.of(persistedToken));

        Authentication authentication =
                jwtAuth("access-id", "77", tenantId, Instant.parse("2026-05-04T12:30:00Z"));

        authService.logout(
                new RefreshTokenRequest("refresh-token"), authentication, "Bearer access-token");

        ArgumentCaptor<RefreshTokenEntity> tokenCaptor =
                ArgumentCaptor.forClass(RefreshTokenEntity.class);
        verify(refreshTokenRepository).save(tokenCaptor.capture());
        assertThat(tokenCaptor.getValue().isRevoked()).isTrue();

        verify(accessTokenRevocationService)
                .revoke("access-id", Instant.parse("2026-05-04T12:30:00Z"));
        verify(auditTrailService).record("AUTH_LOGOUT", "User", "77", tenantId, "77");
    }

    @Test
    void shouldRevokeAccessTokenFromAuthorizationHeaderWhenJwtAuthenticationIsMissing() {
        String tenantId = IamConstants.GLOBAL_TENANT_ID;
        RefreshTokenEntity persistedToken =
                new RefreshTokenEntity(
                        "refresh-id", 77L, tenantId, LocalDateTime.of(2026, 5, 11, 12, 0, 0));

        when(jwtTokenService.parseRefreshToken("refresh-token"))
                .thenReturn(new JwtTokenService.RefreshTokenClaims("refresh-id", 77L, tenantId));
        when(refreshTokenRepository.findByTokenIdAndTenantIdAndRevokedFalse("refresh-id", tenantId))
                .thenReturn(Optional.of(persistedToken));
        when(jwtTokenService.parseAccessToken("access-token"))
                .thenReturn(
                        new JwtTokenService.AccessTokenClaims(
                                "access-id", 77L, tenantId, Instant.parse("2026-05-04T12:30:00Z")));

        authService.logout(new RefreshTokenRequest("refresh-token"), null, "Bearer access-token");

        verify(accessTokenRevocationService)
                .revoke("access-id", Instant.parse("2026-05-04T12:30:00Z"));
        verify(auditTrailService).record("AUTH_LOGOUT", "User", "77", tenantId, "77");
    }

    private Authentication jwtAuth(
            String tokenId, String subject, String tenantId, Instant expiresAt) {
        Jwt jwt =
                Jwt.withTokenValue("access-token")
                        .header("alg", "HS256")
                        .issuer("iam-service")
                        .claim("jti", tokenId)
                        .issuedAt(Instant.parse("2026-05-04T12:00:00Z"))
                        .expiresAt(expiresAt)
                        .subject(subject)
                        .claim("tenant_id", tenantId)
                        .claim("token_type", "access")
                        .build();
        return new JwtAuthenticationToken(jwt);
    }
}
