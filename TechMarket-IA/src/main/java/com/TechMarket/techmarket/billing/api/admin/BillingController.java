package com.techmarket.techmarket.billing.api.admin;

import com.techmarket.techmarket.billing.infrastructure.persistence.jpa.entity.UserInvoiceJpaEntity;
import com.techmarket.techmarket.billing.infrastructure.persistence.jpa.entity.UserPaymentRefundJpaEntity;
import com.techmarket.techmarket.billing.infrastructure.persistence.jpa.repository.UserInvoiceSpringDataRepository;
import com.techmarket.techmarket.billing.infrastructure.persistence.jpa.repository.UserPaymentRefundSpringDataRepository;
import com.techmarket.techmarket.payments.infrastructure.persistence.jpa.entity.UserTransactionJpaEntity;
import com.techmarket.techmarket.payments.infrastructure.persistence.jpa.repository.UserTransactionSpringDataRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
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

@RestController
@RequestMapping("/api")
public class BillingController {

    private static final String REFUND_PENDING = "pendiente_revision";

    private final UserInvoiceSpringDataRepository invoiceRepository;
    private final UserPaymentRefundSpringDataRepository refundRepository;
    private final UserTransactionSpringDataRepository transactionRepository;

    public BillingController(
            UserInvoiceSpringDataRepository invoiceRepository,
            UserPaymentRefundSpringDataRepository refundRepository,
            UserTransactionSpringDataRepository transactionRepository) {
        this.invoiceRepository = invoiceRepository;
        this.refundRepository = refundRepository;
        this.transactionRepository = transactionRepository;
    }

    @GetMapping("/invoices")
    public List<InvoiceResponse> invoices(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        UUID currentUserId = parseUserId(userId);
        return invoiceRepository.findAllByUserIdOrderByCreatedAtDesc(currentUserId).stream()
                .map(this::toInvoiceResponse)
                .toList();
    }

    @GetMapping("/invoices/{invoiceId}/download")
    public InvoiceDownloadResponse downloadInvoice(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @PathVariable String invoiceId) {
        UserInvoiceJpaEntity invoice =
                invoiceRepository
                        .findByIdAndUserId(parsePrefixedUuid(invoiceId, "INV-"), parseUserId(userId))
                        .orElseThrow(
                                () ->
                                        new ResponseStatusException(
                                                HttpStatus.NOT_FOUND, "Invoice not found"));
        return new InvoiceDownloadResponse(invoice.getDownloadUrl());
    }

    @PostMapping("/payments/refunds")
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public RefundResponse refund(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @Valid @RequestBody RefundRequest request) {
        UUID currentUserId = parseUserId(userId);
        UserTransactionJpaEntity transaction =
                transactionRepository
                        .findByIdAndUserId(
                                parsePrefixedUuid(request.transactionId(), "TRX-"), currentUserId)
                        .orElseThrow(
                                () ->
                                        new ResponseStatusException(
                                                HttpStatus.NOT_FOUND, "Transaction not found"));
        UserPaymentRefundJpaEntity refund = new UserPaymentRefundJpaEntity();
        refund.setId(UUID.randomUUID());
        refund.setUserId(currentUserId);
        refund.setTransactionId(transaction.getId());
        refund.setReason(request.motivo());
        refund.setStatus(REFUND_PENDING);
        refund.setCreatedAt(OffsetDateTime.now());
        UserPaymentRefundJpaEntity saved = refundRepository.save(refund);
        return new RefundResponse(formatRefundId(saved.getId()), saved.getStatus());
    }

    private InvoiceResponse toInvoiceResponse(UserInvoiceJpaEntity invoice) {
        return new InvoiceResponse(
                formatInvoiceId(invoice.getId()),
                invoice.getTransactionId() == null ? null : formatTransactionId(invoice.getTransactionId()),
                invoice.getInvoiceNumber(),
                invoice.getAmount());
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

    private String formatInvoiceId(UUID id) {
        return "INV-" + id;
    }

    private String formatTransactionId(UUID id) {
        return "TRX-" + id;
    }

    private String formatRefundId(UUID id) {
        return "RFD-" + id;
    }

    public record InvoiceResponse(String id, String transactionId, String numero, BigDecimal monto) {}

    public record InvoiceDownloadResponse(String downloadUrl) {}

    public record RefundRequest(@NotBlank String transactionId, @NotBlank String motivo) {}

    public record RefundResponse(String refundId, String estado) {}
}
