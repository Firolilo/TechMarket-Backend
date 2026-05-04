package com.techmarket.iamservice.application.service;

import com.techmarket.iamservice.api.exception.ErrorCodes;
import com.techmarket.iamservice.application.dto.AuthTokenResponse;
import com.techmarket.iamservice.application.dto.LoginRequest;
import com.techmarket.iamservice.application.dto.RefreshTokenRequest;
import com.techmarket.iamservice.application.dto.RegisterUserRequest;
import com.techmarket.iamservice.application.exception.AuthServiceException;
import com.techmarket.iamservice.application.model.IamConstants;
import com.techmarket.iamservice.application.model.UserScopeType;
import com.techmarket.iamservice.infrastructure.persistence.entity.RefreshTokenEntity;
import com.techmarket.iamservice.infrastructure.persistence.entity.RoleEntity;
import com.techmarket.iamservice.infrastructure.persistence.entity.UserCredentialEntity;
import com.techmarket.iamservice.infrastructure.persistence.entity.UserEntity;
import com.techmarket.iamservice.infrastructure.persistence.entity.UserScopeEntity;
import com.techmarket.iamservice.infrastructure.persistence.repository.RefreshTokenRepository;
import com.techmarket.iamservice.infrastructure.persistence.repository.TenantUserRepository;
import com.techmarket.iamservice.infrastructure.persistence.repository.UserCredentialRepository;
import com.techmarket.iamservice.infrastructure.persistence.repository.UserScopeRepository;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final TenantUserRepository tenantUserRepository;
    private final UserCredentialRepository userCredentialRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;
    private final AccessTokenRevocationService accessTokenRevocationService;
    private final UserScopeRepository userScopeRepository;
    private final AuditTrailService auditTrailService;

    public AuthService(
            TenantUserRepository tenantUserRepository,
            UserCredentialRepository userCredentialRepository,
            RefreshTokenRepository refreshTokenRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenService jwtTokenService,
            AccessTokenRevocationService accessTokenRevocationService,
            UserScopeRepository userScopeRepository,
            AuditTrailService auditTrailService) {
        this.tenantUserRepository = tenantUserRepository;
        this.userCredentialRepository = userCredentialRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
        this.accessTokenRevocationService = accessTokenRevocationService;
        this.userScopeRepository = userScopeRepository;
        this.auditTrailService = auditTrailService;
    }

    @Transactional
    public AuthTokenResponse login(String tenantId, LoginRequest request) {
        String normalizedTenantId = normalizeTenantId(tenantId);

        UserEntity user =
                tenantUserRepository
                        .findByTenantIdAndUsername(normalizedTenantId, request.username())
                        .orElseThrow(
                                () ->
                                        new AuthServiceException(
                                                ErrorCodes.IAM_INVALID_CREDENTIALS,
                                                HttpStatus.UNAUTHORIZED,
                                                "Invalid username or password"));

        UserCredentialEntity credential =
                userCredentialRepository
                        .findByUserIdAndTenantId(user.getId(), normalizedTenantId)
                        .orElseThrow(
                                () ->
                                        new AuthServiceException(
                                                ErrorCodes.IAM_INVALID_CREDENTIALS,
                                                HttpStatus.UNAUTHORIZED,
                                                "Invalid username or password"));

        if (!passwordEncoder.matches(request.password(), credential.getPasswordHash())) {
            throw new AuthServiceException(
                    ErrorCodes.IAM_INVALID_CREDENTIALS,
                    HttpStatus.UNAUTHORIZED,
                    "Invalid username or password");
        }

        if (!user.isActive()) {
            throw new AuthServiceException(
                    ErrorCodes.IAM_USER_INACTIVE,
                    HttpStatus.UNAUTHORIZED,
                    "User is inactive for this tenant");
        }

        AuthTokenResponse response = issueTokenPair(user, normalizedTenantId);
        auditTrailService.record(
                "AUTH_LOGIN",
                "User",
                user.getId().toString(),
                normalizedTenantId,
                user.getId().toString());
        return response;
    }

    @Transactional
    public AuthTokenResponse register(RegisterUserRequest request) {
        String normalizedEmail = normalizeEmail(request.email());
        if (tenantUserRepository.existsByTenantIdAndEmail(
                        IamConstants.GLOBAL_TENANT_ID, normalizedEmail)
                || tenantUserRepository.existsByTenantIdAndUsername(
                        IamConstants.GLOBAL_TENANT_ID, normalizedEmail)) {
            throw new AuthServiceException(
                    "IAM_USER_ALREADY_EXISTS",
                    HttpStatus.CONFLICT,
                    "User already exists for the public tenant");
        }

        if (!request.password().equals(request.confirmPassword())) {
            throw new IllegalArgumentException("password and confirmPassword must match");
        }

        UserEntity user = new UserEntity(normalizedEmail, normalizedEmail, true);
        user.setTenantId(IamConstants.GLOBAL_TENANT_ID);
        user.setFirstName(trimToNull(request.nombre()));
        user.setLastName(trimToNull(request.apellido()));
        user.setPhone(trimToNull(request.telefono()));
        user.setCountry(trimToNull(request.pais()));
        user.setCity(trimToNull(request.ciudad()));
        user.setUserType(trimToNull(request.tipo()));
        user.setTermsAccepted(request.terminos());
        user.assignRoles(Set.of());

        UserEntity savedUser = tenantUserRepository.save(user);

        userCredentialRepository.save(
                new UserCredentialEntity(
                        savedUser.getId(),
                        IamConstants.GLOBAL_TENANT_ID,
                        passwordEncoder.encode(request.password())));

        userScopeRepository.save(
                new UserScopeEntity(
                        IamConstants.GLOBAL_TENANT_ID,
                        savedUser,
                        null,
                        UserScopeType.GLOBAL.name()));

        auditTrailService.record(
                "AUTH_REGISTER",
                "User",
                savedUser.getId().toString(),
                IamConstants.GLOBAL_TENANT_ID,
                savedUser.getId().toString());

        return issueTokenPair(savedUser, IamConstants.GLOBAL_TENANT_ID);
    }

    @Transactional
    public AuthTokenResponse refresh(RefreshTokenRequest request) {
        JwtTokenService.RefreshTokenClaims refreshClaims =
                jwtTokenService.parseRefreshToken(request.refreshToken());

        RefreshTokenEntity persistedToken =
                refreshTokenRepository
                        .findByTokenIdAndTenantIdAndRevokedFalse(
                                refreshClaims.tokenId(), refreshClaims.tenantId())
                        .orElseThrow(
                                () ->
                                        new AuthServiceException(
                                                ErrorCodes.IAM_INVALID_REFRESH_TOKEN,
                                                HttpStatus.UNAUTHORIZED,
                                                "Refresh token is invalid or revoked"));

        if (persistedToken.getExpiresAt().isBefore(LocalDateTime.now(ZoneOffset.UTC))) {
            persistedToken.revoke();
            refreshTokenRepository.save(persistedToken);
            throw new AuthServiceException(
                    ErrorCodes.IAM_REFRESH_TOKEN_EXPIRED,
                    HttpStatus.UNAUTHORIZED,
                    "Refresh token expired");
        }

        persistedToken.revoke();
        refreshTokenRepository.save(persistedToken);

        UserEntity user =
                tenantUserRepository
                        .findByIdAndTenantId(refreshClaims.userId(), refreshClaims.tenantId())
                        .orElseThrow(
                                () ->
                                        new AuthServiceException(
                                                ErrorCodes.IAM_USER_NOT_FOUND,
                                                HttpStatus.UNAUTHORIZED,
                                                "User not found for refresh token"));

        AuthTokenResponse response = issueTokenPair(user, refreshClaims.tenantId());
        auditTrailService.record(
                "AUTH_REFRESH",
                "User",
                user.getId().toString(),
                refreshClaims.tenantId(),
                user.getId().toString());
        return response;
    }

    @Transactional
    public void logout(
            RefreshTokenRequest request,
            Authentication authentication,
            String authorizationHeader) {
        JwtTokenService.RefreshTokenClaims refreshClaims =
                jwtTokenService.parseRefreshToken(request.refreshToken());

        refreshTokenRepository
                .findByTokenIdAndTenantIdAndRevokedFalse(
                        refreshClaims.tokenId(), refreshClaims.tenantId())
                .ifPresent(
                        token -> {
                            token.revoke();
                            refreshTokenRepository.save(token);
                        });

        if (authentication instanceof JwtAuthenticationToken jwtAuthenticationToken) {
            String tokenId = jwtAuthenticationToken.getToken().getId();
            java.time.Instant expiresAt = jwtAuthenticationToken.getToken().getExpiresAt();
            accessTokenRevocationService.revoke(tokenId, expiresAt);
            String tenantId = jwtAuthenticationToken.getToken().getClaimAsString("tenant_id");
            String userId = jwtAuthenticationToken.getToken().getSubject();
            auditTrailService.record("AUTH_LOGOUT", "User", userId, tenantId, userId);
            return;
        }

        String accessToken = extractBearerToken(authorizationHeader);
        if (accessToken != null) {
            JwtTokenService.AccessTokenClaims accessClaims =
                    jwtTokenService.parseAccessToken(accessToken);
            accessTokenRevocationService.revoke(accessClaims.tokenId(), accessClaims.expiresAt());
            auditTrailService.record(
                    "AUTH_LOGOUT",
                    "User",
                    accessClaims.userId().toString(),
                    accessClaims.tenantId(),
                    accessClaims.userId().toString());
        }
    }

    @Transactional
    public void logoutAll(Authentication authentication, String authorizationHeader) {
        JwtTokenService.AccessTokenClaims accessClaims =
                resolveAccessClaims(authentication, authorizationHeader);

        List<RefreshTokenEntity> refreshTokens =
                refreshTokenRepository.findAllByUserIdAndTenantIdAndRevokedFalse(
                        accessClaims.userId(), accessClaims.tenantId());
        if (!refreshTokens.isEmpty()) {
            refreshTokens.forEach(RefreshTokenEntity::revoke);
            refreshTokenRepository.saveAll(refreshTokens);
        }

        accessTokenRevocationService.revoke(accessClaims.tokenId(), accessClaims.expiresAt());
        auditTrailService.record(
                "AUTH_LOGOUT_ALL",
                "User",
                accessClaims.userId().toString(),
                accessClaims.tenantId(),
                accessClaims.userId().toString());
    }

    private AuthTokenResponse issueTokenPair(UserEntity user, String tenantId) {
        List<String> roles = user.getRoles().stream().map(RoleEntity::getName).sorted().toList();
        List<String> userScopes = resolveUserScopes(user.getId(), tenantId);
        List<String> authorizationScopes = resolveAuthorizationScopes(user.getRoles());

        JwtTokenService.GeneratedToken accessToken =
                jwtTokenService.generateAccessToken(
                        user.getId(),
                        user.getUsername(),
                        tenantId,
                        roles,
                        authorizationScopes,
                        userScopes);
        JwtTokenService.GeneratedToken refreshToken =
                jwtTokenService.generateRefreshToken(user.getId(), tenantId);

        RefreshTokenEntity refreshTokenEntity =
                new RefreshTokenEntity(
                        refreshToken.tokenId(),
                        user.getId(),
                        tenantId,
                        LocalDateTime.ofInstant(refreshToken.expiresAt(), ZoneOffset.UTC));
        refreshTokenRepository.save(refreshTokenEntity);

        long accessTokenTtl =
                Math.max(
                        1,
                        accessToken.expiresAt().getEpochSecond()
                                - java.time.Instant.now().getEpochSecond());
        long refreshTokenTtl =
                Math.max(
                        1,
                        refreshToken.expiresAt().getEpochSecond()
                                - java.time.Instant.now().getEpochSecond());

        return new AuthTokenResponse(
                accessToken.value(),
                refreshToken.value(),
                "Bearer",
                accessTokenTtl,
                refreshTokenTtl,
                tenantId,
                user.getId(),
                user.getUsername(),
                roles,
                userScopes);
    }

    private List<String> resolveUserScopes(Long userId, String tenantId) {
        List<UserScopeEntity> scopes =
                userScopeRepository.findAllByUserIdAndTenantId(userId, tenantId);
        if (scopes.isEmpty()) {
            return List.of("GLOBAL");
        }

        Set<String> values = new TreeSet<>();
        for (UserScopeEntity scope : scopes) {
            if ("GLOBAL".equalsIgnoreCase(scope.getScopeType())) {
                values.clear();
                values.add("GLOBAL");
                break;
            }
            if (scope.getBranch() != null && scope.getBranch().getCode() != null) {
                values.add(scope.getBranch().getCode());
            }
        }

        if (values.isEmpty()) {
            values.add("GLOBAL");
        }
        return new ArrayList<>(values);
    }

    private List<String> resolveAuthorizationScopes(Collection<RoleEntity> roles) {
        Set<String> scopes = new TreeSet<>();
        if (roles != null) {
            for (RoleEntity role : roles) {
                if (role == null) {
                    continue;
                }
                if (IamConstants.GLOBAL_ADMIN_ROLE.equalsIgnoreCase(role.getName())) {
                    scopes.add("iam.users.read");
                    scopes.add("iam.users.write");
                    scopes.add("iam.roles.read");
                    scopes.add("iam.roles.write");
                    scopes.add("iam.permissions.read");
                    scopes.add("iam.permissions.write");
                    scopes.add("iam.branches.read");
                    scopes.add("iam.branches.write");
                    scopes.add("operations.read");
                }

                if (role.getPermissions() == null) {
                    continue;
                }
                role.getPermissions()
                        .forEach(
                                permission -> {
                                    if (permission.getModule() == null
                                            || permission.getAction() == null) {
                                        return;
                                    }
                                    String module =
                                            normalizeScopeToken(permission.getModule().getCode());
                                    String action =
                                            normalizeScopeToken(permission.getAction().getCode());
                                    if (module.isBlank() || action.isBlank()) {
                                        return;
                                    }
                                    scopes.add(module + "." + action);
                                });
            }
        }
        return new ArrayList<>(scopes);
    }

    private String normalizeScopeToken(String token) {
        if (token == null) {
            return "";
        }
        return token.trim().toLowerCase().replaceAll("[^a-z0-9._-]", "");
    }

    private String normalizeTenantId(String tenantId) {
        if (tenantId == null || tenantId.isBlank()) {
            throw new AuthServiceException(
                    ErrorCodes.IAM_TENANT_REQUIRED,
                    HttpStatus.BAD_REQUEST,
                    "X-Tenant-Id header is required");
        }

        String normalized = tenantId.trim();
        try {
            return UUID.fromString(normalized).toString();
        } catch (IllegalArgumentException ex) {
            throw new AuthServiceException(
                    ErrorCodes.IAM_TENANT_INVALID,
                    HttpStatus.BAD_REQUEST,
                    "X-Tenant-Id must be a valid UUID");
        }
    }

    private String normalizeEmail(String email) {
        if (email == null) {
            return null;
        }
        return email.trim().toLowerCase();
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isBlank() ? null : trimmed;
    }

    private String extractBearerToken(String authorizationHeader) {
        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            return null;
        }

        String token = authorizationHeader.trim();
        while (token.regionMatches(true, 0, "Bearer ", 0, "Bearer ".length())) {
            token = token.substring("Bearer ".length()).trim();
        }

        return token.isBlank() ? null : token;
    }

    private JwtTokenService.AccessTokenClaims resolveAccessClaims(
            Authentication authentication, String authorizationHeader) {
        if (authentication instanceof JwtAuthenticationToken jwtAuthenticationToken) {
            String tokenId = jwtAuthenticationToken.getToken().getId();
            java.time.Instant expiresAt = jwtAuthenticationToken.getToken().getExpiresAt();
            String tenantId = jwtAuthenticationToken.getToken().getClaimAsString("tenant_id");
            Long userId = Long.valueOf(jwtAuthenticationToken.getToken().getSubject());

            if (tokenId == null || expiresAt == null || tenantId == null || userId == null) {
                throw new AuthServiceException(
                        "IAM_INVALID_ACCESS_TOKEN",
                        HttpStatus.UNAUTHORIZED,
                        "Access token does not contain required claims");
            }

            return new JwtTokenService.AccessTokenClaims(tokenId, userId, tenantId, expiresAt);
        }

        String accessToken = extractBearerToken(authorizationHeader);
        if (accessToken == null) {
            throw new AuthServiceException(
                    "IAM_INVALID_ACCESS_TOKEN",
                    HttpStatus.UNAUTHORIZED,
                    "Access token is required for logout-all");
        }

        return jwtTokenService.parseAccessToken(accessToken);
    }
}
