package com.techmarket.techmarket.conversations.api.admin;

import com.techmarket.techmarket.tenants.infrastructure.persistence.jpa.repository.TenantSpringDataRepository;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.entity.ClientChatJpaEntity;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.entity.ClientChatMessageJpaEntity;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.entity.ClientChatReadReceiptJpaEntity;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.repository.ClientChatMessageSpringDataRepository;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.repository.ClientChatReadReceiptSpringDataRepository;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.repository.ClientChatSpringDataRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
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
@RequestMapping("/api")
public class ConversationController {

    private static final String CHAT_TYPE = "CHAT";
    private static final String OPEN_STATUS = "Abierta";

    private final ClientChatSpringDataRepository conversationRepository;
    private final ClientChatMessageSpringDataRepository messageRepository;
    private final ClientChatReadReceiptSpringDataRepository readReceiptRepository;
    private final TenantSpringDataRepository tenantRepository;

    public ConversationController(
            ClientChatSpringDataRepository conversationRepository,
            ClientChatMessageSpringDataRepository messageRepository,
            ClientChatReadReceiptSpringDataRepository readReceiptRepository,
            TenantSpringDataRepository tenantRepository) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.readReceiptRepository = readReceiptRepository;
        this.tenantRepository = tenantRepository;
    }

    @GetMapping("/conversations")
    public List<ConversationSummaryResponse> conversations(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        UUID currentUserId = parseUserId(userId);
        return conversationRepository.findAllUserConversations(currentUserId, CHAT_TYPE).stream()
                .map(conversation -> toConversationSummary(conversation, currentUserId))
                .toList();
    }

    @PostMapping("/conversations")
    @ResponseStatus(HttpStatus.CREATED)
    public CreateConversationResponse createConversation(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @Valid @RequestBody CreateConversationRequest request) {
        UUID currentUserId = parseUserId(userId);
        UUID participantId = parseParticipantId(request.participanteId(), request.tipo());
        OffsetDateTime now = OffsetDateTime.now();
        ClientChatJpaEntity conversation = new ClientChatJpaEntity();
        conversation.setId(UUID.randomUUID());
        conversation.setTicketCode(formatConversationId(conversation.getId()));
        conversation.setCustomerUserId(currentUserId);
        conversation.setTicketType(CHAT_TYPE);
        conversation.setSubject(defaultSubject(request.tipo()));
        conversation.setStatus(OPEN_STATUS);
        conversation.setOpenedAt(now);
        conversation.setCreatedAt(now);
        if (request.tipo().toLowerCase().contains("especialista")) {
            conversation.setAssignedTechnicianUserId(participantId);
        } else {
            if (!tenantRepository.existsById(participantId)) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Participant not found");
            }
            conversation.setTenantId(participantId);
        }
        ClientChatJpaEntity savedConversation = conversationRepository.save(conversation);

        ClientChatMessageJpaEntity initialMessage = new ClientChatMessageJpaEntity();
        initialMessage.setId(UUID.randomUUID());
        initialMessage.setTicketId(savedConversation.getId());
        initialMessage.setAuthorUserId(currentUserId);
        initialMessage.setMessageBody(request.mensajeInicial());
        initialMessage.setMessageType("TEXT");
        initialMessage.setVisibleToCustomer(true);
        initialMessage.setCreatedAt(now);
        messageRepository.save(initialMessage);

        return new CreateConversationResponse(
                formatConversationId(savedConversation.getId()), savedConversation.getStatus());
    }

    @GetMapping("/conversations/{conversationId}/messages")
    public List<ConversationMessageResponse> messages(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @PathVariable String conversationId) {
        UUID currentUserId = parseUserId(userId);
        ClientChatJpaEntity conversation = findConversation(conversationId, currentUserId);
        return messageRepository.findAllByTicketIdOrderByCreatedAtAsc(conversation.getId()).stream()
                .map(this::toMessageResponse)
                .toList();
    }

    @PostMapping("/conversations/{conversationId}/messages")
    @ResponseStatus(HttpStatus.CREATED)
    public CreateConversationMessageResponse createMessage(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @PathVariable String conversationId,
            @Valid @RequestBody CreateConversationMessageRequest request) {
        UUID currentUserId = parseUserId(userId);
        ClientChatJpaEntity conversation = findConversation(conversationId, currentUserId);
        ClientChatMessageJpaEntity message = new ClientChatMessageJpaEntity();
        message.setId(UUID.randomUUID());
        message.setTicketId(conversation.getId());
        message.setAuthorUserId(currentUserId);
        message.setMessageBody(request.contenido());
        message.setMessageType("TEXT");
        message.setVisibleToCustomer(true);
        message.setCreatedAt(OffsetDateTime.now());
        ClientChatMessageJpaEntity saved = messageRepository.save(message);
        return new CreateConversationMessageResponse(
                formatMessageId(saved.getId()), saved.getMessageBody(), "enviado");
    }

    @PutMapping("/conversations/{conversationId}/read")
    public MessageResponse markAsRead(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @PathVariable String conversationId) {
        UUID currentUserId = parseUserId(userId);
        ClientChatJpaEntity conversation = findConversation(conversationId, currentUserId);
        ClientChatReadReceiptJpaEntity receipt =
                readReceiptRepository
                        .findByTicketIdAndUserId(conversation.getId(), currentUserId)
                        .orElseGet(
                                () -> {
                                    ClientChatReadReceiptJpaEntity created =
                                            new ClientChatReadReceiptJpaEntity();
                                    created.setId(UUID.randomUUID());
                                    created.setTicketId(conversation.getId());
                                    created.setUserId(currentUserId);
                                    return created;
                                });
        receipt.setReadAt(OffsetDateTime.now());
        readReceiptRepository.save(receipt);
        return new MessageResponse("Conversacion marcada como leida");
    }

    @DeleteMapping("/messages/{messageId}")
    public MessageResponse deleteMessage(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @PathVariable String messageId) {
        ClientChatMessageJpaEntity message =
                messageRepository
                        .findByIdAndAuthorUserId(
                                parsePrefixedUuid(messageId, "MSG-"), parseUserId(userId))
                        .orElseThrow(
                                () ->
                                        new ResponseStatusException(
                                                HttpStatus.NOT_FOUND, "Message not found"));
        messageRepository.delete(message);
        return new MessageResponse("Mensaje eliminado");
    }

    private ConversationSummaryResponse toConversationSummary(
            ClientChatJpaEntity conversation, UUID currentUserId) {
        String lastMessage =
                messageRepository
                        .findTopByTicketIdOrderByCreatedAtDesc(conversation.getId())
                        .map(ClientChatMessageJpaEntity::getMessageBody)
                        .orElse(conversation.getSubject());
        return new ConversationSummaryResponse(
                formatConversationId(conversation.getId()),
                conversation.getSubject(),
                lastMessage,
                unreadMessages(conversation.getId(), currentUserId));
    }

    private int unreadMessages(UUID conversationId, UUID currentUserId) {
        return readReceiptRepository
                .findByTicketIdAndUserId(conversationId, currentUserId)
                .map(ClientChatReadReceiptJpaEntity::getReadAt)
                .map(
                        readAt ->
                                messageRepository
                                        .countByTicketIdAndAuthorUserIdNotAndCreatedAtAfter(
                                                conversationId, currentUserId, readAt))
                .orElseGet(
                        () ->
                                messageRepository.countByTicketIdAndAuthorUserIdNot(
                                        conversationId, currentUserId))
                .intValue();
    }

    private ConversationMessageResponse toMessageResponse(ClientChatMessageJpaEntity message) {
        return new ConversationMessageResponse(
                formatMessageId(message.getId()),
                formatUserId(message.getAuthorUserId()),
                message.getMessageBody(),
                message.getCreatedAt());
    }

    private ClientChatJpaEntity findConversation(String conversationId, UUID userId) {
        return conversationRepository
                .findUserConversation(parsePrefixedUuid(conversationId, "CONV-"), userId)
                .orElseThrow(
                        () ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND, "Conversation not found"));
    }

    private UUID parseParticipantId(String participantId, String tipo) {
        if (tipo != null && tipo.toLowerCase().contains("especialista")) {
            return parsePrefixedUuid(participantId, "TEC-");
        }
        return parsePrefixedUuid(participantId, "EMP-");
    }

    private String defaultSubject(String tipo) {
        String normalized = tipo == null ? "general" : tipo.trim().replace('_', ' ');
        return "Conversacion " + normalized;
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

    private String formatConversationId(UUID id) {
        return "CONV-" + id;
    }

    private String formatMessageId(UUID id) {
        return "MSG-" + id;
    }

    private String formatUserId(UUID id) {
        return "USR-" + id;
    }

    public record ConversationSummaryResponse(
            String id, String titulo, String ultimoMensaje, int mensajesSinLeer) {}

    public record CreateConversationRequest(
            @NotBlank String participanteId, @NotBlank String tipo, @NotBlank String mensajeInicial) {}

    public record CreateConversationResponse(String id, String estado) {}

    public record ConversationMessageResponse(
            String id, String remitenteId, String contenido, OffsetDateTime fecha) {}

    public record CreateConversationMessageRequest(@NotBlank String contenido) {}

    public record CreateConversationMessageResponse(String id, String contenido, String estado) {}

    public record MessageResponse(String mensaje) {}
}
