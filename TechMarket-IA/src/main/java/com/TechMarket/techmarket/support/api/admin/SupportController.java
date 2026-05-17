package com.techmarket.techmarket.support.api.admin;

import com.techmarket.techmarket.users.infrastructure.persistence.jpa.entity.ClientChatJpaEntity;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.entity.ClientChatMessageJpaEntity;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.repository.ClientChatMessageSpringDataRepository;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.repository.ClientChatSpringDataRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
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
@RequestMapping("/api/support/tickets")
public class SupportController {

    private static final String SUPPORT_TYPE = "SUPPORT";
    private static final String OPEN_STATUS = "abierto";

    private final ClientChatSpringDataRepository ticketRepository;
    private final ClientChatMessageSpringDataRepository messageRepository;

    public SupportController(
            ClientChatSpringDataRepository ticketRepository,
            ClientChatMessageSpringDataRepository messageRepository) {
        this.ticketRepository = ticketRepository;
        this.messageRepository = messageRepository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateSupportTicketResponse createTicket(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @Valid @RequestBody CreateSupportTicketRequest request) {
        UUID currentUserId = parseUserId(userId);
        OffsetDateTime now = OffsetDateTime.now();
        ClientChatJpaEntity ticket = new ClientChatJpaEntity();
        ticket.setId(UUID.randomUUID());
        ticket.setTicketCode(formatTicketId(ticket.getId()));
        ticket.setCustomerUserId(currentUserId);
        ticket.setTicketType(SUPPORT_TYPE);
        ticket.setPriority(request.categoria());
        ticket.setSubject(request.asunto());
        ticket.setDescription(request.descripcion());
        ticket.setStatus(OPEN_STATUS);
        ticket.setOpenedAt(now);
        ticket.setCreatedAt(now);
        ClientChatJpaEntity saved = ticketRepository.save(ticket);
        return new CreateSupportTicketResponse(formatTicketId(saved.getId()), saved.getStatus());
    }

    @GetMapping
    public List<SupportTicketSummaryResponse> tickets(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        UUID currentUserId = parseUserId(userId);
        return ticketRepository
                .findAllByCustomerUserIdAndTicketTypeOrderByCreatedAtDesc(
                        currentUserId, SUPPORT_TYPE)
                .stream()
                .map(
                        ticket ->
                                new SupportTicketSummaryResponse(
                                        formatTicketId(ticket.getId()),
                                        ticket.getSubject(),
                                        ticket.getStatus(),
                                        ticket.getCreatedAt()))
                .toList();
    }

    @GetMapping("/{ticketId}")
    public SupportTicketDetailResponse ticket(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @PathVariable String ticketId) {
        ClientChatJpaEntity ticket = findTicket(ticketId, parseUserId(userId));
        return new SupportTicketDetailResponse(
                formatTicketId(ticket.getId()),
                ticket.getSubject(),
                ticket.getDescription(),
                ticket.getStatus());
    }

    @PostMapping("/{ticketId}/messages")
    @ResponseStatus(HttpStatus.CREATED)
    public CreateSupportTicketMessageResponse createMessage(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @PathVariable String ticketId,
            @Valid @RequestBody CreateSupportTicketMessageRequest request) {
        UUID currentUserId = parseUserId(userId);
        ClientChatJpaEntity ticket = findTicket(ticketId, currentUserId);
        ClientChatMessageJpaEntity message = new ClientChatMessageJpaEntity();
        message.setId(UUID.randomUUID());
        message.setTicketId(ticket.getId());
        message.setAuthorUserId(currentUserId);
        message.setMessageBody(request.contenido());
        message.setMessageType("TEXT");
        message.setVisibleToCustomer(true);
        message.setCreatedAt(OffsetDateTime.now());
        ClientChatMessageJpaEntity saved = messageRepository.save(message);
        return new CreateSupportTicketMessageResponse(
                formatTicketMessageId(saved.getId()), "Respuesta enviada");
    }

    @PutMapping("/{ticketId}/status")
    public SupportTicketStatusResponse updateStatus(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @PathVariable String ticketId,
            @Valid @RequestBody UpdateSupportTicketStatusRequest request) {
        ClientChatJpaEntity ticket = findTicket(ticketId, parseUserId(userId));
        ticket.setStatus(request.estado());
        ClientChatJpaEntity saved = ticketRepository.save(ticket);
        return new SupportTicketStatusResponse(formatTicketId(saved.getId()), saved.getStatus());
    }

    private ClientChatJpaEntity findTicket(String ticketId, UUID userId) {
        return ticketRepository
                .findByIdAndCustomerUserIdAndTicketType(
                        parsePrefixedUuid(ticketId, "TCK-"), userId, SUPPORT_TYPE)
                .orElseThrow(
                        () ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND, "Support ticket not found"));
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

    private String formatTicketId(UUID id) {
        return "TCK-" + id;
    }

    private String formatTicketMessageId(UUID id) {
        return "TMSG-" + id;
    }

    public record CreateSupportTicketRequest(
            @NotBlank String categoria, @NotBlank String asunto, @NotBlank String descripcion) {}

    public record CreateSupportTicketResponse(String id, String estado) {}

    public record SupportTicketSummaryResponse(
            String id, String asunto, String estado, OffsetDateTime fecha) {}

    public record SupportTicketDetailResponse(
            String id, String asunto, String descripcion, String estado) {}

    public record CreateSupportTicketMessageRequest(@NotBlank String contenido) {}

    public record CreateSupportTicketMessageResponse(String id, String mensaje) {}

    public record UpdateSupportTicketStatusRequest(@NotBlank String estado) {}

    public record SupportTicketStatusResponse(String id, String estado) {}
}
