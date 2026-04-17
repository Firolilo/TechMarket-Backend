package com.techmarket.iamservice.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

class TenantAuthorizationServiceTest {

    private TenantAuthorizationService tenantAuthorizationService;

    @BeforeEach
    void setUp() {
        tenantAuthorizationService = new TenantAuthorizationService(new PrincipalAccessService());
    }

    @Test
    void shouldAllowGlobalAdminToReadWithoutTenantHeader() {
        Authentication authentication =
                jwtAuth("11111111-1111-1111-1111-111111111111", List.of("ADMIN_GLOBAL"), "10");

        String resolved = tenantAuthorizationService.resolveTenantForRead(null, authentication);

        assertThat(resolved).isNull();
    }

    @Test
    void shouldUseTokenTenantWhenHeaderMissingForNonGlobalAdmin() {
        Authentication authentication =
                jwtAuth("22222222-2222-2222-2222-222222222222", List.of("Docente"), "20");

        String resolved = tenantAuthorizationService.resolveTenantForRead(null, authentication);

        assertThat(resolved).isEqualTo("22222222-2222-2222-2222-222222222222");
    }

    @Test
    void shouldRejectMismatchedTenantHeaderForNonGlobalAdmin() {
        Authentication authentication =
                jwtAuth("33333333-3333-3333-3333-333333333333", List.of("Docente"), "30");

        assertThatThrownBy(
                        () ->
                                tenantAuthorizationService.resolveTenantForRead(
                                        "44444444-4444-4444-4444-444444444444", authentication))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void shouldAllowHeaderOnlyWriteWhenAuthenticationTokenIsMissing() {
        String resolved =
                tenantAuthorizationService.resolveTenantForWrite(
                        "55555555-5555-5555-5555-555555555555", null, null);

        assertThat(resolved).isEqualTo("55555555-5555-5555-5555-555555555555");
    }

    private Authentication jwtAuth(String tenantId, List<String> roles, String subject) {
        Instant now = Instant.now();
        Jwt jwt =
                Jwt.withTokenValue("token")
                        .header("alg", "HS256")
                        .issuer("iam-service")
                        .issuedAt(now)
                        .expiresAt(now.plusSeconds(3600))
                        .subject(subject)
                        .claim("tenant_id", tenantId)
                        .claim("roles", roles)
                        .build();
        return new JwtAuthenticationToken(jwt);
    }
}
