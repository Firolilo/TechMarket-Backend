package com.techmarket.techmarket.users.api.admin.client;

import com.techmarket.techmarket.users.infrastructure.persistence.jpa.entity.UserJpaEntity;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.repository.UserSpringDataRepository;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Resolves the TechMarket-IA user row for an authenticated client, creating it on the fly the first
 * time (self-registered clients are created in IAM but never in TechMarket-IA). Mirrors the
 * lazy-provisioning approach used for empresas/ambassadors. Since IA cannot talk to IAM, the
 * identity (email/name/phone) is supplied by the caller from the IAM profile; existing rows are
 * never overwritten, only blank fields are backfilled.
 */
@Component
public class ClientUserProvisioner {

    private final UserSpringDataRepository repository;

    public ClientUserProvisioner(UserSpringDataRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public UserJpaEntity resolveOrCreate(
            UUID id, String email, String firstName, String lastName, String phone) {
        OffsetDateTime now = OffsetDateTime.now();

        UserJpaEntity existing = repository.findById(id).orElse(null);
        if (existing != null) {
            return backfill(existing, email, firstName, lastName, phone, now);
        }

        UserJpaEntity user = new UserJpaEntity();
        user.setId(id);
        user.setEmail(trimToNull(email));
        user.setFirstName(trimToNull(firstName));
        user.setLastName(trimToNull(lastName));
        user.setPhone(trimToNull(phone));
        user.setStatus("ACTIVE");
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        return repository.save(user);
    }

    private UserJpaEntity backfill(
            UserJpaEntity user,
            String email,
            String firstName,
            String lastName,
            String phone,
            OffsetDateTime now) {
        boolean changed = false;
        if (isBlank(user.getEmail()) && !isBlank(email)) {
            user.setEmail(email.trim());
            changed = true;
        }
        if (isBlank(user.getFirstName()) && !isBlank(firstName)) {
            user.setFirstName(firstName.trim());
            changed = true;
        }
        if (isBlank(user.getLastName()) && !isBlank(lastName)) {
            user.setLastName(lastName.trim());
            changed = true;
        }
        if (isBlank(user.getPhone()) && !isBlank(phone)) {
            user.setPhone(phone.trim());
            changed = true;
        }
        if (changed) {
            user.setUpdatedAt(now);
            return repository.save(user);
        }
        return user;
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private static String trimToNull(String value) {
        return isBlank(value) ? null : value.trim();
    }
}
