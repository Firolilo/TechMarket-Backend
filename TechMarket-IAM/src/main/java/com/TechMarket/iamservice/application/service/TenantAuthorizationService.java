package com.techmarket.iamservice.application.service;

import com.techmarket.iamservice.application.model.IamConstants;
import java.util.UUID;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class TenantAuthorizationService {

    private final PrincipalAccessService principalAccessService;

    public TenantAuthorizationService(PrincipalAccessService principalAccessService) {
        this.principalAccessService = principalAccessService;
    }

    public String resolveTenantForRead(String tenantIdHeader, Authentication authentication) {
        if (principalAccessService.isGlobalAdmin(authentication)) {
            if (tenantIdHeader == null || tenantIdHeader.isBlank()) {
                return null;
            }
            return normalizeTenantId(tenantIdHeader);
        }

        String tokenTenantId = principalAccessService.currentTokenTenant(authentication);
        if ((tenantIdHeader == null || tenantIdHeader.isBlank())
                && tokenTenantId != null
                && !tokenTenantId.isBlank()) {
            return normalizeTenantId(tokenTenantId);
        }

        String tenantId = normalizeTenantId(tenantIdHeader);
        if (tokenTenantId == null || tokenTenantId.isBlank()) {
            return tenantId;
        }

        String normalizedTokenTenantId = normalizeTenantId(tokenTenantId);
        if (!tenantId.equals(normalizedTokenTenantId)) {
            throw new AccessDeniedException("X-Tenant-Id does not match token tenant_id");
        }
        return tenantId;
    }

    public String resolveTenantForWrite(
            String tenantIdHeader, String tenantIdPayload, Authentication authentication) {
        String headerTenantId =
                tenantIdHeader == null || tenantIdHeader.isBlank()
                        ? null
                        : normalizeTenantId(tenantIdHeader);
        String payloadTenantId =
                tenantIdPayload == null || tenantIdPayload.isBlank()
                        ? null
                        : normalizeTenantId(tenantIdPayload);

        if (principalAccessService.isGlobalAdmin(authentication)) {
            if (payloadTenantId != null) {
                return payloadTenantId;
            }
            if (headerTenantId != null) {
                return headerTenantId;
            }
            throw new IllegalArgumentException(
                    "X-Tenant-Id header or tenantId payload is required");
        }

        String tokenTenantId =
                normalizeTenantId(principalAccessService.currentTokenTenant(authentication));
        if (tokenTenantId == null || tokenTenantId.isBlank()) {
            String resolved = payloadTenantId != null ? payloadTenantId : headerTenantId;
            if (resolved == null || resolved.isBlank()) {
                throw new IllegalArgumentException(
                        "X-Tenant-Id header or tenantId payload is required");
            }
            if (headerTenantId != null
                    && payloadTenantId != null
                    && !headerTenantId.equals(payloadTenantId)) {
                throw new AccessDeniedException("X-Tenant-Id and tenantId payload must match");
            }
            return resolved;
        }

        String resolved = headerTenantId != null ? headerTenantId : tokenTenantId;
        if (!resolved.equals(tokenTenantId)) {
            throw new AccessDeniedException("X-Tenant-Id does not match token tenant_id");
        }

        if (payloadTenantId != null && !payloadTenantId.equals(tokenTenantId)) {
            throw new AccessDeniedException("tenantId payload does not match token tenant_id");
        }

        return resolved;
    }

    public String requireTenant(String tenantIdHeader) {
        return normalizeTenantId(tenantIdHeader);
    }

    public String normalizeTenantId(String tenantId) {
        if (tenantId == null || tenantId.isBlank()) {
            return null;
        }
        String normalized = tenantId.trim();
        try {
            return UUID.fromString(normalized).toString();
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("X-Tenant-Id must be a valid UUID");
        }
    }

    public void validateTenantAccess(String tenantIdHeader, Authentication authentication) {
        String tenantId = normalizeTenantId(tenantIdHeader);
        if (tenantId == null || tenantId.isBlank()) {
            throw new IllegalArgumentException("X-Tenant-Id header is required");
        }
        if (principalAccessService.isGlobalAdmin(authentication)) {
            return;
        }

        if (authentication == null) {
            return;
        }

        String tokenTenantId =
                normalizeTenantId(principalAccessService.currentTokenTenant(authentication));
        if (tokenTenantId == null || tokenTenantId.isBlank()) {
            return;
        }

        if (!tenantId.equals(tokenTenantId)) {
            throw new AccessDeniedException("X-Tenant-Id does not match token tenant_id");
        }
    }

    public boolean isGlobalAdmin(Authentication authentication) {
        return principalAccessService.isGlobalAdmin(authentication);
    }

    public boolean isGlobalTenant(String tenantId) {
        return IamConstants.GLOBAL_TENANT_ID.equals(tenantId);
    }
}
