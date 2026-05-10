package com.techmarket.techmarket.ambassadors.api.admin;

import com.techmarket.techmarket.ambassadors.api.admin.request.CreateAmbassadorWithdrawalRequest;
import com.techmarket.techmarket.ambassadors.api.admin.response.AmbassadorCommissionResponse;
import com.techmarket.techmarket.ambassadors.api.admin.response.AmbassadorCommissionSummaryResponse;
import com.techmarket.techmarket.ambassadors.api.admin.response.AmbassadorPayoutResponse;
import com.techmarket.techmarket.ambassadors.api.admin.response.AmbassadorWalletResponse;
import com.techmarket.techmarket.ambassadors.api.admin.response.AmbassadorWithdrawalResponse;
import com.techmarket.techmarket.ambassadors.application.service.AmbassadorIdentitySupport;
import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.entity.AmbassadorWithdrawalJpaEntity;
import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.repository.AmbassadorCommissionSpringDataRepository;
import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.repository.AmbassadorPayoutItemSpringDataRepository;
import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.repository.AmbassadorWithdrawalSpringDataRepository;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/// Endpoints de Ingresos para el módulo Embajador.
/// Cubre comisiones, wallet, retiros y pagos (endpoints 95–104).
@RestController
@RequestMapping("/api/ambassadors")
public class AmbassadorEarningsController {

    private final AmbassadorIdentitySupport identitySupport;
    private final AmbassadorCommissionSpringDataRepository commissionRepository;
    private final AmbassadorWithdrawalSpringDataRepository withdrawalRepository;
    private final AmbassadorPayoutItemSpringDataRepository payoutItemRepository;

    public AmbassadorEarningsController(
            AmbassadorIdentitySupport identitySupport,
            AmbassadorCommissionSpringDataRepository commissionRepository,
            AmbassadorWithdrawalSpringDataRepository withdrawalRepository,
            AmbassadorPayoutItemSpringDataRepository payoutItemRepository) {
        this.identitySupport = identitySupport;
        this.commissionRepository = commissionRepository;
        this.withdrawalRepository = withdrawalRepository;
        this.payoutItemRepository = payoutItemRepository;
    }

    // ── Endpoint 95: GET /api/ambassadors/commissions ──────────────────────
    @GetMapping("/commissions")
    public List<AmbassadorCommissionResponse> listCommissions(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        UUID ambassadorId = identitySupport.requireAmbassadorId(userId);
        return commissionRepository
                .findAllByAmbassadorIdOrderByGeneratedAtDesc(ambassadorId)
                .stream()
                .map(
                        c ->
                                new AmbassadorCommissionResponse(
                                        identitySupport.formatCommissionId(c.getId()),
                                        c.getGeneratedAt() != null
                                                ? c.getGeneratedAt()
                                                        .format(
                                                                DateTimeFormatter
                                                                        .ISO_OFFSET_DATE_TIME)
                                                : null,
                                        normalizeEventType(c.getEventType()),
                                        normalizeReferenceType(c.getReferenceType()),
                                        buildDescription(c.getEventType(), c.getReferenceType()),
                                        parseAmount(c.getAmount()),
                                        parseAmount(c.getAmount()) * 20.0,
                                        c.getStatus() != null ? c.getStatus() : "pendiente",
                                        "pagado".equalsIgnoreCase(c.getStatus())
                                                || "disponible".equalsIgnoreCase(c.getStatus()),
                                        levelFromAttributionType(c.getAttributionType())))
                .toList();
    }

    // ── Endpoint 96: GET /api/ambassadors/commissions/summary ──────────────
    @GetMapping("/commissions/summary")
    public AmbassadorCommissionSummaryResponse commissionsSummary(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        UUID ambassadorId = identitySupport.requireAmbassadorId(userId);
        var commissions =
                commissionRepository.findAllByAmbassadorIdOrderByGeneratedAtDesc(ambassadorId);
        double total = commissions.stream().mapToDouble(c -> parseAmount(c.getAmount())).sum();
        double disponible =
                commissions.stream()
                        .filter(c -> "disponible".equalsIgnoreCase(c.getStatus()))
                        .mapToDouble(c -> parseAmount(c.getAmount()))
                        .sum();
        double pendiente =
                commissions.stream()
                        .filter(c -> "pendiente".equalsIgnoreCase(c.getStatus()))
                        .mapToDouble(c -> parseAmount(c.getAmount()))
                        .sum();
        double pagado =
                commissions.stream()
                        .filter(c -> "pagado".equalsIgnoreCase(c.getStatus()))
                        .mapToDouble(c -> parseAmount(c.getAmount()))
                        .sum();
        return new AmbassadorCommissionSummaryResponse(
                formatMoney(total),
                formatMoney(disponible),
                formatMoney(pendiente),
                formatMoney(pagado));
    }

