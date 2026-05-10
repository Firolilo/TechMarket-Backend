package com.techmarket.techmarket.specialists.api.admin;

import com.techmarket.techmarket.specialists.api.admin.request.CreateWithdrawalRequest;
import com.techmarket.techmarket.specialists.api.admin.response.SpecialistEarningsSummaryResponse;
import com.techmarket.techmarket.specialists.api.admin.response.SpecialistTransactionDetailResponse;
import com.techmarket.techmarket.specialists.api.admin.response.SpecialistTransactionResponse;
import com.techmarket.techmarket.specialists.api.admin.response.SpecialistWalletResponse;
import com.techmarket.techmarket.specialists.api.admin.response.WithdrawalResponse;
import com.techmarket.techmarket.specialists.application.service.SpecialistIdentitySupport;
import com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.entity.SpecialistTransactionJpaEntity;
import com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.entity.SpecialistWithdrawalJpaEntity;
import com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.repository.SpecialistTransactionSpringDataRepository;
import com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.repository.SpecialistWithdrawalSpringDataRepository;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/specialists")
public class SpecialistWalletController {

    private final SpecialistIdentitySupport identitySupport;
    private final SpecialistTransactionSpringDataRepository transactionRepository;
    private final SpecialistWithdrawalSpringDataRepository withdrawalRepository;

    public SpecialistWalletController(
            SpecialistIdentitySupport identitySupport,
            SpecialistTransactionSpringDataRepository transactionRepository,
            SpecialistWithdrawalSpringDataRepository withdrawalRepository) {
        this.identitySupport = identitySupport;
        this.transactionRepository = transactionRepository;
        this.withdrawalRepository = withdrawalRepository;
    }

    @GetMapping("/wallet")
    public SpecialistWalletResponse wallet(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        UUID currentUserId = identitySupport.requireUserId(userId);
        return new SpecialistWalletResponse(
                formatMoney(transactionRepository.sumAvailableByUserId(currentUserId), "Bs"),
                formatMoney(transactionRepository.sumCompletedGrossByUserId(currentUserId), "Bs"),
                formatMoney(transactionRepository.sumInProcessByUserId(currentUserId), "Bs"));
    }

    @PostMapping("/wallet/withdraw")
    public WithdrawalResponse withdraw(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @Valid @RequestBody CreateWithdrawalRequest request) {
        UUID currentUserId = identitySupport.requireUserId(userId);
        LocalDate estimatedAt = LocalDate.now().plusDays(3);
        SpecialistWithdrawalJpaEntity withdrawal = new SpecialistWithdrawalJpaEntity();
        withdrawal.setId(UUID.randomUUID());
        withdrawal.setUserId(currentUserId);
        withdrawal.setAmount(request.monto());
        withdrawal.setCurrency("Bs");
        withdrawal.setStatus("solicitado");
        withdrawal.setRequestedAt(OffsetDateTime.now());
        withdrawal.setEstimatedAt(estimatedAt);
        withdrawalRepository.save(withdrawal);
        return new WithdrawalResponse(
                formatMoney(request.monto(), "Bs"), "Solicitud de retiro enviada", estimatedAt.toString());
    }

    @GetMapping("/earnings/summary")
    public SpecialistEarningsSummaryResponse earningsSummary(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        UUID currentUserId = identitySupport.requireUserId(userId);
        BigDecimal total = transactionRepository.sumCompletedGrossByUserId(currentUserId);
        long completedServices = transactionRepository.countCompletedByUserId(currentUserId);
        BigDecimal average =
                completedServices == 0
                        ? BigDecimal.ZERO
                        : safeAmount(total).divide(BigDecimal.valueOf(completedServices), 2, RoundingMode.HALF_UP);
        return new SpecialistEarningsSummaryResponse(
                "mensual",
                formatMoney(total, "Bs"),
                completedServices,
                formatMoney(average, "Bs"));
    }

    @GetMapping("/transactions")
    public List<SpecialistTransactionResponse> transactions(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        UUID currentUserId = identitySupport.requireUserId(userId);
        return transactionRepository.findAllByUserIdOrderByTransactionDateDesc(currentUserId).stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/transactions/{transactionId}")
    public SpecialistTransactionDetailResponse transaction(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @PathVariable String transactionId) {
        UUID currentUserId = identitySupport.requireUserId(userId);
        UUID id = identitySupport.parsePrefixedUuid(transactionId, "TX-");
        SpecialistTransactionJpaEntity transaction =
                transactionRepository
                        .findByIdAndUserId(id, currentUserId)
                        .orElseThrow(
                                () ->
                                        new ResponseStatusException(
                                                HttpStatus.NOT_FOUND, "Transaction not found"));
        return toDetailResponse(transaction);
    }

    private SpecialistTransactionResponse toResponse(SpecialistTransactionJpaEntity transaction) {
        OffsetDateTime transactionDate = transaction.getTransactionDate();
        return new SpecialistTransactionResponse(
                identitySupport.formatTransactionId(transaction.getId()),
                transaction.getServiceName(),
                transaction.getClientName(),
                formatMoney(transaction.getAmount(), transaction.getCurrency()),
                transactionDate == null ? null : transactionDate.toLocalDate().toString(),
                transaction.getStatus());
    }

    private SpecialistTransactionDetailResponse toDetailResponse(
            SpecialistTransactionJpaEntity transaction) {
        BigDecimal amount = safeAmount(transaction.getAmount());
        BigDecimal commission = safeAmount(transaction.getPlatformCommission());
        OffsetDateTime transactionDate = transaction.getTransactionDate();
        return new SpecialistTransactionDetailResponse(
                identitySupport.formatTransactionId(transaction.getId()),
                transaction.getServiceName(),
                transaction.getClientName(),
                formatMoney(amount, transaction.getCurrency()),
                formatMoney(commission, transaction.getCurrency()),
                formatMoney(amount.subtract(commission), transaction.getCurrency()),
                transactionDate == null ? null : transactionDate.toLocalDate().toString(),
                transaction.getStatus());
    }

    private String formatMoney(BigDecimal amount, String currency) {
        String normalizedCurrency = currency == null || currency.isBlank() ? "Bs" : currency;
        return normalizedCurrency + " " + safeAmount(amount).toPlainString();
    }

    private BigDecimal safeAmount(BigDecimal amount) {
        return amount == null ? BigDecimal.ZERO : amount;
    }
}
