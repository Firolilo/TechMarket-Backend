package com.techmarket.techmarket.users.api.admin.client;

import com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.entity.SpecialistServiceAppointmentJpaEntity;
import com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.repository.SpecialistServiceAppointmentSpringDataRepository;
import com.techmarket.techmarket.tenants.infrastructure.persistence.jpa.entity.TenantJpaEntity;
import com.techmarket.techmarket.tenants.infrastructure.persistence.jpa.repository.TenantSpringDataRepository;
import com.techmarket.techmarket.users.api.admin.client.request.CreateChatMessageRequest;
import com.techmarket.techmarket.users.api.admin.client.request.CreateChatRequest;
import com.techmarket.techmarket.users.api.admin.client.request.CreateSpecialistChatRequest;
import com.techmarket.techmarket.users.api.admin.client.response.ChatCompanyResponse;
import com.techmarket.techmarket.users.api.admin.client.response.ChatMessageResponse;
import com.techmarket.techmarket.users.api.admin.client.response.ChatSummaryResponse;
import com.techmarket.techmarket.users.api.admin.client.response.CreateChatResponse;
import com.techmarket.techmarket.users.api.admin.client.response.MessageResponse;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.entity.ClientChatJpaEntity;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.entity.ClientChatMessageJpaEntity;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.entity.ClientChatReadReceiptJpaEntity;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.entity.UserJpaEntity;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.repository.ClientChatMessageSpringDataRepository;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.repository.ClientChatReadReceiptSpringDataRepository;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.repository.ClientChatSpringDataRepository;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.repository.UserSpringDataRepository;
import jakarta.validation.Valid;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
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
@RequestMapping("/api/clients/chats")
public class ClientChatController {

    private static final String CHAT_TYPE = "CHAT";
    private static final String OPEN_STATUS = "Abierto";

    private final ClientChatSpringDataRepository chatRepository;
    private final ClientChatMessageSpringDataRepository messageRepository;
    private final ClientChatReadReceiptSpringDataRepository readReceiptRepository;
    private final TenantSpringDataRepository tenantRepository;
    private final SpecialistServiceAppointmentSpringDataRepository appointmentRepository;
    private final UserSpringDataRepository userRepository;

