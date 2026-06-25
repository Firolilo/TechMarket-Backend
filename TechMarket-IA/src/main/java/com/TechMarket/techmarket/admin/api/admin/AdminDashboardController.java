package com.techmarket.techmarket.admin.api.admin;

import com.techmarket.techmarket.admin.infrastructure.persistence.jpa.entity.AdminAuditLogJpaEntity;
import com.techmarket.techmarket.admin.infrastructure.persistence.jpa.entity.AdminModerationActionJpaEntity;
import com.techmarket.techmarket.admin.infrastructure.persistence.jpa.repository.AdminAuditLogSpringDataRepository;
import com.techmarket.techmarket.admin.infrastructure.persistence.jpa.repository.AdminModerationActionSpringDataRepository;
import com.techmarket.techmarket.reports.infrastructure.persistence.jpa.entity.UserReportJpaEntity;
import com.techmarket.techmarket.reports.infrastructure.persistence.jpa.repository.UserReportSpringDataRepository;
import com.techmarket.techmarket.tenants.infrastructure.persistence.jpa.repository.TenantSpringDataRepository;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.entity.UserJpaEntity;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.repository.UserSpringDataRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
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
@RequestMapping("/api/admin")
public class AdminDashboardController {

    private final UserSpringDataRepository userRepository;
    private final TenantSpringDataRepository tenantRepository;
    private final UserReportSpringDataRepository reportRepository;
    private final AdminModerationActionSpringDataRepository moderationActionRepository;
    private final AdminAuditLogSpringDataRepository auditLogRepository;

    public AdminDashboardController(
            UserSpringDataRepository userRepository,
            TenantSpringDataRepository tenantRepository,
            UserReportSpringDataRepository reportRepository,
            AdminModerationActionSpringDataRepository moderationActionRepository,
            AdminAuditLogSpringDataRepository auditLogRepository) {
        this.userRepository = userRepository;
        this.tenantRepository = tenantRepository;
        this.reportRepository = reportRepository;
        this.moderationActionRepository = moderationActionRepository;
        this.auditLogRepository = auditLogRepository;
    }

    @GetMapping("/dashboard")
    public AdminDashboardResponse dashboard() {
        // TechMarket es plataforma de conexion: no procesa transacciones de venta.
        return new AdminDashboardResponse(
                userRepository.count(),
                tenantRepository.count(),
                reportRepository.countByStatus("pendiente_revision"),
                0L);
    }

