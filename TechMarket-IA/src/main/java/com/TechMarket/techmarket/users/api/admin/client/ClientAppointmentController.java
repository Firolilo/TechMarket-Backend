package com.techmarket.techmarket.users.api.admin.client;

import com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.entity.SpecialistReviewJpaEntity;
import com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.entity.SpecialistServiceAppointmentJpaEntity;
import com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.repository.ClientAppointmentSummaryProjection;
import com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.repository.SpecialistReviewSpringDataRepository;
import com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.repository.SpecialistServiceAppointmentSpringDataRepository;
import com.techmarket.techmarket.users.api.admin.client.request.CreateAppointmentRequest;
import com.techmarket.techmarket.users.api.admin.client.request.UpsertReviewRequest;
import com.techmarket.techmarket.users.api.admin.client.response.ClientAppointmentResponse;
import com.techmarket.techmarket.users.api.admin.client.response.CreateAppointmentResponse;
import com.techmarket.techmarket.users.api.admin.client.response.CreateReviewResponse;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.entity.ClientChatJpaEntity;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.repository.ClientChatSpringDataRepository;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.repository.UserSpringDataRepository;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * Agendamiento de citas del cliente con un especialista. Crea un ticket ("APPOINTMENT") y la cita
 * (service_appointments) en estado "pendiente". El especialista la ve como solicitud y la
 * acepta/rechaza/completa desde su portal; el cliente ve aquí el estado del ciclo.
 */
@RestController
@RequestMapping("/api/clients/appointments")
public class ClientAppointmentController {

    private static final String APPOINTMENT_TYPE = "APPOINTMENT";
    private static final String PENDING_STATUS = "pendiente";
    private static final String DEFAULT_SERVICE = "Servicio técnico";
    private static final String DEFAULT_TIME = "09:00";

    private final ClientChatSpringDataRepository ticketRepository;
    private final SpecialistServiceAppointmentSpringDataRepository appointmentRepository;
    private final UserSpringDataRepository userRepository;
    private final SpecialistReviewSpringDataRepository reviewRepository;

    public ClientAppointmentController(
            ClientChatSpringDataRepository ticketRepository,
            SpecialistServiceAppointmentSpringDataRepository appointmentRepository,
            UserSpringDataRepository userRepository,
            SpecialistReviewSpringDataRepository reviewRepository) {
        this.ticketRepository = ticketRepository;
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
        this.reviewRepository = reviewRepository;
    }