    public ClientChatController(
            ClientChatSpringDataRepository chatRepository,
            ClientChatMessageSpringDataRepository messageRepository,
            ClientChatReadReceiptSpringDataRepository readReceiptRepository,
            TenantSpringDataRepository tenantRepository,
            SpecialistServiceAppointmentSpringDataRepository appointmentRepository,
            UserSpringDataRepository userRepository) {
        this.chatRepository = chatRepository;
        this.messageRepository = messageRepository;
        this.readReceiptRepository = readReceiptRepository;
        this.tenantRepository = tenantRepository;
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<ChatSummaryResponse> chats(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        UUID currentUserId = parseUserId(userId);
        return chatRepository
                .findAllByCustomerUserIdAndTicketTypeOrderByCreatedAtDesc(currentUserId, CHAT_TYPE)
                .stream()
                .map(chat -> toChatSummary(chat, currentUserId))
                .toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateChatResponse createChat(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @Valid @RequestBody CreateChatRequest request) {
        UUID currentUserId = parseUserId(userId);
        UUID tenantId = parsePrefixedUuid(request.empresaId(), "EMP-");
        if (!tenantRepository.existsById(tenantId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Company not found");
        }
        OffsetDateTime now = OffsetDateTime.now();
        ClientChatJpaEntity chat = new ClientChatJpaEntity();
        chat.setId(UUID.randomUUID());
        chat.setTicketCode(formatChatId(chat.getId()));
        chat.setTenantId(tenantId);
        chat.setCustomerUserId(currentUserId);
        chat.setTicketType(CHAT_TYPE);
        chat.setSubject(request.asunto());
        chat.setStatus(OPEN_STATUS);
        chat.setOpenedAt(now);
        chat.setCreatedAt(now);
        ClientChatJpaEntity savedChat = chatRepository.save(chat);
        return new CreateChatResponse(formatChatId(savedChat.getId()), savedChat.getStatus());
    }

    @PostMapping("/specialist")
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public CreateChatResponse createSpecialistChat(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @Valid @RequestBody CreateSpecialistChatRequest request) {
        UUID currentUserId = parseUserId(userId);
        UUID specialistId = parsePrefixedUuid(request.especialistaId(), "USR-");
        OffsetDateTime now = OffsetDateTime.now();

        ClientChatJpaEntity chat = new ClientChatJpaEntity();
        chat.setId(UUID.randomUUID());
        chat.setTicketCode(formatChatId(chat.getId()));
        chat.setCustomerUserId(currentUserId);
        chat.setAssignedTechnicianUserId(specialistId);
        chat.setTicketType(CHAT_TYPE);
        chat.setSubject(request.asunto());
        chat.setStatus(OPEN_STATUS);
        chat.setOpenedAt(now);
        chat.setCreatedAt(now);
        ClientChatJpaEntity savedChat = chatRepository.save(chat);

        SpecialistServiceAppointmentJpaEntity appointment =
                new SpecialistServiceAppointmentJpaEntity();
        appointment.setId(UUID.randomUUID());
        appointment.setTicketId(savedChat.getId());
        appointment.setAssignedTechnicianUserId(specialistId);
        appointment.setStatus("pendiente");
        appointment.setStartAt(now);
        appointmentRepository.save(appointment);

        return new CreateChatResponse(formatChatId(savedChat.getId()), savedChat.getStatus());
    }

    @GetMapping("/{chatId}/messages")
    public List<ChatMessageResponse> messages(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @PathVariable String chatId) {
        UUID currentUserId = parseUserId(userId);
        ClientChatJpaEntity chat = findChat(chatId, currentUserId);
        return messageRepository.findAllByTicketIdOrderByCreatedAtAsc(chat.getId()).stream()
                .filter(message -> !Boolean.FALSE.equals(message.getVisibleToCustomer()))
                .map(message -> toMessageResponse(message, currentUserId))
                .toList();
    }

    @PostMapping("/{chatId}/messages")
    @ResponseStatus(HttpStatus.CREATED)
    public ChatMessageResponse createMessage(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @PathVariable String chatId,
            @Valid @RequestBody CreateChatMessageRequest request) {
        UUID currentUserId = parseUserId(userId);
        ClientChatJpaEntity chat = findChat(chatId, currentUserId);
        ClientChatMessageJpaEntity message = new ClientChatMessageJpaEntity();
        message.setId(UUID.randomUUID());
        message.setTicketId(chat.getId());
        message.setAuthorUserId(currentUserId);
        message.setMessageBody(request.contenido());
        message.setMessageType("TEXT");
        message.setVisibleToCustomer(true);
        message.setCreatedAt(OffsetDateTime.now());
        return toMessageResponse(messageRepository.save(message), currentUserId);
    }

    @PutMapping("/{chatId}/read")
    public MessageResponse markAsRead(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @PathVariable String chatId) {
        UUID currentUserId = parseUserId(userId);
        ClientChatJpaEntity chat = findChat(chatId, currentUserId);
        ClientChatReadReceiptJpaEntity receipt =
                readReceiptRepository
                        .findByTicketIdAndUserId(chat.getId(), currentUserId)
                        .orElseGet(
                                () -> {
                                    ClientChatReadReceiptJpaEntity created =
                                            new ClientChatReadReceiptJpaEntity();
                                    created.setId(UUID.randomUUID());
                                    created.setTicketId(chat.getId());
                                    created.setUserId(currentUserId);
                                    return created;
                                });
        receipt.setReadAt(OffsetDateTime.now());
        readReceiptRepository.save(receipt);
        return new MessageResponse("Chat marcado como leído");
    }

    private ChatSummaryResponse toChatSummary(ClientChatJpaEntity chat, UUID currentUserId) {
        String displayName = null;
        if (chat.getTenantId() != null) {
            displayName =
                    tenantRepository
                            .findById(chat.getTenantId())
                            .map(TenantJpaEntity::getBusinessName)
                            .orElse(null);
        } else if (chat.getAssignedTechnicianUserId() != null) {
            displayName =
                    userRepository
                            .findById(chat.getAssignedTechnicianUserId())
                            .map(this::fullName)
                            .orElse(null);
        }
        String lastMessage =
                messageRepository
                        .findTopByTicketIdOrderByCreatedAtDesc(chat.getId())
                        .map(ClientChatMessageJpaEntity::getMessageBody)
                        .orElse(chat.getSubject());
        int unreadMessages = unreadMessages(chat.getId(), currentUserId);
        return new ChatSummaryResponse(
                formatChatId(chat.getId()),
                new ChatCompanyResponse(displayName),
                lastMessage,
                unreadMessages);
    }

    private String fullName(UserJpaEntity user) {
        String first = user.getFirstName() == null ? "" : user.getFirstName().trim();
        String last = user.getLastName() == null ? "" : user.getLastName().trim();
        String full = (first + " " + last).trim();
        return full.isBlank() ? user.getEmail() : full;
    }

    private int unreadMessages(UUID chatId, UUID currentUserId) {
        return readReceiptRepository
                .findByTicketIdAndUserId(chatId, currentUserId)
                .map(ClientChatReadReceiptJpaEntity::getReadAt)
                .map(
                        readAt ->
                                messageRepository
                                        .countByTicketIdAndAuthorUserIdNotAndCreatedAtAfter(
                                                chatId, currentUserId, readAt))
                .orElseGet(
                        () ->
                                messageRepository.countByTicketIdAndAuthorUserIdNot(
                                        chatId, currentUserId))
                .intValue();
    }

    private ChatMessageResponse toMessageResponse(
            ClientChatMessageJpaEntity message, UUID currentUserId) {
        return new ChatMessageResponse(
                formatMessageId(message.getId()),
                currentUserId.equals(message.getAuthorUserId()) ? "cliente" : "empresa",
                message.getMessageBody(),
                message.getCreatedAt());
    }

    private ClientChatJpaEntity findChat(String chatId, UUID userId) {
        return chatRepository
                .findByIdAndCustomerUserId(parsePrefixedUuid(chatId, "CHT-"), userId)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Chat not found"));
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

    private String formatChatId(UUID id) {
        return "CHT-" + id;
    }

    private String formatMessageId(UUID id) {
        return "MSG-" + id;
    }
}
