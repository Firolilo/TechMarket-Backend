package com.techmarket.techmarket.specialists.api.admin;

import com.techmarket.techmarket.specialists.api.admin.request.CreateSpecialistServiceRequest;
import com.techmarket.techmarket.specialists.api.admin.request.UpdateSpecialistServiceRequest;
import com.techmarket.techmarket.specialists.api.admin.response.CreateSpecialistServiceResponse;
import com.techmarket.techmarket.specialists.api.admin.response.MessageResponse;
import com.techmarket.techmarket.specialists.api.admin.response.SpecialistServiceResponse;
import com.techmarket.techmarket.specialists.api.admin.response.ToggleFeaturedResponse;
import com.techmarket.techmarket.specialists.application.service.SpecialistIdentitySupport;
import com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.entity.SpecialistServiceJpaEntity;
import com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.repository.SpecialistServiceSpringDataRepository;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
@RequestMapping("/api/specialists/services")
public class SpecialistServiceController {

    private static final String DEFAULT_CURRENCY = "Bs";

    private final SpecialistIdentitySupport identitySupport;
    private final SpecialistServiceSpringDataRepository serviceRepository;

    public SpecialistServiceController(
            SpecialistIdentitySupport identitySupport,
            SpecialistServiceSpringDataRepository serviceRepository) {
        this.identitySupport = identitySupport;
        this.serviceRepository = serviceRepository;
    }

    @GetMapping
    public List<SpecialistServiceResponse> services(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        UUID currentUserId = identitySupport.requireUserId(userId);
        return serviceRepository.findAllByUserIdOrderByCreatedAtDesc(currentUserId).stream()
                .map(this::toResponse)
                .toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateSpecialistServiceResponse createService(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @Valid @RequestBody CreateSpecialistServiceRequest request) {
        UUID currentUserId = identitySupport.requireUserId(userId);
        OffsetDateTime now = OffsetDateTime.now();
        SpecialistServiceJpaEntity service = new SpecialistServiceJpaEntity();
        service.setId(UUID.randomUUID());
        service.setUserId(currentUserId);
        service.setName(request.nombre());
        service.setDescription(request.descripcion());
        service.setPrice(request.precio());
        service.setCurrency(currencyOrDefault(request.moneda()));
        service.setServiceType(request.tipo());
        service.setFeatured(Boolean.TRUE.equals(request.destacado()));
        service.setCreatedAt(now);
        service.setUpdatedAt(now);
        SpecialistServiceJpaEntity saved = serviceRepository.save(service);
        return new CreateSpecialistServiceResponse(
                identitySupport.formatServiceId(saved.getId()),
                saved.getName(),
                "Servicio creado exitosamente");
    }

    @PutMapping("/{serviceId}")
    public MessageResponse updateService(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @PathVariable String serviceId,
            @Valid @RequestBody UpdateSpecialistServiceRequest request) {
        UUID currentUserId = identitySupport.requireUserId(userId);
        SpecialistServiceJpaEntity service =
                findOwnedService(parseServiceId(serviceId), currentUserId);
        if (request.nombre() != null) {
            service.setName(request.nombre());
        }
        if (request.descripcion() != null) {
            service.setDescription(request.descripcion());
        }
        if (request.precio() != null) {
            service.setPrice(request.precio());
        }
        if (request.moneda() != null) {
            service.setCurrency(currencyOrDefault(request.moneda()));
        }
        if (request.tipo() != null) {
            service.setServiceType(request.tipo());
        }
        if (request.destacado() != null) {
            service.setFeatured(request.destacado());
        }
        service.setUpdatedAt(OffsetDateTime.now());
        serviceRepository.save(service);
        return new MessageResponse("Servicio actualizado");
    }

    @DeleteMapping("/{serviceId}")
    public MessageResponse deleteService(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @PathVariable String serviceId) {
        UUID currentUserId = identitySupport.requireUserId(userId);
        SpecialistServiceJpaEntity service =
                findOwnedService(parseServiceId(serviceId), currentUserId);
        serviceRepository.delete(service);
        return new MessageResponse("Servicio eliminado");
    }

    @PatchMapping("/{serviceId}/toggle-featured")
    public ToggleFeaturedResponse toggleFeatured(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @PathVariable String serviceId) {
        UUID currentUserId = identitySupport.requireUserId(userId);
        SpecialistServiceJpaEntity service =
                findOwnedService(parseServiceId(serviceId), currentUserId);
        service.setFeatured(!service.isFeatured());
        service.setUpdatedAt(OffsetDateTime.now());
        SpecialistServiceJpaEntity saved = serviceRepository.save(service);
        return new ToggleFeaturedResponse(saved.isFeatured());
    }

    private SpecialistServiceJpaEntity findOwnedService(UUID id, UUID userId) {
        return serviceRepository
                .findByIdAndUserId(id, userId)
                .orElseThrow(
                        () ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND, "Service not found"));
    }

    private SpecialistServiceResponse toResponse(SpecialistServiceJpaEntity service) {
        return new SpecialistServiceResponse(
                identitySupport.formatServiceId(service.getId()),
                service.getName(),
                service.getDescription(),
                formatPrice(service.getCurrency(), service.getPrice()),
                service.getServiceType(),
                service.isFeatured());
    }

    private UUID parseServiceId(String serviceId) {
        return identitySupport.parsePrefixedUuid(serviceId, "SERV-");
    }

    private String currencyOrDefault(String currency) {
        if (currency == null || currency.isBlank()) {
            return DEFAULT_CURRENCY;
        }
        return currency.trim();
    }

    private String formatPrice(String currency, BigDecimal price) {
        BigDecimal amount = price == null ? BigDecimal.ZERO.setScale(2) : price;
        return currencyOrDefault(currency) + " " + amount.toPlainString();
    }
}
