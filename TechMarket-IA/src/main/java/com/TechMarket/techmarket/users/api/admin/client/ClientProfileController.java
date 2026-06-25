package com.techmarket.techmarket.users.api.admin.client;

import com.techmarket.techmarket.users.api.admin.client.request.UpdateClientProfileRequest;
import com.techmarket.techmarket.users.api.admin.client.response.ClientProfileResponse;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.entity.UserJpaEntity;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.repository.UserSpringDataRepository;
import jakarta.validation.Valid;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/clients/profile")
public class ClientProfileController {

    private final UserSpringDataRepository repository;
    private final ClientUserProvisioner provisioner;

    public ClientProfileController(
            UserSpringDataRepository repository, ClientUserProvisioner provisioner) {
        this.repository = repository;
        this.provisioner = provisioner;
    }

    @GetMapping
    public ClientProfileResponse profile(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @RequestHeader(value = "X-User-Email", required = false) String email,
            @RequestHeader(value = "X-User-First-Name", required = false) String firstName,
            @RequestHeader(value = "X-User-Last-Name", required = false) String lastName,
            @RequestHeader(value = "X-User-Phone", required = false) String phone) {
        UUID id = parseUserId(userId);
        return toResponse(provisioner.resolveOrCreate(id, email, firstName, lastName, phone));
    }

    @PutMapping
    public ClientProfileResponse updateProfile(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @RequestHeader(value = "X-User-Email", required = false) String email,
            @RequestHeader(value = "X-User-First-Name", required = false) String firstName,
            @RequestHeader(value = "X-User-Last-Name", required = false) String lastName,
            @RequestHeader(value = "X-User-Phone", required = false) String phone,
            @Valid @RequestBody UpdateClientProfileRequest request) {
        UUID id = parseUserId(userId);
        UserJpaEntity user = provisioner.resolveOrCreate(id, email, firstName, lastName, phone);
        if (request.nombre() != null) {
            user.setFirstName(request.nombre());
        }
        if (request.apellido() != null) {
            user.setLastName(request.apellido());
        }
        if (request.telefono() != null) {
            user.setPhone(request.telefono());
        }
        user.setUpdatedAt(OffsetDateTime.now());
        return toResponse(repository.save(user));
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

    private ClientProfileResponse toResponse(UserJpaEntity user) {
        return new ClientProfileResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhone(),
                null);
    }
}