    // ── Endpoint 99: GET /api/ambassadors/wallet ────────────────────────────
    @GetMapping("/wallet")
    public AmbassadorWalletResponse wallet(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        UUID ambassadorId = identitySupport.requireAmbassadorId(userId);
        var commissions =
                commissionRepository.findAllByAmbassadorIdOrderByGeneratedAtDesc(ambassadorId);
        double disponible =
                commissions.stream()
                        .filter(c -> "disponible".equalsIgnoreCase(c.getStatus()))
                        .mapToDouble(c -> parseAmount(c.getAmount()))
                        .sum();
        double pendiente =
                commissions.stream()
                        .filter(c -> "pendiente".equalsIgnoreCase(c.getStatus()))
                        .mapToDouble(c -> parseAmount(c.getAmount()))
                        .sum();
        var withdrawals =
                withdrawalRepository.findAllByAmbassadorIdOrderByRequestedAtDesc(ambassadorId);
        double retirado =
                withdrawals.stream()
                        .filter(
                                w ->
                                        "pagado".equalsIgnoreCase(w.getStatus())
                                                || "completado".equalsIgnoreCase(w.getStatus()))
                        .mapToDouble(w -> w.getAmount() != null ? w.getAmount().doubleValue() : 0.0)
                        .sum();
        return new AmbassadorWalletResponse(
                formatMoney(disponible), formatMoney(pendiente), formatMoney(retirado));
    }

    // ── Endpoint 100: POST /api/ambassadors/wallet/withdraw ────────────────
    @PostMapping("/wallet/withdraw")
    @ResponseStatus(HttpStatus.CREATED)
    public AmbassadorWithdrawalResponse requestWithdrawal(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @Valid @RequestBody CreateAmbassadorWithdrawalRequest request) {
        UUID ambassadorId = identitySupport.requireAmbassadorId(userId);
        LocalDate estimatedAt = LocalDate.now().plusDays(3);
        AmbassadorWithdrawalJpaEntity withdrawal = new AmbassadorWithdrawalJpaEntity();
        withdrawal.setId(UUID.randomUUID());
        withdrawal.setAmbassadorId(ambassadorId);
        withdrawal.setAmount(request.monto());
        withdrawal.setCurrency("Bs");
        withdrawal.setStatus("pendiente");
        withdrawal.setRequestedAt(OffsetDateTime.now());
        withdrawal.setEstimatedAt(estimatedAt);
        withdrawalRepository.save(withdrawal);
        return new AmbassadorWithdrawalResponse(
                identitySupport.formatWithdrawalId(withdrawal.getId()),
                formatMoney(request.monto().doubleValue()),
                "pendiente",
                estimatedAt.toString());
    }

    // ── Endpoint 101: GET /api/ambassadors/payouts ─────────────────────────
    @GetMapping("/payouts")
    public List<AmbassadorPayoutResponse> payouts(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        UUID ambassadorId = identitySupport.requireAmbassadorId(userId);
        return withdrawalRepository
                .findAllByAmbassadorIdOrderByRequestedAtDesc(ambassadorId)
                .stream()
                .map(
                        w ->
                                new AmbassadorPayoutResponse(
                                        identitySupport.formatWithdrawalId(w.getId()),
                                        w.getAmount() != null ? w.getAmount().doubleValue() : 0.0,
                                        "Transferencia – "
                                                + (w.getCurrency() != null
                                                        ? w.getCurrency()
                                                        : "Bs"),
                                        w.getStatus() != null ? w.getStatus() : "pendiente",
                                        w.getRequestedAt() != null
                                                ? w.getRequestedAt()
                                                        .format(
                                                                DateTimeFormatter
                                                                        .ISO_OFFSET_DATE_TIME)
                                                : null,
                                        w.getEstimatedAt() != null
                                                ? w.getEstimatedAt().toString()
                                                : null))
                .toList();
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

    private String normalizeEventType(String eventType) {
        if (eventType == null) return "venta";
        return switch (eventType.toLowerCase()) {
            case "subscription", "suscripcion" -> "suscripcion";
            case "service", "servicio" -> "servicio";
            case "registration", "registro_activo", "registroactivo" -> "registroActivo";
            default -> "venta";
        };
    }

    private String normalizeReferenceType(String referenceType) {
        if (referenceType == null) return "hardware";
        return switch (referenceType.toLowerCase()) {
            case "software" -> "software";
            case "servicios", "services" -> "servicios";
            default -> "hardware";
        };
    }

    private String buildDescription(String eventType, String referenceType) {
        String tipo = normalizeEventType(eventType);
        String origen = normalizeReferenceType(referenceType);
        return "Comisión " + tipo + " – " + origen;
    }

    private int levelFromAttributionType(String attributionType) {
        if (attributionType == null) return 1;
        try {
            String digits = attributionType.replaceAll("\\D", "");
            return digits.isEmpty() ? 1 : Integer.parseInt(digits);
        } catch (NumberFormatException ex) {
            return 1;
        }
    }
}
