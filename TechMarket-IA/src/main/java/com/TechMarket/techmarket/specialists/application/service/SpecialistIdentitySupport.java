package com.techmarket.techmarket.specialists.application.service;

import com.techmarket.techmarket.users.infrastructure.persistence.jpa.entity.UserJpaEntity;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.repository.UserSpringDataRepository;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class SpecialistIdentitySupport {

    private final UserSpringDataRepository userRepository;

    public SpecialistIdentitySupport(UserSpringDataRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserJpaEntity requireUser(String userId) {
        UUID id = parseUserId(userId);
        return userRepository
                .findById(id)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    public UUID requireUserId(String userId) {
        return requireUser(userId).getId();
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

    public String formatSpecialistId(UUID id) {
        return "TEC-" + id;
    }

    public String formatServiceId(UUID id) {
        return "SERV-" + id;
    }

    public String formatPortfolioId(UUID id) {
        return "PORT-" + id;
    }

    private UUID parseUserId(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "X-User-Id is required");
        }
        try {
            return UUID.fromString(userId.trim());
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "X-User-Id is invalid");
        }
    }
}
