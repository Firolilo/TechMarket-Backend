package com.techmarket.techmarket.specialists.api.admin;

import com.techmarket.techmarket.specialists.api.admin.request.RespondSpecialistRequestRequest;
import com.techmarket.techmarket.specialists.api.admin.response.SpecialistRequestActionResponse;
import com.techmarket.techmarket.specialists.api.admin.response.SpecialistRequestResponse;
import com.techmarket.techmarket.specialists.application.service.SpecialistIdentitySupport;
import com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.entity.SpecialistServiceAppointmentJpaEntity;
import com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.repository.SpecialistAppointmentSummaryProjection;
import com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.repository.SpecialistServiceAppointmentSpringDataRepository;
import jakarta.validation.Valid;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/specialists/requests")
public class SpecialistRequestController {

    private final SpecialistIdentitySupport identitySupport;
    private final SpecialistServiceAppointmentSpringDataRepository appointmentRepository;

    public SpecialistRequestController(
            SpecialistIdentitySupport identitySupport,
            SpecialistServiceAppointmentSpringDataRepository appointmentRepository) {
        this.identitySupport = identitySupport;
        this.appointmentRepository = appointmentRepository;
    }

    @GetMapping
    public List<SpecialistRequestResponse> requests(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        UUID currentUserId = identitySupport.requireUserId(userId);
        return appointmentRepository.findRequestsByTechnicianUserId(currentUserId).stream()
                .map(this::toResponse)
                .toList();
    }

    @PatchMapping("/{requestId}/respond")
    @Transactional
    public SpecialistRequestActionResponse respond(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @PathVariable String requestId,
            @Valid @RequestBody RespondSpecialistRequestRequest request) {
        UUID currentUserId = identitySupport.requireUserId(userId);
        SpecialistServiceAppointmentJpaEntity appointment =
                findAppointment(requestId, currentUserId, "Request not found");
        String action = normalizeAction(request.accion());
        if ("aceptada".equals(action)) {
            appointment.setStatus("aceptada");
            appointmentRepository.save(appointment);
            return new SpecialistRequestActionResponse(
                    action, "Solicitud aceptada, se notificó al cliente");
        }
        appointment.setStatus("rechazada");
        appointmentRepository.save(appointment);
        return new SpecialistRequestActionResponse(
                action, "Solicitud rechazada, se notificó al cliente");
    }

    private SpecialistRequestResponse toResponse(SpecialistAppointmentSummaryProjection request) {
        OffsetDateTime startAt = request.getStartAt();
        return new SpecialistRequestResponse(
                identitySupport.formatRequestId(request.getId()),
                fullName(request.getCustomerFirstName(), request.getCustomerLastName()),
                request.getServiceName(),
                startAt == null ? null : startAt.toLocalDate().toString(),
                request.getStatus(),
                request.getPriority());
    }

    private SpecialistServiceAppointmentJpaEntity findAppointment(
            String requestId, UUID currentUserId, String message) {
        UUID id = identitySupport.parsePrefixedUuid(requestId, "REQ-");
        return appointmentRepository
                .findByIdAndAssignedTechnicianUserId(id, currentUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, message));
    }

    private String normalizeAction(String action) {
        String normalized = action.trim().toLowerCase(Locale.ROOT);
        if ("aceptar".equals(normalized) || "aceptada".equals(normalized) || "aceptado".equals(normalized)) {
            return "aceptada";
        }
        if ("rechazar".equals(normalized) || "rechazada".equals(normalized) || "rechazado".equals(normalized)) {
            return "rechazada";
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "accion is invalid");
    }

    private String fullName(String firstName, String lastName) {
        String normalizedFirstName = firstName == null ? "" : firstName.trim();
        String normalizedLastName = lastName == null ? "" : lastName.trim();
        String fullName = (normalizedFirstName + " " + normalizedLastName).trim();
        return fullName.isBlank() ? null : fullName;
    }
}
