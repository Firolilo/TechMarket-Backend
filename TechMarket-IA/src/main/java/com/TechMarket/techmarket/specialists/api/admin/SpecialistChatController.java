package com.techmarket.techmarket.specialists.api.admin;

import com.techmarket.techmarket.specialists.api.admin.request.CreateSpecialistChatFileRequest;
import com.techmarket.techmarket.specialists.api.admin.request.CreateSpecialistChatMessageRequest;
import com.techmarket.techmarket.specialists.api.admin.response.CreateSpecialistChatFileResponse;
import com.techmarket.techmarket.specialists.api.admin.response.CreateSpecialistChatMessageResponse;
import com.techmarket.techmarket.specialists.api.admin.response.SpecialistChatFileResponse;
import com.techmarket.techmarket.specialists.api.admin.response.SpecialistChatMessageResponse;
import com.techmarket.techmarket.specialists.api.admin.response.SpecialistChatSummaryResponse;
import com.techmarket.techmarket.specialists.application.service.SpecialistIdentitySupport;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.entity.ClientChatAttachmentJpaEntity;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.entity.ClientChatJpaEntity;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.entity.ClientChatMessageJpaEntity;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.entity.ClientChatReadReceiptJpaEntity;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.entity.UserJpaEntity;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.repository.ClientChatAttachmentSpringDataRepository;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.repository.ClientChatMessageSpringDataRepository;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.repository.ClientChatReadReceiptSpringDataRepository;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.repository.ClientChatSpringDataRepository;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.repository.UserSpringDataRepository;
import jakarta.validation.Valid;
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
@RequestMapping("/api/specialists/chats")
public class SpecialistChatController {

    private static final String CHAT_TYPE = "CHAT";

    private final SpecialistIdentitySupport identitySupport;
    private final ClientChatSpringDataRepository chatRepository;
    private final ClientChatMessageSpringDataRepository messageRepository;
    private final ClientChatReadReceiptSpringDataRepository readReceiptRepository;
    private final ClientChatAttachmentSpringDataRepository attachmentRepository;
    private final UserSpringDataRepository userRepository;

