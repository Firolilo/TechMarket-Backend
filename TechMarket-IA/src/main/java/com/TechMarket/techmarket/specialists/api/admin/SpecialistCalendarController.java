package com.techmarket.techmarket.specialists.api.admin;

import com.techmarket.techmarket.specialists.api.admin.request.CreateCalendarBlockRequest;
import com.techmarket.techmarket.specialists.api.admin.response.MessageResponse;
import com.techmarket.techmarket.specialists.api.admin.response.SpecialistCalendarEntryResponse;
import com.techmarket.techmarket.specialists.application.service.SpecialistIdentitySupport;
import com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.entity.SpecialistCalendarBlockJpaEntity;
import com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.repository.SpecialistAppointmentSummaryProjection;
import com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.repository.SpecialistCalendarBlockSpringDataRepository;
import com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.repository.SpecialistServiceAppointmentSpringDataRepository;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/specialists/calendar")
public class SpecialistCalendarController {

    private final SpecialistIdentitySupport identitySupport;
    private final SpecialistServiceAppointmentSpringDataRepository appointmentRepository;
    private final SpecialistCalendarBlockSpringDataRepository blockRepository;

    public SpecialistCalendarController(
            SpecialistIdentitySupport identitySupport,
            SpecialistServiceAppointmentSpringDataRepository appointmentRepository,
            SpecialistCalendarBlockSpringDataRepository blockRepository) {
        this.identitySupport = identitySupport;
        this.appointmentRepository = appointmentRepository;
        this.blockRepository = blockRepository;
    }

    @GetMapping
    public List<SpecialistCalendarEntryResponse> calendar(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        UUID currentUserId = identitySupport.requireUserId(userId);
        List<SpecialistCalendarEntryResponse> response = new ArrayList<>();
        appointmentRepository.findCalendarByTechnicianUserId(currentUserId).stream()
                .map(this::toAppointmentResponse)
                .forEach(response::add);
        blockRepository.findAllByUserIdOrderByBlockDateAscStartTimeAsc(currentUserId).stream()
                .map(this::toBlockResponse)
                .forEach(response::add);
        return response;
    }

    @PostMapping("/blocks")
    public MessageResponse createBlock(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @Valid @RequestBody CreateCalendarBlockRequest request) {
        UUID currentUserId = identitySupport.requireUserId(userId);
        OffsetDateTime now = OffsetDateTime.now();
        SpecialistCalendarBlockJpaEntity block = new SpecialistCalendarBlockJpaEntity();
        block.setId(UUID.randomUUID());
        block.setUserId(currentUserId);
        block.setBlockDate(parseDate(request.fecha()));
        block.setStartTime(request.hora());
        block.setEndTime(request.fin());
        block.setReason(request.motivo());
        block.setCreatedAt(now);
        block.setUpdatedAt(now);
        blockRepository.save(block);
        return new MessageResponse("Bloque agregado a la agenda");
    }

    @DeleteMapping("/blocks/{blockId}")
    public MessageResponse deleteBlock(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @PathVariable String blockId) {
        UUID currentUserId = identitySupport.requireUserId(userId);
        UUID id = identitySupport.parsePrefixedUuid(blockId, "BLK-");
        SpecialistCalendarBlockJpaEntity block =
                blockRepository
                        .findByIdAndUserId(id, currentUserId)
                        .orElseThrow(
                                () ->
                                        new ResponseStatusException(
                                                HttpStatus.NOT_FOUND, "Calendar block not found"));
        blockRepository.delete(block);
        return new MessageResponse("Bloque eliminado");
    }

    private SpecialistCalendarEntryResponse toAppointmentResponse(
            SpecialistAppointmentSummaryProjection appointment) {
        OffsetDateTime startAt = appointment.getStartAt();
        return new SpecialistCalendarEntryResponse(
                identitySupport.formatProjectId(appointment.getId()),
                fullName(appointment.getCustomerFirstName(), appointment.getCustomerLastName()),
                appointment.getServiceName(),
                startAt == null ? null : startAt.toLocalDate().toString(),
                startAt == null ? null : startAt.toLocalTime().toString(),
                appointment.getStatus(),
                appointment.getLocation() == null ? null : "domicilio");
    }

    private SpecialistCalendarEntryResponse toBlockResponse(
            SpecialistCalendarBlockJpaEntity block) {
        return new SpecialistCalendarEntryResponse(
                identitySupport.formatCalendarBlockId(block.getId()),
                "Bloqueado",
                block.getReason(),
                block.getBlockDate().toString(),
                block.getStartTime(),
                "bloqueado",
                "no_disponible");
    }

    private LocalDate parseDate(String value) {
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "fecha is invalid");
        }
    }

    private String fullName(String firstName, String lastName) {
        String normalizedFirstName = firstName == null ? "" : firstName.trim();
        String normalizedLastName = lastName == null ? "" : lastName.trim();
        String fullName = (normalizedFirstName + " " + normalizedLastName).trim();
        return fullName.isBlank() ? null : fullName;
    }
}
