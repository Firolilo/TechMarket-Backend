package com.techmarket.techmarket.payments.api.admin;

import com.techmarket.techmarket.billing.infrastructure.persistence.jpa.entity.UserInvoiceJpaEntity;
import com.techmarket.techmarket.billing.infrastructure.persistence.jpa.repository.UserInvoiceSpringDataRepository;
import com.techmarket.techmarket.payments.infrastructure.persistence.jpa.entity.UserPaymentIntentJpaEntity;
import com.techmarket.techmarket.payments.infrastructure.persistence.jpa.entity.UserPaymentMethodJpaEntity;
import com.techmarket.techmarket.payments.infrastructure.persistence.jpa.entity.UserTransactionJpaEntity;
import com.techmarket.techmarket.payments.infrastructure.persistence.jpa.repository.UserPaymentIntentSpringDataRepository;
import com.techmarket.techmarket.payments.infrastructure.persistence.jpa.repository.UserPaymentMethodSpringDataRepository;
import com.techmarket.techmarket.payments.infrastructure.persistence.jpa.repository.UserTransactionSpringDataRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
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
public class PaymentController {

    private static final String PENDING = "pendiente";
    private static final String APPROVED = "aprobado";

    private final UserPaymentMethodSpringDataRepository paymentMethodRepository;
    private final UserPaymentIntentSpringDataRepository paymentIntentRepository;
    private final UserTransactionSpringDataRepository transactionRepository;
    private final UserInvoiceSpringDataRepository invoiceRepository;

    public PaymentController(
            UserPaymentMethodSpringDataRepository paymentMethodRepository,
            UserPaymentIntentSpringDataRepository paymentIntentRepository,
            UserTransactionSpringDataRepository transactionRepository,
            UserInvoiceSpringDataRepository invoiceRepository) {
        this.paymentMethodRepository = paymentMethodRepository;
        this.paymentIntentRepository = paymentIntentRepository;
        this.transactionRepository = transactionRepository;
        this.invoiceRepository = invoiceRepository;
    }

    @GetMapping("/payments/methods")
    public List<PaymentMethodResponse> paymentMethods(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        return paymentMethodRepository
                .findAllByUserIdOrderByCreatedAtDesc(parseUserId(userId))
                .stream()
                .map(this::toPaymentMethodResponse)
                .toList();
    }

    @PostMapping("/payments/methods")
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public CreatePaymentMethodResponse createPaymentMethod(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @Valid @RequestBody CreatePaymentMethodRequest request) {
        UUID currentUserId = parseUserId(userId);
        if (request.predeterminado()) {
            paymentMethodRepository
                    .findAllByUserIdOrderByCreatedAtDesc(currentUserId)
                    .forEach(
                            method -> {
                                method.setDefaultMethod(false);
                                paymentMethodRepository.save(method);
                            });
        }
        UserPaymentMethodJpaEntity paymentMethod = new UserPaymentMethodJpaEntity();
        paymentMethod.setId(UUID.randomUUID());
        paymentMethod.setUserId(currentUserId);
        paymentMethod.setMethodType(request.tipo());
        paymentMethod.setTokenReference(request.token());
        paymentMethod.setBrand(resolveBrand(request.token()));
        paymentMethod.setLast4(resolveLast4(request.token()));
        paymentMethod.setDefaultMethod(request.predeterminado());
        paymentMethod.setCreatedAt(OffsetDateTime.now());
        UserPaymentMethodJpaEntity saved = paymentMethodRepository.save(paymentMethod);
        return new CreatePaymentMethodResponse(
                formatPaymentMethodId(saved.getId()), "Metodo de pago agregado");
    }

    @DeleteMapping("/payments/methods/{paymentMethodId}")
    public MessageResponse deletePaymentMethod(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @PathVariable String paymentMethodId) {
        paymentMethodRepository.delete(findPaymentMethod(paymentMethodId, parseUserId(userId)));
        return new MessageResponse("Metodo de pago eliminado");
    }

    @PostMapping("/payments/intents")
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentIntentResponse createPaymentIntent(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @Valid @RequestBody CreatePaymentIntentRequest request) {
        UUID currentUserId = parseUserId(userId);
        UserPaymentMethodJpaEntity paymentMethod =
                findPaymentMethod(request.metodoPagoId(), currentUserId);
        UserPaymentIntentJpaEntity intent = new UserPaymentIntentJpaEntity();
        intent.setId(UUID.randomUUID());
        intent.setUserId(currentUserId);
        intent.setReferenceId(request.referenciaId());
        intent.setAmount(request.monto());
        intent.setCurrency(request.moneda());
        intent.setPaymentMethodId(paymentMethod.getId());
        intent.setStatus(PENDING);
        intent.setCreatedAt(OffsetDateTime.now());
        UserPaymentIntentJpaEntity saved = paymentIntentRepository.save(intent);
        return new PaymentIntentResponse(
                formatPaymentIntentId(saved.getId()), saved.getStatus(), saved.getAmount());
    }