    public SpecialistChatController(
            SpecialistIdentitySupport identitySupport,
            ClientChatSpringDataRepository chatRepository,
            ClientChatMessageSpringDataRepository messageRepository,
            ClientChatReadReceiptSpringDataRepository readReceiptRepository,
            ClientChatAttachmentSpringDataRepository attachmentRepository,
            UserSpringDataRepository userRepository) {
        this.identitySupport = identitySupport;
        this.chatRepository = chatRepository;
        this.messageRepository = messageRepository;
        this.readReceiptRepository = readReceiptRepository;
        this.attachmentRepository = attachmentRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<SpecialistChatSummaryResponse> chats(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        UUID currentUserId = identitySupport.requireUserId(userId);
        return chatRepository
                .findAllByAssignedTechnicianUserIdAndTicketTypeOrderByCreatedAtDesc(
                        currentUserId, CHAT_TYPE)
                .stream()
                .map(chat -> toSummary(chat, currentUserId))
                .toList();
    }

    @GetMapping("/{chatId}")
    public List<SpecialistChatMessageResponse> messages(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @PathVariable String chatId) {
        UUID currentUserId = identitySupport.requireUserId(userId);
        ClientChatJpaEntity chat = findChat(chatId, currentUserId);
        return messageRepository.findAllByTicketIdOrderByCreatedAtAsc(chat.getId()).stream()
                .map(message -> toMessageResponse(message, currentUserId))
                .toList();
    }

    @PostMapping("/{chatId}/messages")
    @ResponseStatus(HttpStatus.CREATED)
    public CreateSpecialistChatMessageResponse createMessage(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @PathVariable String chatId,
            @Valid @RequestBody CreateSpecialistChatMessageRequest request) {
        UUID currentUserId = identitySupport.requireUserId(userId);
        ClientChatJpaEntity chat = findChat(chatId, currentUserId);
        OffsetDateTime now = OffsetDateTime.now();
        ClientChatMessageJpaEntity message = new ClientChatMessageJpaEntity();
        message.setId(UUID.randomUUID());
        message.setTicketId(chat.getId());
        message.setAuthorUserId(currentUserId);
        message.setMessageBody(request.contenido());
        message.setMessageType(request.tipo() == null ? "texto" : request.tipo());
        message.setVisibleToCustomer(true);
        message.setCreatedAt(now);
        ClientChatMessageJpaEntity saved = messageRepository.save(message);
        return new CreateSpecialistChatMessageResponse(
                identitySupport.formatMessageId(saved.getId()), saved.getCreatedAt());
    }

    @GetMapping("/{chatId}/files")
    public List<SpecialistChatFileResponse> files(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @PathVariable String chatId) {
        UUID currentUserId = identitySupport.requireUserId(userId);
        ClientChatJpaEntity chat = findChat(chatId, currentUserId);
        return attachmentRepository.findAllByTicketIdOrderByCreatedAtDesc(chat.getId()).stream()
                .map(this::toFileResponse)
                .toList();
    }

    @PostMapping("/{chatId}/files")
    @ResponseStatus(HttpStatus.CREATED)
    public CreateSpecialistChatFileResponse createFile(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @PathVariable String chatId,
            @Valid @RequestBody CreateSpecialistChatFileRequest request) {
        UUID currentUserId = identitySupport.requireUserId(userId);
        ClientChatJpaEntity chat = findChat(chatId, currentUserId);
        ClientChatAttachmentJpaEntity attachment = new ClientChatAttachmentJpaEntity();
        attachment.setId(UUID.randomUUID());
        attachment.setTicketId(chat.getId());
        attachment.setFileUrl(request.url());
        attachment.setOriginalFileName(request.nombre());
        attachment.setFileType(request.tipo());
        attachment.setFileSize(request.tamano());
        attachment.setUploadedByUserId(currentUserId);
        attachment.setCreatedAt(OffsetDateTime.now());
        ClientChatAttachmentJpaEntity saved = attachmentRepository.save(attachment);
        return new CreateSpecialistChatFileResponse(
                identitySupport.formatFileId(saved.getId()), saved.getFileUrl());
    }

    private SpecialistChatSummaryResponse toSummary(ClientChatJpaEntity chat, UUID currentUserId) {
        ClientChatMessageJpaEntity lastMessage =
                messageRepository.findTopByTicketIdOrderByCreatedAtDesc(chat.getId()).orElse(null);
        String messageBody = lastMessage == null ? chat.getSubject() : lastMessage.getMessageBody();
        OffsetDateTime lastActivity =
                lastMessage == null ? chat.getCreatedAt() : lastMessage.getCreatedAt();
        return new SpecialistChatSummaryResponse(
                identitySupport.formatChatId(chat.getId()),
                clientName(chat.getCustomerUserId()),
                messageBody,
                unreadMessages(chat.getId(), currentUserId),
                lastActivity);
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

    private SpecialistChatMessageResponse toMessageResponse(
            ClientChatMessageJpaEntity message, UUID currentUserId) {
        return new SpecialistChatMessageResponse(
                identitySupport.formatMessageId(message.getId()),
                currentUserId.equals(message.getAuthorUserId()) ? "tecnico" : "cliente",
                message.getMessageBody(),
                message.getCreatedAt(),
                message.getMessageType() == null ? "texto" : message.getMessageType());
    }

    private SpecialistChatFileResponse toFileResponse(ClientChatAttachmentJpaEntity attachment) {
        OffsetDateTime createdAt = attachment.getCreatedAt();
        return new SpecialistChatFileResponse(
                identitySupport.formatFileId(attachment.getId()),
                attachment.getOriginalFileName(),
                attachment.getFileSize(),
                createdAt == null ? null : createdAt.toLocalDate().toString());
    }

    private ClientChatJpaEntity findChat(String chatId, UUID currentUserId) {
        UUID id = identitySupport.parsePrefixedUuid(chatId, "CHT-");
        return chatRepository
                .findByIdAndAssignedTechnicianUserId(id, currentUserId)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Chat not found"));
    }

    private String clientName(UUID clientId) {
        if (clientId == null) {
            return null;
        }
        return userRepository.findById(clientId).map(this::fullName).orElse(null);
    }

    private String fullName(UserJpaEntity user) {
        String firstName = user.getFirstName() == null ? "" : user.getFirstName().trim();
        String lastName = user.getLastName() == null ? "" : user.getLastName().trim();
        String fullName = (firstName + " " + lastName).trim();
        return fullName.isBlank() ? user.getEmail() : fullName;
    }
}
