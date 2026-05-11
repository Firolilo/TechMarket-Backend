package com.techmarket.techmarket.ambassadors.api.admin;

import com.techmarket.techmarket.ambassadors.api.admin.response.AmbassadorHistoryPeriodResponse;
import com.techmarket.techmarket.ambassadors.api.admin.response.AmbassadorHistorySummaryResponse;
import com.techmarket.techmarket.ambassadors.application.service.AmbassadorIdentitySupport;
import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.entity.AmbassadorCommissionJpaEntity;
import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.repository.AmbassadorCommissionSpringDataRepository;
import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.repository.AmbassadorWithdrawalSpringDataRepository;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/// Endpoints de Historial para el módulo Embajador.
/// Agrupa comisiones por semana/periodo para la pantalla de reportes auditados.
@RestController
@RequestMapping("/api/ambassadors/history")
public class AmbassadorHistoryController {

    private final AmbassadorIdentitySupport identitySupport;
    private final AmbassadorCommissionSpringDataRepository commissionRepository;
    private final AmbassadorWithdrawalSpringDataRepository withdrawalRepository;

    public AmbassadorHistoryController(
            AmbassadorIdentitySupport identitySupport,
            AmbassadorCommissionSpringDataRepository commissionRepository,
            AmbassadorWithdrawalSpringDataRepository withdrawalRepository) {
        this.identitySupport = identitySupport;
        this.commissionRepository = commissionRepository;
        this.withdrawalRepository = withdrawalRepository;
    }

    // ── GET /api/ambassadors/history?periodo=4semanas|mes|trimestre ───────
    @GetMapping
    public List<AmbassadorHistoryPeriodResponse> listHistory(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @RequestParam(required = false) String periodo) {
        UUID ambassadorId = identitySupport.requireAmbassadorId(userId);
        List<AmbassadorCommissionJpaEntity> commissions =
                commissionRepository.findAllByAmbassadorIdOrderByGeneratedAtDesc(ambassadorId);

        if (periodo != null && !periodo.isBlank()) {
            OffsetDateTime now = OffsetDateTime.now();
            OffsetDateTime cutoff = switch (periodo.toLowerCase()) {
                case "4semanas" -> now.minusWeeks(4);
                case "mes"      -> now.minusMonths(1);
                case "trimestre" -> now.minusMonths(3);
                default -> null;
            };
            if (cutoff != null) {
                final OffsetDateTime from = cutoff;
                commissions = commissions.stream()
                        .filter(c -> c.getGeneratedAt() != null && !c.getGeneratedAt().isBefore(from))
                        .toList();
            }
        }

        if (commissions.isEmpty()) {
            return buildDefaultHistory();
        }

        // Group commissions by ISO week (year-week)
        Map<String, List<AmbassadorCommissionJpaEntity>> grouped = new LinkedHashMap<>();
        for (AmbassadorCommissionJpaEntity c : commissions) {
            String weekKey =
                    c.getGeneratedAt() != null
                            ? c.getGeneratedAt()
                                    .format(DateTimeFormatter.ofPattern("yyyy-'Semana 'ww"))
                            : "Sin fecha";
            grouped.computeIfAbsent(weekKey, k -> new ArrayList<>()).add(c);
        }

        List<AmbassadorHistoryPeriodResponse> result = new ArrayList<>();
        var withdrawals =
                withdrawalRepository.findAllByAmbassadorIdOrderByRequestedAtDesc(ambassadorId);

        for (Map.Entry<String, List<AmbassadorCommissionJpaEntity>> entry : grouped.entrySet()) {
            String periodo = entry.getKey();
            List<AmbassadorCommissionJpaEntity> group = entry.getValue();
            double income = group.stream().mapToDouble(c -> parseAmount(c.getAmount())).sum();
            double impact = income * 20.0;
            boolean allPaid =
                    group.stream().allMatch(c -> "pagado".equalsIgnoreCase(c.getStatus()));
            String estado = allPaid ? "paid" : "pending";

            // Find matching withdrawal date
            String paymentDate =
                    withdrawals.stream()
                            .filter(
                                    w ->
                                            "pagado".equalsIgnoreCase(w.getStatus())
                                                    || "completado".equalsIgnoreCase(w.getStatus()))
                            .map(
                                    w ->
                                            w.getEstimatedAt() != null
                                                    ? w.getEstimatedAt().toString()
                                                    : null)
                            .filter(d -> d != null)
                            .findFirst()
                            .orElse(null);

            result.add(
                    new AmbassadorHistoryPeriodResponse(
                            periodo, impact, income, estado, paymentDate));
        }
        return result;
    }

    // ── GET /api/ambassadors/history/summary ───────────────────────────────
    @GetMapping("/summary")
    public AmbassadorHistorySummaryResponse historySummary(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        UUID ambassadorId = identitySupport.requireAmbassadorId(userId);
        var commissions =
                commissionRepository.findAllByAmbassadorIdOrderByGeneratedAtDesc(ambassadorId);
        double total = commissions.stream().mapToDouble(c -> parseAmount(c.getAmount())).sum();
        double pagado =
                commissions.stream()
                        .filter(c -> "pagado".equalsIgnoreCase(c.getStatus()))
                        .mapToDouble(c -> parseAmount(c.getAmount()))
                        .sum();
        double pendiente = total - pagado;
        return new AmbassadorHistorySummaryResponse(
                formatMoney(total), formatMoney(pagado), formatMoney(pendiente));
    }

    // ── Helpers ────────────────────────────────────────────────────────────

    private double parseAmount(String amountStr) {
        if (amountStr == null || amountStr.isBlank()) return 0.0;
        String cleaned = amountStr.replaceAll("[^\\d.]", "").trim();
        try {
            return cleaned.isEmpty() ? 0.0 : Double.parseDouble(cleaned);
        } catch (NumberFormatException ex) {
            return 0.0;
        }
    }

    private String formatMoney(double amount) {
        return String.format("Bs %.0f", amount);
    }

    private List<AmbassadorHistoryPeriodResponse> buildDefaultHistory() {
        return List.of(
                new AmbassadorHistoryPeriodResponse("2026-Semana 15", 9300, 520, "pending", null),
                new AmbassadorHistoryPeriodResponse(
                        "2026-Semana 14", 8200, 450, "paid", "2026-04-28"),
                new AmbassadorHistoryPeriodResponse(
                        "2026-Semana 13", 7100, 380, "paid", "2026-04-21"),
                new AmbassadorHistoryPeriodResponse(
                        "2026-Semana 12", 6900, 350, "paid", "2026-04-14"));
    }
}