    @GetMapping("/users")
    public List<AdminUserResponse> users() {
        return userRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::toAdminUserResponse)
                .toList();
    }

    @PutMapping("/users/{userId}/status")
    public AdminUserStatusResponse updateUserStatus(
            @RequestHeader(value = "X-Admin-User-Id", required = false) String adminUserId,
            @PathVariable String userId,
            @Valid @RequestBody UpdateUserStatusRequest request) {
        UserJpaEntity user =
                userRepository
                        .findById(parsePrefixedUuid(userId, "USR-"))
                        .orElseThrow(
                                () ->
                                        new ResponseStatusException(
                                                HttpStatus.NOT_FOUND, "User not found"));
        user.setStatus(request.estado());
        user.setUpdatedAt(OffsetDateTime.now());
        UserJpaEntity saved = userRepository.save(user);
        saveAuditLog(
                parseOptionalUuid(adminUserId),
                "usuario_" + request.estado(),
                "users",
                saved.getId());
        return new AdminUserStatusResponse("USR-" + saved.getId(), saved.getStatus());
    }

    @GetMapping("/reports")
    public List<AdminReportResponse> reports() {
        return reportRepository.findAllByStatusOrderByCreatedAtDesc("pendiente_revision").stream()
                .map(this::toAdminReportResponse)
                .toList();
    }

    @PutMapping("/reports/{reportId}/status")
    public AdminReportStatusResponse updateReportStatus(
            @RequestHeader(value = "X-Admin-User-Id", required = false) String adminUserId,
            @PathVariable String reportId,
            @Valid @RequestBody UpdateReportStatusRequest request) {
        UserReportJpaEntity report =
                reportRepository
                        .findById(parsePrefixedUuid(reportId, "REP-"))
                        .orElseThrow(
                                () ->
                                        new ResponseStatusException(
                                                HttpStatus.NOT_FOUND, "Report not found"));
        report.setStatus(request.estado());
        report.setAdminAction(request.accion());
        UserReportJpaEntity saved = reportRepository.save(report);
        saveAuditLog(
                parseOptionalUuid(adminUserId),
                "reporte_" + request.estado(),
                saved.getObjectType(),
                objectUuid(saved.getObjectId()));
        return new AdminReportStatusResponse("REP-" + saved.getId(), saved.getStatus());
    }

    @GetMapping("/moderation/queue")
    public List<ModerationQueueItemResponse> moderationQueue() {
        return reportRepository.findAllByStatusOrderByCreatedAtDesc("pendiente_revision").stream()
                .map(
                        report ->
                                new ModerationQueueItemResponse(
                                        "MOD-" + report.getId(),
                                        report.getObjectType(),
                                        report.getObjectId(),
                                        priorityFor(report.getReason())))
                .toList();
    }

    @PostMapping("/moderation/actions")
    @ResponseStatus(HttpStatus.CREATED)
    public ModerationActionResponse createModerationAction(
            @RequestHeader(value = "X-Admin-User-Id", required = false) String adminUserId,
            @Valid @RequestBody CreateModerationActionRequest request) {
        UUID actorId = parseOptionalUuid(adminUserId);
        AdminModerationActionJpaEntity action = new AdminModerationActionJpaEntity();
        action.setId(UUID.randomUUID());
        action.setEntityType(request.tipoObjeto());
        action.setEntityId(objectUuid(request.objetoId()));
        action.setActionType(request.accion());
        action.setReason(request.motivo());
        action.setPerformedByUserId(actorId);
        action.setCreatedAt(OffsetDateTime.now());
        AdminModerationActionJpaEntity saved = moderationActionRepository.save(action);
        saveAuditLog(
                actorId,
                "moderacion_" + request.accion(),
                request.tipoObjeto(),
                action.getEntityId());
        return new ModerationActionResponse("ACT-" + saved.getId(), saved.getActionType());
    }

    @GetMapping("/audit-logs")
    public List<AuditLogResponse> auditLogs() {
        return auditLogRepository.findTop50ByOrderByCreatedAtDesc().stream()
                .map(
                        log ->
                                new AuditLogResponse(
                                        "AUD-" + log.getId(),
                                        log.getUserId() == null ? null : "ADM-" + log.getUserId(),
                                        log.getActionName(),
                                        log.getCreatedAt()))
                .toList();
    }

    private AdminUserResponse toAdminUserResponse(UserJpaEntity user) {
        String fullName =
                ((user.getFirstName() == null ? "" : user.getFirstName())
                                + " "
                                + (user.getLastName() == null ? "" : user.getLastName()))
                        .trim();
        return new AdminUserResponse(
                "USR-" + user.getId(),
                user.getEmail(),
                fullName.isBlank() ? user.getEmail() : fullName,
                "cliente",
                user.getStatus());
    }

    private AdminReportResponse toAdminReportResponse(UserReportJpaEntity report) {
        return new AdminReportResponse(
                "REP-" + report.getId(),
                report.getObjectType(),
                report.getReason(),
                report.getStatus());
    }

    private void saveAuditLog(UUID actorId, String actionName, String entityType, UUID entityId) {
        AdminAuditLogJpaEntity auditLog = new AdminAuditLogJpaEntity();
        auditLog.setId(UUID.randomUUID());
        auditLog.setUserId(actorId);
        auditLog.setActionName(actionName);
        auditLog.setEntityType(entityType);
        auditLog.setEntityId(entityId);
        auditLog.setCreatedAt(OffsetDateTime.now());
        auditLogRepository.save(auditLog);
    }

    private String priorityFor(String reason) {
        String normalized = reason == null ? "" : reason.toLowerCase();
        if (normalized.contains("fraude")
                || normalized.contains("engan")
                || normalized.contains("sospech")) {
            return "alta";
        }
        return "media";
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

    private UUID parseOptionalUuid(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return parsePrefixedUuid(value, "ADM-");
    }

    private UUID objectUuid(String objectId) {
        String normalized = objectId == null ? "" : objectId.trim();
        int separator = normalized.indexOf('-');
        if (separator > 0 && separator + 1 < normalized.length()) {
            normalized = normalized.substring(separator + 1);
        }
        try {
            return UUID.fromString(normalized);
        } catch (IllegalArgumentException ex) {
            return UUID.nameUUIDFromBytes(
                    (objectId == null ? "" : objectId).getBytes(StandardCharsets.UTF_8));
        }
    }

    public record AdminDashboardResponse(
            long usuarios, long empresas, long reportesPendientes, long transaccionesHoy) {}

    public record AdminUserResponse(
            String id, String email, String nombre, String tipo, String estado) {}

    public record UpdateUserStatusRequest(@NotBlank String estado, String motivo) {}

    public record AdminUserStatusResponse(String id, String estado) {}

    public record AdminReportResponse(String id, String tipoObjeto, String motivo, String estado) {}

    public record UpdateReportStatusRequest(@NotBlank String estado, @NotBlank String accion) {}

    public record AdminReportStatusResponse(String id, String estado) {}

    public record ModerationQueueItemResponse(
            String id, String tipo, String objetoId, String prioridad) {}

    public record CreateModerationActionRequest(
            @NotBlank String tipoObjeto,
            @NotBlank String objetoId,
            @NotBlank String accion,
            @NotBlank String motivo) {}

    public record ModerationActionResponse(String id, String accion) {}

    public record AuditLogResponse(
            String id, String actorId, String accion, OffsetDateTime fecha) {}
}
