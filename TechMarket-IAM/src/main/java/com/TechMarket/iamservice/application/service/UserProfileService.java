package com.techmarket.iamservice.application.service;

import com.techmarket.core.iam.infrastructure.persistence.entity.RoleJpaEntity;
import com.techmarket.core.iam.infrastructure.persistence.entity.UserJpaEntity;
import com.techmarket.iamservice.application.dto.PublicUserProfileResponse;
import com.techmarket.iamservice.application.dto.UpdateProfileRequest;
import com.techmarket.iamservice.application.dto.UserProfileResponse;
import com.techmarket.iamservice.application.exception.AuthServiceException;
import com.techmarket.iamservice.infrastructure.persistence.repository.TenantUserRepository;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserProfileService {

    private final TenantUserRepository tenantUserRepository;
    private final PrincipalAccessService principalAccessService;

    public UserProfileService(
            TenantUserRepository tenantUserRepository,
            PrincipalAccessService principalAccessService) {
        this.tenantUserRepository = tenantUserRepository;
        this.principalAccessService = principalAccessService;
    }

    @Transactional(readOnly = true)
    public UserProfileResponse currentProfile(Authentication authentication) {
        return toProfileResponse(findCurrentUser(authentication));
    }

    @Transactional
    public UserProfileResponse updateCurrentProfile(
            Authentication authentication, UpdateProfileRequest request) {
        UserJpaEntity user = findCurrentUser(authentication);
        String username = buildUsername(request, user.getUsername());
        user.setUsername(username);
        return toProfileResponse(tenantUserRepository.save(user));
    }

    @Transactional(readOnly = true)
    public PublicUserProfileResponse publicProfile(String userId) {
        UserJpaEntity user =
                tenantUserRepository
                        .findById(parseUserId(userId))
                        .orElseThrow(
                                () ->
                                        new AuthServiceException(
                                                "IAM_USER_NOT_FOUND",
                                                HttpStatus.NOT_FOUND,
                                                "User not found"));
        String[] names = splitName(user.getUsername());
        return new PublicUserProfileResponse(
                formatUserId(user.getId()),
                names[0],
                names[1],
                resolveTipo(user),
                null,
                null,
                user.isActive(),
                0,
                0);
    }

    private UserJpaEntity findCurrentUser(Authentication authentication) {
        Long userId = principalAccessService.currentUserId(authentication);
        String tenantId = principalAccessService.currentTokenTenant(authentication);
        if (userId == null || tenantId == null) {
            throw new AuthServiceException(
                    "IAM_AUTHENTICATION_REQUIRED",
                    HttpStatus.UNAUTHORIZED,
                    "Authenticated user is required");
        }
        return tenantUserRepository
                .findByIdAndTenantId(userId, tenantId)
                .orElseThrow(
                        () ->
                                new AuthServiceException(
                                        "IAM_USER_NOT_FOUND",
                                        HttpStatus.NOT_FOUND,
                                        "User not found"));
    }

    private UserProfileResponse toProfileResponse(UserJpaEntity user) {
        String[] names = splitName(user.getUsername());
        return new UserProfileResponse(
                formatUserId(user.getId()),
                user.getEmail(),
                names[0],
                names[1],
                resolveTipo(user),
                null,
                null,
                null,
                null,
                user.isActive() ? "Activo" : "Inactivo",
                toInstant(user.getCreatedAt()),
                toInstant(user.getLastModifiedAt()));
    }

    private String buildUsername(UpdateProfileRequest request, String fallback) {
        String nombre = request.nombre();
        String apellido = request.apellido();
        if ((nombre == null || nombre.isBlank()) && (apellido == null || apellido.isBlank())) {
            return fallback;
        }
        return ((nombre == null ? "" : nombre.trim())
                        + " "
                        + (apellido == null ? "" : apellido.trim()))
                .trim();
    }

    private String[] splitName(String username) {
        if (username == null || username.isBlank()) {
            return new String[] {"", ""};
        }
        String normalized = username.trim();
        int splitAt = normalized.indexOf(' ');
        if (splitAt < 0) {
            return new String[] {normalized, ""};
        }
        return new String[] {normalized.substring(0, splitAt), normalized.substring(splitAt + 1)};
    }

    private String resolveTipo(UserJpaEntity user) {
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            return "cliente";
        }
        return user.getRoles().stream()
                .map(RoleJpaEntity::getName)
                .filter(name -> name != null && !name.isBlank())
                .sorted()
                .findFirst()
                .map(String::toLowerCase)
                .orElse("cliente");
    }

    private Long parseUserId(String userId) {
        String normalized = userId == null ? "" : userId.trim();
        if (normalized.regionMatches(true, 0, "USR-", 0, 4)) {
            normalized = normalized.substring(4);
        }
        try {
            return Long.valueOf(normalized);
        } catch (NumberFormatException ex) {
            throw new AuthServiceException(
                    "IAM_USER_ID_INVALID", HttpStatus.BAD_REQUEST, "User id is invalid");
        }
    }

    private String formatUserId(Long userId) {
        return userId == null ? null : "USR-" + String.format("%03d", userId);
    }

    private Instant toInstant(LocalDateTime value) {
        return value == null ? null : value.toInstant(ZoneOffset.UTC);
    }
}