    @GetMapping
    public List<ClientAppointmentResponse> appointments(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        UUID currentUserId = parseUserId(userId);
        return appointmentRepository.findAppointmentsByCustomerUserId(currentUserId).stream()
                .map(this::toResponse)
                .toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public CreateAppointmentResponse createAppointment(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @Valid @RequestBody CreateAppointmentRequest request) {
        UUID currentUserId = parseUserId(userId);
        UUID specialistId = parsePrefixedUuid(request.especialistaId(), "USR-");
        if (!userRepository.existsById(specialistId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Specialist not found");
        }
        OffsetDateTime startAt = parseStartAt(request.fecha(), request.hora());
        OffsetDateTime now = OffsetDateTime.now();
        String subject = blankToDefault(request.servicio(), DEFAULT_SERVICE);

        ClientChatJpaEntity ticket = new ClientChatJpaEntity();
        ticket.setId(UUID.randomUUID());
        ticket.setTicketCode("APT-" + ticket.getId());
        ticket.setCustomerUserId(currentUserId);
        ticket.setAssignedTechnicianUserId(specialistId);
        ticket.setTicketType(APPOINTMENT_TYPE);
        ticket.setSubject(subject);
        ticket.setDescription(request.descripcion());
        ticket.setPriority("media");
        ticket.setStatus(PENDING_STATUS);
        ticket.setOpenedAt(now);
        ticket.setCreatedAt(now);
        ClientChatJpaEntity savedTicket = ticketRepository.save(ticket);

        SpecialistServiceAppointmentJpaEntity appointment =
                new SpecialistServiceAppointmentJpaEntity();
        appointment.setId(UUID.randomUUID());
        appointment.setTicketId(savedTicket.getId());
        appointment.setAssignedTechnicianUserId(specialistId);
        appointment.setStatus(PENDING_STATUS);
        appointment.setStartAt(startAt);
        appointment.setLocation(request.ubicacion());
        appointment.setNotes(request.notas());
        SpecialistServiceAppointmentJpaEntity saved = appointmentRepository.save(appointment);

        return new CreateAppointmentResponse(
                "CITA-" + saved.getId(),
                PENDING_STATUS,
                "Cita solicitada, esperando al especialista");
    }

    /**
     * El cliente califica al especialista de una cita. La reseña entra en la tabla {@code reviews}
     * ligada al ticket de la cita, así que el promedio de reputación del especialista (que se
     * calcula sobre esas reseñas) se actualiza con calificaciones reales.
     */
    @PostMapping("/{appointmentId}/review")
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public CreateReviewResponse reviewAppointment(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @PathVariable String appointmentId,
            @Valid @RequestBody UpsertReviewRequest request) {
        UUID currentUserId = parseUserId(userId);
        UUID id = parsePrefixedUuid(appointmentId, "CITA-");
        SpecialistServiceAppointmentJpaEntity appointment =
                appointmentRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new ResponseStatusException(
                                                HttpStatus.NOT_FOUND, "Appointment not found"));
        UUID ticketId = appointment.getTicketId();
        ClientChatJpaEntity ticket =
                ticketId == null ? null : ticketRepository.findById(ticketId).orElse(null);
        if (ticket == null || !currentUserId.equals(ticket.getCustomerUserId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found");
        }

        SpecialistReviewJpaEntity review = new SpecialistReviewJpaEntity();
        review.setId(UUID.randomUUID());
        review.setTicketId(ticketId);
        review.setUserId(currentUserId);
        review.setRating(BigDecimal.valueOf(request.calificacion()));
        review.setComment(request.comentario());
        review.setCreatedAt(OffsetDateTime.now());
        SpecialistReviewJpaEntity saved = reviewRepository.save(review);
        return new CreateReviewResponse(
                "REV-" + saved.getId(), "Reseña publicada, gracias por calificar al especialista");
    }

    private ClientAppointmentResponse toResponse(ClientAppointmentSummaryProjection projection) {
        String startAt = projection.getStartAt();
        return new ClientAppointmentResponse(
                "CITA-" + projection.getId(),
                fullName(projection.getTechnicianFirstName(), projection.getTechnicianLastName()),
                blankToDefault(projection.getServiceName(), DEFAULT_SERVICE),
                datePart(startAt),
                timePart(startAt),
                normalizeStatus(projection.getStatus()),
                projection.getLocation(),
                projection.getNotes());
    }

    private OffsetDateTime parseStartAt(String fecha, String hora) {
        try {
            LocalDate date = LocalDate.parse(fecha.trim());
            LocalTime time = LocalTime.parse(blankToDefault(hora, DEFAULT_TIME).trim());
            return OffsetDateTime.of(date, time, ZoneOffset.UTC);
        } catch (DateTimeParseException ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "fecha/hora must be ISO (yyyy-MM-dd / HH:mm)");
        }
    }

    private String normalizeStatus(String status) {
        String normalized = status == null ? "" : status.trim().toLowerCase(Locale.ROOT);
        return switch (normalized) {
            case "accepted", "aceptado", "aceptada", "confirmed", "confirmada" -> "aceptada";
            case "rejected", "rechazado", "rechazada" -> "rechazada";
            case "completed",
                    "completado",
                    "completada",
                    "finalizado",
                    "finalizada" -> "completada";
            case "cancelled", "cancelado", "cancelada" -> "cancelada";
            case "" -> PENDING_STATUS;
            default -> normalized;
        };
    }

    private String datePart(String value) {
        return value != null && value.length() >= 10 ? value.substring(0, 10) : null;
    }

    private String timePart(String value) {
        return value != null && value.length() >= 16 ? value.substring(11, 16) : null;
    }

    private String fullName(String firstName, String lastName) {
        String first = firstName == null ? "" : firstName.trim();
        String last = lastName == null ? "" : lastName.trim();
        String full = (first + " " + last).trim();
        return full.isBlank() ? null : full;
    }

    private String blankToDefault(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
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

    private UUID parsePrefixedUuid(String value, String prefix) {
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
}
