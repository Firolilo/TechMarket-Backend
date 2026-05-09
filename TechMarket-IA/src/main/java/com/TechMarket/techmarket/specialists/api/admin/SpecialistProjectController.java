package com.techmarket.techmarket.specialists.api.admin;

import com.techmarket.techmarket.specialists.api.admin.request.UpdateProjectStatusRequest;
import com.techmarket.techmarket.specialists.api.admin.response.SpecialistProjectClientResponse;
import com.techmarket.techmarket.specialists.api.admin.response.SpecialistProjectDetailResponse;
import com.techmarket.techmarket.specialists.api.admin.response.SpecialistProjectHistoryResponse;
import com.techmarket.techmarket.specialists.api.admin.response.SpecialistProjectResponse;
import com.techmarket.techmarket.specialists.api.admin.response.SpecialistProjectStatusResponse;
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
@RequestMapping("/api/specialists/projects")
public class SpecialistProjectController {

    private final SpecialistIdentitySupport identitySupport;
    private final SpecialistServiceAppointmentSpringDataRepository appointmentRepository;

    public SpecialistProjectController(
            SpecialistIdentitySupport identitySupport,
            SpecialistServiceAppointmentSpringDataRepository appointmentRepository) {
        this.identitySupport = identitySupport;
        this.appointmentRepository = appointmentRepository;
    }

    @GetMapping
    public List<SpecialistProjectResponse> projects(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        UUID currentUserId = identitySupport.requireUserId(userId);
        return appointmentRepository.findActiveProjectsByTechnicianUserId(currentUserId).stream()
                .map(this::toProjectResponse)
                .toList();
    }

    @GetMapping("/{projectId}")
    public SpecialistProjectDetailResponse project(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @PathVariable String projectId) {
        UUID currentUserId = identitySupport.requireUserId(userId);
        UUID id = identitySupport.parsePrefixedUuid(projectId, "PROJ-");
        SpecialistAppointmentSummaryProjection project =
                appointmentRepository
                        .findSummaryByIdAndTechnicianUserId(id, currentUserId)
                        .orElseThrow(
                                () ->
                                        new ResponseStatusException(
                                                HttpStatus.NOT_FOUND, "Project not found"));
        return toDetailResponse(project);
    }

    @PatchMapping("/{projectId}/status")
    @Transactional
    public SpecialistProjectStatusResponse updateStatus(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @PathVariable String projectId,
            @Valid @RequestBody UpdateProjectStatusRequest request) {
        UUID currentUserId = identitySupport.requireUserId(userId);
        UUID id = identitySupport.parsePrefixedUuid(projectId, "PROJ-");
        SpecialistServiceAppointmentJpaEntity project =
                appointmentRepository
                        .findByIdAndAssignedTechnicianUserId(id, currentUserId)
                        .orElseThrow(
                                () ->
                                        new ResponseStatusException(
                                                HttpStatus.NOT_FOUND, "Project not found"));
        String status = normalizeProjectStatus(request.estado());
        project.setStatus(status);
        appointmentRepository.save(project);
        return new SpecialistProjectStatusResponse(
                status, "Proyecto marcado como " + status.replace('_', ' '));
    }

    @GetMapping("/history")
    public List<SpecialistProjectHistoryResponse> history(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        UUID currentUserId = identitySupport.requireUserId(userId);
        return appointmentRepository.findProjectHistoryByTechnicianUserId(currentUserId).stream()
                .map(this::toHistoryResponse)
                .toList();
    }

    private SpecialistProjectResponse toProjectResponse(
            SpecialistAppointmentSummaryProjection project) {
        OffsetDateTime startAt = project.getStartAt();
        return new SpecialistProjectResponse(
                identitySupport.formatProjectId(project.getId()),
                fullName(project.getCustomerFirstName(), project.getCustomerLastName()),
                project.getServiceName(),
                project.getStatus(),
                startAt == null ? null : startAt.toLocalDate().toString());
    }

    private SpecialistProjectDetailResponse toDetailResponse(
            SpecialistAppointmentSummaryProjection project) {
        OffsetDateTime startAt = project.getStartAt();
        return new SpecialistProjectDetailResponse(
                identitySupport.formatProjectId(project.getId()),
                new SpecialistProjectClientResponse(
                        fullName(project.getCustomerFirstName(), project.getCustomerLastName()),
                        project.getCustomerPhone()),
                project.getServiceName(),
                startAt == null ? null : startAt.toLocalDate().toString(),
                project.getStatus(),
                project.getDescription() == null ? project.getNotes() : project.getDescription());
    }

    private SpecialistProjectHistoryResponse toHistoryResponse(
            SpecialistAppointmentSummaryProjection project) {
        OffsetDateTime startAt = project.getStartAt();
        return new SpecialistProjectHistoryResponse(
                identitySupport.formatProjectId(project.getId()),
                fullName(project.getCustomerFirstName(), project.getCustomerLastName()),
                project.getServiceName(),
                startAt == null ? null : startAt.toLocalDate().toString(),
                "Bs 0.00");
    }

    private String normalizeProjectStatus(String status) {
        String normalized = status.trim().toLowerCase(Locale.ROOT);
        if ("en_progreso".equals(normalized)
                || "completado".equals(normalized)
                || "cancelado".equals(normalized)) {
            return normalized;
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "estado is invalid");
    }

    private String fullName(String firstName, String lastName) {
        String normalizedFirstName = firstName == null ? "" : firstName.trim();
        String normalizedLastName = lastName == null ? "" : lastName.trim();
        String fullName = (normalizedFirstName + " " + normalizedLastName).trim();
        return fullName.isBlank() ? null : fullName;
    }
}
