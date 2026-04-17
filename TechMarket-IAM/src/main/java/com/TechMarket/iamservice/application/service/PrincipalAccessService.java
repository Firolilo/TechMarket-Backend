package com.techmarket.iamservice.application.service;

import com.techmarket.iamservice.application.model.IamConstants;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class PrincipalAccessService {

    public boolean isGlobalAdmin(Authentication authentication) {
        if (authentication == null) {
            return false;
        }

        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if (authority == null) {
                continue;
            }
            String value = authority.getAuthority();
            if (value == null) {
                continue;
            }
            if ("ROLE_ADMIN_GLOBAL".equalsIgnoreCase(value)
                    || "ROLE_ADMINGLOBAL".equalsIgnoreCase(value)) {
                return true;
            }
        }

        if (authentication instanceof JwtAuthenticationToken jwtAuthenticationToken) {
            Collection<String> roles =
                    jwtAuthenticationToken.getToken().getClaimAsStringList("roles");
            if (roles == null) {
                return false;
            }
            return roles.stream()
                    .filter(role -> role != null && !role.isBlank())
                    .anyMatch(role -> IamConstants.GLOBAL_ADMIN_ROLE.equalsIgnoreCase(role));
        }

        return false;
    }

    public Long currentUserId(Authentication authentication) {
        if (!(authentication instanceof JwtAuthenticationToken jwtAuthenticationToken)) {
            return null;
        }
        try {
            return Long.valueOf(jwtAuthenticationToken.getToken().getSubject());
        } catch (Exception ex) {
            return null;
        }
    }

    public String currentTokenTenant(Authentication authentication) {
        if (!(authentication instanceof JwtAuthenticationToken jwtAuthenticationToken)) {
            return null;
        }
        return jwtAuthenticationToken.getToken().getClaimAsString("tenant_id");
    }

    public Set<String> currentRoleNames(Authentication authentication) {
        Set<String> roles = new LinkedHashSet<>();
        if (authentication == null) {
            return roles;
        }

        if (authentication instanceof JwtAuthenticationToken jwtAuthenticationToken) {
            Collection<String> tokenRoles =
                    jwtAuthenticationToken.getToken().getClaimAsStringList("roles");
            if (tokenRoles != null) {
                for (String role : tokenRoles) {
                    if (role == null || role.isBlank()) {
                        continue;
                    }
                    roles.add(role.trim().toLowerCase());
                }
            }
            return roles;
        }

        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if (authority == null || authority.getAuthority() == null) {
                continue;
            }
            String value = authority.getAuthority();
            if (value.regionMatches(true, 0, "ROLE_", 0, 5)) {
                roles.add(value.substring(5).toLowerCase());
            }
        }
        return roles;
    }
}
