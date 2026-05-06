package com.techmarket.iamservice.application.service;

import com.techmarket.iamservice.application.dto.PublicUserProfileResponse;
import com.techmarket.iamservice.application.dto.UpdateProfileRequest;
import com.techmarket.iamservice.application.dto.UserProfileResponse;
import com.techmarket.iamservice.application.exception.AuthServiceException;
import com.techmarket.iamservice.infrastructure.persistence.entity.RoleEntity;
import com.techmarket.iamservice.infrastructure.persistence.entity.UserEntity;
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
        UserEntity user = findCurrentUser(authentication);
        if (request.nombre() != null) {
            user.setFirstName(trimToNull(request.nombre()));
        }
        if (request.apellido() != null) {
            user.setLastName(trimToNull(request.apellido()));
        }
        if (request.telefono() != null) {
            user.setPhone(trimToNull(request.telefono()));
        }
        if (request.ciudad() != null) {
            user.setCity(trimToNull(request.ciudad()));
        }
        return toProfileResponse(tenantUserRepository.save(user));
    }

    @Transactional(readOnly = true)
    public PublicUserProfileResponse publicProfile(String userId) {
        UserEntity user =
                tenantUserRepository
                        .findById(parseUserId(userId))
                        .orElseThrow(
                                () ->
                                        new AuthServiceException(
                                                "IAM_USER_NOT_FOUND",
                                                HttpStatus.NOT_FOUND,
                                                "User not found"));
        return new PublicUserProfileResponse(
                formatUserId(user.getId()),
                user.getFirstName(),
                user.getLastName(),
                resolveTipo(user),
                user.getCity(),
                null,
                user.isActive(),
                0,
                0);
    }

    private UserEntity findCurrentUser(Authentication authentication) {
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

    private UserProfileResponse toProfileResponse(UserEntity user) {
        return new UserProfileResponse(
                formatUserId(user.getId()),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                resolveTipo(user),
                user.getPhone(),
                user.getCountry(),
                user.getCity(),
                null,
                user.isActive() ? "Activo" : "Inactivo",
                toInstant(user.getCreatedAt()),
                toInstant(user.getLastModifiedAt()));
    }

    private String resolveTipo(UserEntity user) {
        if (user.getUserType() != null && !user.getUserType().isBlank()) {
            return user.getUserType();
        }
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            return "cliente";
        }
        return user.getRoles().stream()
                .map(RoleEntity::getName)
                .filter(name -> name != null && !name.isBlank())
                .sorted()
                .findFirst()
                .map(String::toLowerCase)
                .orElse("cliente");
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isBlank() ? null : trimmed;
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
