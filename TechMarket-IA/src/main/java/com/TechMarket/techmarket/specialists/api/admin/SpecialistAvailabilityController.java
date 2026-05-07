package com.techmarket.techmarket.specialists.api.admin;

import com.techmarket.techmarket.specialists.api.admin.request.UpdateSpecialistAvailabilityRequest;
import com.techmarket.techmarket.specialists.api.admin.request.UpdateSpecialistAvailabilityStatusRequest;
import com.techmarket.techmarket.specialists.api.admin.response.MessageResponse;
import com.techmarket.techmarket.specialists.api.admin.response.SpecialistAvailabilityResponse;
import com.techmarket.techmarket.specialists.api.admin.response.SpecialistAvailabilityStatusResponse;
import com.techmarket.techmarket.specialists.api.admin.response.SpecialistScheduleResponse;
import com.techmarket.techmarket.specialists.application.service.SpecialistIdentitySupport;
import com.techmarket.techmarket.specialists.application.service.SpecialistJsonListMapper;
import com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.entity.SpecialistAvailabilityJpaEntity;
import com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.repository.SpecialistAvailabilitySpringDataRepository;
import jakarta.validation.Valid;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/specialists/availability")
public class SpecialistAvailabilityController {

    private static final String DEFAULT_STATUS = "disponible";
    private static final List<String> DEFAULT_DAYS =
            List.of("lunes", "martes", "miercoles", "jueves", "viernes", "sabado");
    private static final List<String> DEFAULT_MODALITIES =
            List.of("presencial", "remoto", "domicilio");
    private static final Set<String> ALLOWED_STATUSES =
            Set.of("disponible", "ocupado", "ausente");

    private final SpecialistIdentitySupport identitySupport;
    private final SpecialistJsonListMapper jsonListMapper;
    private final SpecialistAvailabilitySpringDataRepository availabilityRepository;

    public SpecialistAvailabilityController(
            SpecialistIdentitySupport identitySupport,
            SpecialistJsonListMapper jsonListMapper,
            SpecialistAvailabilitySpringDataRepository availabilityRepository) {
        this.identitySupport = identitySupport;
        this.jsonListMapper = jsonListMapper;
        this.availabilityRepository = availabilityRepository;
    }

    @GetMapping
    public SpecialistAvailabilityResponse availability(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        UUID currentUserId = identitySupport.requireUserId(userId);
        return availabilityRepository
                .findByUserId(currentUserId)
                .map(this::toResponse)
                .orElseGet(this::defaultResponse);
    }

    @PutMapping
    public MessageResponse updateAvailability(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @Valid @RequestBody UpdateSpecialistAvailabilityRequest request) {
        UUID currentUserId = identitySupport.requireUserId(userId);
        SpecialistAvailabilityJpaEntity availability = getOrCreateAvailability(currentUserId);
        if (request.estado() != null) {
            availability.setStatus(validateStatus(request.estado()));
        }
        if (request.dias() != null) {
            availability.setDaysJson(jsonListMapper.toJson(request.dias()));
        }
        if (request.inicio() != null) {
            availability.setStartTime(request.inicio());
        }
        if (request.fin() != null) {
            availability.setEndTime(request.fin());
        }
        if (request.modalidad() != null) {
            availability.setModalitiesJson(jsonListMapper.toJson(request.modalidad()));
        }
        if (request.cobertura() != null) {
            availability.setCoverage(request.cobertura());
        }
        availability.setUpdatedAt(OffsetDateTime.now());
        availabilityRepository.save(availability);
        return new MessageResponse("Disponibilidad actualizada");
    }

    @PatchMapping("/status")
    public SpecialistAvailabilityStatusResponse updateStatus(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @Valid @RequestBody UpdateSpecialistAvailabilityStatusRequest request) {
        UUID currentUserId = identitySupport.requireUserId(userId);
        SpecialistAvailabilityJpaEntity availability = getOrCreateAvailability(currentUserId);
        availability.setStatus(validateStatus(request.estado()));
        if (request.tiempoRespuesta() != null) {
            availability.setResponseTime(request.tiempoRespuesta());
        }
        availability.setUpdatedAt(OffsetDateTime.now());
        SpecialistAvailabilityJpaEntity saved = availabilityRepository.save(availability);
        return new SpecialistAvailabilityStatusResponse(saved.getStatus(), saved.getResponseTime());
    }

    private SpecialistAvailabilityJpaEntity getOrCreateAvailability(UUID userId) {
        return availabilityRepository
                .findByUserId(userId)
                .orElseGet(
                        () -> {
                            OffsetDateTime now = OffsetDateTime.now();
                            SpecialistAvailabilityJpaEntity availability =
                                    new SpecialistAvailabilityJpaEntity();
                            availability.setId(UUID.randomUUID());
                            availability.setUserId(userId);
                            availability.setStatus(DEFAULT_STATUS);
                            availability.setDaysJson(jsonListMapper.toJson(DEFAULT_DAYS));
                            availability.setStartTime("08:00");
                            availability.setEndTime("18:00");
                            availability.setModalitiesJson(jsonListMapper.toJson(DEFAULT_MODALITIES));
                            availability.setCreatedAt(now);
                            availability.setUpdatedAt(now);
                            return availability;
                        });
    }

    private SpecialistAvailabilityResponse toResponse(SpecialistAvailabilityJpaEntity availability) {
        return new SpecialistAvailabilityResponse(
                availability.getStatus(),
                jsonListMapper.fromJson(availability.getDaysJson()),
                new SpecialistScheduleResponse(availability.getStartTime(), availability.getEndTime()),
                jsonListMapper.fromJson(availability.getModalitiesJson()),
                availability.getCoverage());
    }

    private SpecialistAvailabilityResponse defaultResponse() {
        return new SpecialistAvailabilityResponse(
                DEFAULT_STATUS,
                DEFAULT_DAYS,
                new SpecialistScheduleResponse("08:00", "18:00"),
                DEFAULT_MODALITIES,
                "Santa Cruz de la Sierra");
    }

    private String validateStatus(String status) {
        String normalized = status == null ? "" : status.trim().toLowerCase();
        if (!ALLOWED_STATUSES.contains(normalized)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "estado is invalid");
        }
        return normalized;
    }
}
