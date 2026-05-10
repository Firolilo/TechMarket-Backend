package com.techmarket.techmarket.ambassadors.application.service;

import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.entity.AmbassadorJpaEntity;
import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.repository.AmbassadorSpringDataRepository;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.entity.UserJpaEntity;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.repository.UserSpringDataRepository;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AmbassadorIdentitySupport {

    private final UserSpringDataRepository userRepository;
    private final AmbassadorSpringDataRepository ambassadorRepository;

    public AmbassadorIdentitySupport(
            UserSpringDataRepository userRepository,
            AmbassadorSpringDataRepository ambassadorRepository) {
        this.userRepository = userRepository;
        this.ambassadorRepository = ambassadorRepository;
    }

    public UserJpaEntity requireUser(String userId) {
        UUID id = parseUserId(userId);
        return userRepository
                .findById(id)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    public AmbassadorJpaEntity requireAmbassador(String userId) {
        UUID id = parseUserId(userId);
        return ambassadorRepository.findByUserId(id).orElseGet(() -> createDefaultAmbassador(id));
    }

    private AmbassadorJpaEntity createDefaultAmbassador(UUID userId) {
        AmbassadorJpaEntity ambassador = new AmbassadorJpaEntity();
        ambassador.setId(UUID.randomUUID());
        ambassador.setUserId(userId);
        ambassador.setReferralCode("AMB-" + userId.toString().substring(0, 8).toUpperCase());
        ambassador.setStatus("activo");
        ambassador.setLevel("bronce");
        ambassador.setActivatedAt(java.time.OffsetDateTime.now());
        return ambassadorRepository.save(ambassador);
    }

    public UUID requireAmbassadorId(String userId) {
        return requireAmbassador(userId).getId();
    }

    public UUID parsePrefixedUuid(String value, String prefix) {
        String normalized = value == null ? "" : value.trim();
        if (normalized.regionMatches(true, 0, prefix, 0, prefix.length())) {
            normalized = normalized.substring(prefix.length());
        }
        try {
            return UUID.fromString(normalized);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Identifier is invalid");
        }
    }

    public String formatCommissionId(UUID id) {
        return "COM-" + id;
    }

    public String formatWithdrawalId(UUID id) {
        return "WDR-" + id;
    }

    public String formatPayoutId(UUID id) {
        return "PAY-" + id;
    }

    public String formatMissionId(UUID id) {
        return "MSN-" + id;
    }

    private UUID parseUserId(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "X-User-Id header is required");
        }
        try {
            return UUID.fromString(userId.trim());
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "X-User-Id is not a valid UUID");
        }
    }
}
