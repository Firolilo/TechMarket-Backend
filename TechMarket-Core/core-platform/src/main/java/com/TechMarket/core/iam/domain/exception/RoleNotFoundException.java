package com.techmarket.core.iam.domain.exception;

import com.techmarket.core.shared.exceptions.ApplicationException;
import java.util.Map;

public class RoleNotFoundException extends ApplicationException {

    public RoleNotFoundException(Long roleId) {
        super("IAM_ROLE_NOT_FOUND", Map.of("roleId", roleId), "Role not found: " + roleId);
    }
}