    @PostMapping("/payments/confirm")
    @Transactional
    public PaymentConfirmResponse confirmPayment(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @Valid @RequestBody ConfirmPaymentRequest request) {
        UUID currentUserId = parseUserId(userId);
        UserPaymentIntentJpaEntity intent =
                paymentIntentRepository
                        .findByIdAndUserId(
                                parsePrefixedUuid(request.paymentIntentId(), "PAY-"), currentUserId)
                        .orElseThrow(
                                () ->
                                        new ResponseStatusException(
                                                HttpStatus.NOT_FOUND, "Payment intent not found"));
        intent.setStatus(APPROVED);
        intent.setConfirmedAt(OffsetDateTime.now());
        paymentIntentRepository.save(intent);

        UserTransactionJpaEntity transaction = new UserTransactionJpaEntity();
        transaction.setId(UUID.randomUUID());
        transaction.setUserId(currentUserId);
        transaction.setPaymentIntentId(intent.getId());
        transaction.setConcept("compra_marketplace");
        transaction.setAmount(intent.getAmount());
        transaction.setCurrency(intent.getCurrency());
        transaction.setStatus(APPROVED);
        transaction.setCreatedAt(OffsetDateTime.now());
        UserTransactionJpaEntity saved = transactionRepository.save(transaction);
        createInvoice(saved);
        return new PaymentConfirmResponse(
                formatTransactionId(saved.getId()), saved.getStatus(), saved.getAmount());
    }

    @GetMapping("/transactions")
    public List<TransactionSummaryResponse> transactions(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        return transactionRepository
                .findAllByUserIdOrderByCreatedAtDesc(parseUserId(userId))
                .stream()
                .map(this::toTransactionSummaryResponse)
                .toList();
    }

    @GetMapping("/transactions/{transactionId}")
    public TransactionDetailResponse transaction(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @PathVariable String transactionId) {
        UserTransactionJpaEntity transaction =
                transactionRepository
                        .findByIdAndUserId(
                                parsePrefixedUuid(transactionId, "TRX-"), parseUserId(userId))
                        .orElseThrow(
                                () ->
                                        new ResponseStatusException(
                                                HttpStatus.NOT_FOUND, "Transaction not found"));
        return new TransactionDetailResponse(
                formatTransactionId(transaction.getId()),
                transaction.getAmount(),
                transaction.getCurrency(),
                transaction.getStatus(),
                transaction.getCreatedAt());
    }

    private UserPaymentMethodJpaEntity findPaymentMethod(String paymentMethodId, UUID userId) {
        return paymentMethodRepository
                .findByIdAndUserId(parsePrefixedUuid(paymentMethodId, "PM-"), userId)
                .orElseThrow(
                        () ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND, "Payment method not found"));
    }

    private PaymentMethodResponse toPaymentMethodResponse(UserPaymentMethodJpaEntity method) {
        return new PaymentMethodResponse(
                formatPaymentMethodId(method.getId()),
                method.getMethodType(),
                method.getBrand(),
                method.getLast4(),
                method.isDefaultMethod());
    }

    private TransactionSummaryResponse toTransactionSummaryResponse(
            UserTransactionJpaEntity transaction) {
        return new TransactionSummaryResponse(
                formatTransactionId(transaction.getId()),
                transaction.getConcept(),
                transaction.getAmount(),
                transaction.getCurrency(),
                transaction.getStatus());
    }

    private void createInvoice(UserTransactionJpaEntity transaction) {
        OffsetDateTime now = OffsetDateTime.now();
        UserInvoiceJpaEntity invoice = new UserInvoiceJpaEntity();
        invoice.setId(UUID.randomUUID());
        invoice.setUserId(transaction.getUserId());
        invoice.setTransactionId(transaction.getId());
        invoice.setInvoiceNumber(
                "TM-" + now.format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")));
        invoice.setAmount(transaction.getAmount());
        invoice.setCurrency(transaction.getCurrency());
        invoice.setDownloadUrl("https://techmarket.bo/invoices/" + invoice.getId() + ".pdf");
        invoice.setCreatedAt(now);
        invoiceRepository.save(invoice);
    }

    private String resolveBrand(String token) {
        String normalized = token == null ? "" : token.toLowerCase(Locale.ROOT);
        if (normalized.contains("visa")) {
            return "Visa";
        }
        if (normalized.contains("mastercard")) {
            return "Mastercard";
        }
        return "Tarjeta";
    }

    private String resolveLast4(String token) {
        String digits = token == null ? "" : token.replaceAll("\\D", "");
        if (digits.length() < 4) {
            return "0000";
        }
        return digits.substring(digits.length() - 4);
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

    private String formatPaymentMethodId(UUID id) {
        return "PM-" + id;
    }

    private String formatPaymentIntentId(UUID id) {
        return "PAY-" + id;
    }

    private String formatTransactionId(UUID id) {
        return "TRX-" + id;
    }

    public record PaymentMethodResponse(
            String id, String tipo, String marca, String ultimos4, boolean predeterminado) {}

    public record CreatePaymentMethodRequest(
            @NotBlank String tipo, @NotBlank String token, boolean predeterminado) {}

    public record CreatePaymentMethodResponse(String id, String mensaje) {}

    public record CreatePaymentIntentRequest(
            @NotBlank String referenciaId,
            @NotNull @Positive BigDecimal monto,
            @NotBlank String moneda,
            @NotBlank String metodoPagoId) {}

    public record PaymentIntentResponse(String paymentIntentId, String estado, BigDecimal monto) {}

    public record ConfirmPaymentRequest(@NotBlank String paymentIntentId) {}

    public record PaymentConfirmResponse(String transactionId, String estado, BigDecimal monto) {}

    public record TransactionSummaryResponse(
            String id, String concepto, BigDecimal monto, String moneda, String estado) {}

    public record TransactionDetailResponse(
            String id, BigDecimal monto, String moneda, String estado, OffsetDateTime fecha) {}

    public record MessageResponse(String mensaje) {}
}
