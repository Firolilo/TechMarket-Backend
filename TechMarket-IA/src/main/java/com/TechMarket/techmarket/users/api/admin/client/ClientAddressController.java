package com.techmarket.techmarket.users.api.admin.client;

import com.techmarket.techmarket.users.api.admin.client.request.UpsertClientAddressRequest;
import com.techmarket.techmarket.users.api.admin.client.response.ClientAddressResponse;
import com.techmarket.techmarket.users.api.admin.client.response.MessageResponse;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.entity.ClientAddressJpaEntity;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.repository.ClientAddressSpringDataRepository;
import jakarta.validation.Valid;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/clients/addresses")
public class ClientAddressController {

    private final ClientAddressSpringDataRepository repository;

    public ClientAddressController(ClientAddressSpringDataRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<ClientAddressResponse> addresses(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        UUID currentUserId = parseUserId(userId);
        return repository.findAllByUserIdOrderByDefaultAddressDescCreatedAtAsc(currentUserId).stream()
                .map(this::toResponse)
                .toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClientAddressResponse create(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @Valid @RequestBody UpsertClientAddressRequest request) {
        UUID currentUserId = parseUserId(userId);
        OffsetDateTime now = OffsetDateTime.now();
        ClientAddressJpaEntity address = new ClientAddressJpaEntity();
        address.setId(UUID.randomUUID());
        address.setUserId(currentUserId);
        address.setTitle(request.titulo());
        address.setCountry(request.pais());
        address.setCity(request.ciudad());
        address.setAddress(request.direccion());
        address.setReference(request.referencia());
        address.setDefaultAddress(Boolean.TRUE.equals(request.esPredeterminada()));
        address.setCreatedAt(now);
        address.setUpdatedAt(now);
        ClientAddressJpaEntity saved = repository.save(address);
        if (saved.isDefaultAddress()) {
            unsetOtherDefaults(currentUserId, saved.getId());
        }
        return toResponse(saved);
    }

    @PutMapping("/{addressId}")
    public ClientAddressResponse update(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @PathVariable String addressId,
            @Valid @RequestBody UpsertClientAddressRequest request) {
        UUID currentUserId = parseUserId(userId);
        ClientAddressJpaEntity address = findAddress(parseAddressId(addressId), currentUserId);
        if (request.titulo() != null) {
            address.setTitle(request.titulo());
        }
        if (request.pais() != null) {
            address.setCountry(request.pais());
        }
        if (request.ciudad() != null) {
            address.setCity(request.ciudad());
        }
        if (request.direccion() != null) {
            address.setAddress(request.direccion());
        }
        if (request.referencia() != null) {
            address.setReference(request.referencia());
        }
        if (request.esPredeterminada() != null) {
            address.setDefaultAddress(request.esPredeterminada());
        }
        address.setUpdatedAt(OffsetDateTime.now());
        ClientAddressJpaEntity saved = repository.save(address);
        if (saved.isDefaultAddress()) {
            unsetOtherDefaults(currentUserId, saved.getId());
        }
        return toResponse(saved);
    }

    @DeleteMapping("/{addressId}")
    public MessageResponse delete(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @PathVariable String addressId) {
        UUID currentUserId = parseUserId(userId);
        ClientAddressJpaEntity address = findAddress(parseAddressId(addressId), currentUserId);
        repository.delete(address);
        return new MessageResponse("Direccion eliminada correctamente");
    }

    @PutMapping("/{addressId}/default")
    public MessageResponse markDefault(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @PathVariable String addressId) {
        UUID currentUserId = parseUserId(userId);
        ClientAddressJpaEntity address = findAddress(parseAddressId(addressId), currentUserId);
        address.setDefaultAddress(true);
        address.setUpdatedAt(OffsetDateTime.now());
        repository.save(address);
        unsetOtherDefaults(currentUserId, address.getId());
        return new MessageResponse("Direccion establecida como predeterminada");
    }

    private ClientAddressJpaEntity findAddress(UUID addressId, UUID userId) {
        return repository
                .findByIdAndUserId(addressId, userId)
                .orElseThrow(
                        () ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND, "Address not found"));
    }

    private void unsetOtherDefaults(UUID userId, UUID selectedAddressId) {
        List<ClientAddressJpaEntity> addresses = repository.findAllByUserId(userId);
        for (ClientAddressJpaEntity address : addresses) {
            if (selectedAddressId.equals(address.getId()) || !address.isDefaultAddress()) {
                continue;
            }
            address.setDefaultAddress(false);
            address.setUpdatedAt(OffsetDateTime.now());
            repository.save(address);
        }
    }

    private ClientAddressResponse toResponse(ClientAddressJpaEntity address) {
        return new ClientAddressResponse(
                formatAddressId(address.getId()),
                address.getTitle(),
                address.getCountry(),
                address.getCity(),
                address.getAddress(),
                address.getReference(),
                address.isDefaultAddress());
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

    private UUID parseAddressId(String addressId) {
        String normalized = addressId == null ? "" : addressId.trim();
        if (normalized.regionMatches(true, 0, "ADDR-", 0, 5)) {
            normalized = normalized.substring(5);
        }
        try {
            return UUID.fromString(normalized);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "addressId is invalid");
        }
    }

    private String formatAddressId(UUID addressId) {
        return "ADDR-" + addressId;
    }
}
