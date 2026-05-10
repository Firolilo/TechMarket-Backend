package com.techmarket.techmarket.reports.api.admin;

import com.techmarket.techmarket.reports.infrastructure.persistence.jpa.entity.UserReportJpaEntity;
import com.techmarket.techmarket.reports.infrastructure.persistence.jpa.repository.UserReportSpringDataRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private static final String PENDING_REVIEW = "pendiente_revision";

    private final UserReportSpringDataRepository reportRepository;

    public ReportController(UserReportSpringDataRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateReportResponse createReport(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @Valid @RequestBody CreateReportRequest request) {
        UserReportJpaEntity report = new UserReportJpaEntity();
        report.setId(UUID.randomUUID());
        report.setUserId(parseUserId(userId));
        report.setObjectType(request.tipoObjeto());
        report.setObjectId(request.objetoId());
        report.setReason(request.motivo());
        report.setDescription(request.descripcion());
        report.setStatus(PENDING_REVIEW);
        report.setCreatedAt(OffsetDateTime.now());
        UserReportJpaEntity saved = reportRepository.save(report);
        return new CreateReportResponse(formatReportId(saved.getId()), saved.getStatus());
    }

    @GetMapping
    public List<ReportSummaryResponse> reports(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        return reportRepository.findAllByUserIdOrderByCreatedAtDesc(parseUserId(userId)).stream()
                .map(
                        report ->
                                new ReportSummaryResponse(
                                        formatReportId(report.getId()),
                                        report.getObjectType(),
                                        report.getReason(),
                                        report.getStatus()))
                .toList();
    }

    @GetMapping("/{reportId}")
    public ReportDetailResponse report(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @PathVariable String reportId) {
        UserReportJpaEntity report =
                reportRepository
                        .findByIdAndUserId(parsePrefixedUuid(reportId, "REP-"), parseUserId(userId))
                        .orElseThrow(
                                () ->
                                        new ResponseStatusException(
                                                HttpStatus.NOT_FOUND, "Report not found"));
        return new ReportDetailResponse(
                formatReportId(report.getId()),
                report.getObjectType(),
                report.getObjectId(),
                report.getStatus());
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

    private String formatReportId(UUID id) {
        return "REP-" + id;
    }

    public record CreateReportRequest(
            @NotBlank String tipoObjeto,
            @NotBlank String objetoId,
            @NotBlank String motivo,
            String descripcion) {}

    public record CreateReportResponse(String id, String estado) {}

    public record ReportSummaryResponse(
            String id, String tipoObjeto, String motivo, String estado) {}

    public record ReportDetailResponse(
            String id, String tipoObjeto, String objetoId, String estado) {}
}
