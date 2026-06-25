package com.techmarket.techmarket.empresa.api.admin;

import com.techmarket.techmarket.tenants.infrastructure.persistence.jpa.entity.TenantJpaEntity;
import com.techmarket.techmarket.tenants.infrastructure.persistence.jpa.repository.TenantSpringDataRepository;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.entity.UserJpaEntity;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.repository.UserSpringDataRepository;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * Resolves the company (tenant) for an authenticated user, creating it on the fly the first time an
 * empresa user has none yet (self-registered companies are created in IAM but not in
 * TechMarket-IA). Mirrors the lazy-provisioning approach already used for ambassadors. The company
 * starts empty and the owner completes it from {@code /empresa/perfil}.
 */
@Component
public class EmpresaTenantProvisioner {

    private final TenantSpringDataRepository tenantRepository;
    private final UserSpringDataRepository userRepository;
    private final JdbcTemplate jdbcTemplate;

    public EmpresaTenantProvisioner(
            TenantSpringDataRepository tenantRepository,
            UserSpringDataRepository userRepository,
            JdbcTemplate jdbcTemplate) {
        this.tenantRepository = tenantRepository;
        this.userRepository = userRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public TenantJpaEntity resolveOrCreate(UUID userId) {
        return tenantRepository
                .findFirstByMemberUserId(userId)
                .orElseGet(() -> createForUser(userId));
    }

    private TenantJpaEntity createForUser(UUID userId) {
        // tenant_members.user_id tiene FK a users: solo se puede crear la empresa si el usuario
        // existe en TechMarket-IA. Si no (p. ej. registrado solo en IAM), 404 en vez de un 500 por
        // violacion de FK.
        UserJpaEntity user =
                userRepository
                        .findById(userId)
                        .orElseThrow(
                                () ->
                                        new ResponseStatusException(
                                                HttpStatus.NOT_FOUND,
                                                "No se encontro empresa para este usuario"));
        OffsetDateTime now = OffsetDateTime.now();

        TenantJpaEntity tenant = new TenantJpaEntity();
        tenant.setId(UUID.randomUUID());
        tenant.setBusinessName(defaultBusinessName(user));
        tenant.setStatus("ACTIVE");
        tenant.setCreatedAt(now);
        tenant.setUpdatedAt(now);
        tenantRepository.save(tenant);

        // tenant_members no tiene entidad JPA; se inserta directamente. El usuario queda como
        // OWNER.
        jdbcTemplate.update(
                "INSERT INTO tenant_members (id, tenant_id, user_id, tenant_role, status,"
                        + " invited_by_user_id, joined_at) VALUES (?, ?, ?, 'OWNER', 'ACTIVE', NULL,"
                        + " ?)",
                UUID.randomUUID(),
                tenant.getId(),
                userId,
                now);

        return tenant;
    }

    private String defaultBusinessName(UserJpaEntity user) {
        if (user == null) {
            return "Mi empresa";
        }
        String firstName = user.getFirstName() != null ? user.getFirstName().trim() : "";
        String lastName = user.getLastName() != null ? user.getLastName().trim() : "";
        String full = (firstName + " " + lastName).trim();
        if (!full.isBlank()) {
            return full;
        }
        String email = user.getEmail();
        if (email != null && email.contains("@")) {
            return email.substring(0, email.indexOf('@'));
        }
        return "Mi empresa";
    }
}
