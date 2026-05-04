package com.techmarket.iamservice.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.techmarket.iamservice.application.dto.AuthTokenResponse;
import com.techmarket.iamservice.application.dto.RegisterUserRequest;
import com.techmarket.iamservice.application.model.IamConstants;
import com.techmarket.iamservice.infrastructure.persistence.entity.UserEntity;
import com.techmarket.iamservice.infrastructure.persistence.repository.RefreshTokenRepository;
import com.techmarket.iamservice.infrastructure.persistence.repository.TenantUserRepository;
import com.techmarket.iamservice.infrastructure.persistence.repository.UserCredentialRepository;
import com.techmarket.iamservice.infrastructure.persistence.repository.UserScopeRepository;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class AuthServiceRegisterTest {

    @Mock private TenantUserRepository tenantUserRepository;
    @Mock private UserCredentialRepository userCredentialRepository;
    @Mock private RefreshTokenRepository refreshTokenRepository;
    @Mock private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;
    @Mock private JwtTokenService jwtTokenService;
    @Mock private AccessTokenRevocationService accessTokenRevocationService;
    @Mock private UserScopeRepository userScopeRepository;
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
                        auditTrailService);
    }

    @Test
    void shouldCreatePublicUserAndIssueTokenPair() {
        RegisterUserRequest request =
                new RegisterUserRequest(
                        "usuario@example.com",
                        "SecurePass123!",
                        "SecurePass123!",
                        "cliente",
                        "Juan",
                        "Pérez",
                        "+56912345678",
                        "Bolivia",
                        "Santa Cruz",
                        true);

        when(tenantUserRepository.existsByTenantIdAndEmail(
                        IamConstants.GLOBAL_TENANT_ID, "usuario@example.com"))
                .thenReturn(false);
        when(tenantUserRepository.existsByTenantIdAndUsername(
                        IamConstants.GLOBAL_TENANT_ID, "usuario@example.com"))
                .thenReturn(false);
        when(tenantUserRepository.save(any(UserEntity.class)))
                .thenAnswer(
                        invocation -> {
                            UserEntity user = invocation.getArgument(0);
                            ReflectionTestUtils.setField(user, "id", 77L);
                            return user;
                        });
        when(passwordEncoder.encode("SecurePass123!")).thenReturn("encoded-password");
        when(userCredentialRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(userScopeRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(jwtTokenService.generateAccessToken(
                        anyLong(), anyString(), anyString(), anyList(), anyList(), anyList()))
                .thenReturn(
                        new JwtTokenService.GeneratedToken(
                                "access-token",
                                "access-id",
                                Instant.parse("2026-05-04T12:00:00Z")));
        when(jwtTokenService.generateRefreshToken(anyLong(), anyString()))
                .thenReturn(
                        new JwtTokenService.GeneratedToken(
                                "refresh-token",
                                "refresh-id",
                                Instant.parse("2026-05-11T12:00:00Z")));

        AuthTokenResponse response = authService.register(request);

        ArgumentCaptor<UserEntity> userCaptor = ArgumentCaptor.forClass(UserEntity.class);
        verify(tenantUserRepository).save(userCaptor.capture());

        UserEntity savedUser = userCaptor.getValue();
        assertThat(savedUser.getTenantId()).isEqualTo(IamConstants.GLOBAL_TENANT_ID);
        assertThat(savedUser.getUsername()).isEqualTo("usuario@example.com");
        assertThat(savedUser.getEmail()).isEqualTo("usuario@example.com");
        assertThat(savedUser.getFirstName()).isEqualTo("Juan");
        assertThat(savedUser.getLastName()).isEqualTo("Pérez");
        assertThat(savedUser.getPhone()).isEqualTo("+56912345678");
        assertThat(savedUser.getCountry()).isEqualTo("Bolivia");
        assertThat(savedUser.getCity()).isEqualTo("Santa Cruz");
        assertThat(savedUser.getUserType()).isEqualTo("cliente");
        assertThat(savedUser.isTermsAccepted()).isTrue();

        assertThat(response.accessToken()).isEqualTo("access-token");
        assertThat(response.refreshToken()).isEqualTo("refresh-token");
        assertThat(response.tenantId()).isEqualTo(IamConstants.GLOBAL_TENANT_ID);
        assertThat(response.userId()).isEqualTo(77L);
        assertThat(response.username()).isEqualTo("usuario@example.com");
        assertThat(response.roles()).isEmpty();
    }
}
