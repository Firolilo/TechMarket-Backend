package com.techmarket.techmarket.empresa.api.admin;

import com.techmarket.techmarket.tenants.infrastructure.persistence.jpa.entity.TenantJpaEntity;
import com.techmarket.techmarket.tenants.infrastructure.persistence.jpa.repository.TenantSpringDataRepository;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.entity.ClientChatJpaEntity;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.entity.ClientChatMessageJpaEntity;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.repository.ClientChatMessageSpringDataRepository;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.repository.ClientChatSpringDataRepository;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.repository.UserSpringDataRepository;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/empresa/chat")
public class EmpresaChatController {

    private final TenantSpringDataRepository tenantRepository;
    private final ClientChatSpringDataRepository chatRepository;
    private final ClientChatMessageSpringDataRepository messageRepository;
    private final UserSpringDataRepository userRepository;

    public EmpresaChatController(
            TenantSpringDataRepository tenantRepository,
            ClientChatSpringDataRepository chatRepository,
            ClientChatMessageSpringDataRepository messageRepository,
            UserSpringDataRepository userRepository) {
        this.tenantRepository = tenantRepository;
        this.chatRepository = chatRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/conversaciones")
    public ConversacionesResponse conversaciones() {
        UUID userId = resolveAuthenticatedUserId();
        TenantJpaEntity tenant = requireTenant(userId);
        UUID tenantId = tenant.getId();

        List<ClientChatJpaEntity> chats = chatRepository.findAllByTenantId(tenantId);
        List<ChatThreadResponse> threads = new ArrayList<>();

        for (ClientChatJpaEntity chat : chats) {
            List<ClientChatMessageJpaEntity> messages =
                    messageRepository.findAllByTicketIdOrderByCreatedAtAsc(chat.getId());

            ClientChatMessageJpaEntity lastMsg =
                    messages.isEmpty() ? null : messages.get(messages.size() - 1);

            String customerName = resolveUserName(chat.getCustomerUserId());
            String avatar = initials(customerName);
            String lastMessageText =
                    lastMsg != null && lastMsg.getMessageBody() != null
                            ? lastMsg.getMessageBody()
                            : "";
            String relativeTime =
                    lastMsg != null
                            ? formatRelativeTime(lastMsg.getCreatedAt())
                            : formatRelativeTime(chat.getCreatedAt());

            long unread =
                    messages.stream()
                            .filter(
                                    m ->
                                            chat.getCustomerUserId() != null
                                                    && chat.getCustomerUserId()
                                                            .equals(m.getAuthorUserId()))
                            .count();

            List<ChatMessageResponse> msgResponses =
                    messages.stream()
                            .map(
                                    m -> {
                                        String author =
                                                chat.getCustomerUserId() != null
                                                                && chat.getCustomerUserId()
                                                                        .equals(m.getAuthorUserId())
                                                        ? "cliente"
                                                        : "empresa";
                                        String time =
                                                m.getCreatedAt() != null
                                                        ? m.getCreatedAt()
                                                                .format(
                                                                        DateTimeFormatter.ofPattern(
                                                                                "HH:mm"))
                                                        : "";
                                        return new ChatMessageResponse(
                                                "msg-" + m.getId(),
                                                author,
                                                m.getMessageBody() != null
                                                        ? m.getMessageBody()
                                                        : "",
                                                time);
                                    })
                            .toList();

            threads.add(
                    new ChatThreadResponse(
                            "CHT-" + chat.getId(),
                            customerName,
                            chat.getSubject() != null ? chat.getSubject() : "Consulta",
                            lastMessageText,
                            relativeTime,
                            unread > 0 ? (int) unread : null,
                            avatar,
                            msgResponses));
        }

        threads.sort((a, b) -> b.time().compareTo(a.time()));
        return new ConversacionesResponse(threads);
    }

    @GetMapping("/conversaciones/{chatId}/mensajes")
    public List<ChatMessageResponse> mensajes(@PathVariable String chatId) {
        UUID userId = resolveAuthenticatedUserId();
        TenantJpaEntity tenant = requireTenant(userId);
        UUID ticketId = parsePrefixedUuid(chatId, "CHT-");

        ClientChatJpaEntity chat =
                chatRepository
                        .findById(ticketId)
                        .filter(c -> tenant.getId().equals(c.getTenantId()))
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return messageRepository.findAllByTicketIdOrderByCreatedAtAsc(chat.getId()).stream()
                .map(
                        m -> {
                            String author =
                                    chat.getCustomerUserId() != null
                                                    && chat.getCustomerUserId()
                                                            .equals(m.getAuthorUserId())
                                            ? "cliente"
                                            : "empresa";
                            String time =
                                    m.getCreatedAt() != null
                                            ? m.getCreatedAt()
                                                    .format(DateTimeFormatter.ofPattern("HH:mm"))
                                            : "";
                            return new ChatMessageResponse(
                                    "msg-" + m.getId(),
                                    author,
                                    m.getMessageBody() != null ? m.getMessageBody() : "",
                                    time);
                        })
                .toList();
    }

    @PostMapping("/conversaciones/{chatId}/mensajes")
    @ResponseStatus(HttpStatus.CREATED)
    public ChatMessageResponse sendMessage(
            @PathVariable String chatId, @RequestBody SendMessageRequest request) {
        UUID userId = resolveAuthenticatedUserId();
        TenantJpaEntity tenant = requireTenant(userId);
        UUID ticketId = parsePrefixedUuid(chatId, "CHT-");

        ClientChatJpaEntity chat =
                chatRepository
                        .findById(ticketId)
                        .filter(c -> tenant.getId().equals(c.getTenantId()))
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        ClientChatMessageJpaEntity msg = new ClientChatMessageJpaEntity();
        msg.setId(UUID.randomUUID());
        msg.setTicketId(chat.getId());
        msg.setAuthorUserId(userId);
        msg.setMessageBody(request.text() != null ? request.text() : "");
        msg.setMessageType("TEXT");
        msg.setVisibleToCustomer(true);
        msg.setCreatedAt(OffsetDateTime.now());
        messageRepository.save(msg);

        String time = msg.getCreatedAt().format(DateTimeFormatter.ofPattern("HH:mm"));
        return new ChatMessageResponse("msg-" + msg.getId(), "empresa", msg.getMessageBody(), time);
    }

    @PatchMapping("/conversaciones/{chatId}/leido")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void marcarLeido(@PathVariable String chatId) {
        resolveAuthenticatedUserId();
    }

    private TenantJpaEntity requireTenant(UUID userId) {
        return tenantRepository
                .findFirstByMemberUserId(userId)
                .orElseThrow(
                        () ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "No se encontro empresa para este usuario"));
    }

    private String resolveUserName(UUID userId) {
        if (userId == null) return "Cliente";
        return userRepository
                .findById(userId)
                .map(
                        u -> {
                            String fn = u.getFirstName() != null ? u.getFirstName() : "";
                            String ln =
                                    u.getLastName() != null && !u.getLastName().isEmpty()
                                            ? u.getLastName()
                                                            .substring(0, 1)
                                                            .toUpperCase(Locale.ROOT)
                                                    + "."
                                            : "";
                            String full = (fn + (ln.isEmpty() ? "" : " " + ln)).trim();
                            return full.isEmpty() ? "Cliente" : full;
                        })
                .orElse("Cliente");
    }

    private String initials(String name) {
        if (name == null || name.isBlank()) return "CL";
        String[] parts = name.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Math.min(2, parts.length); i++) {
            if (!parts[i].isEmpty()) sb.append(parts[i].charAt(0));
        }
        String result = sb.toString().toUpperCase(Locale.ROOT);
        return result.isEmpty() ? "CL" : result;
    }

    private String formatRelativeTime(OffsetDateTime time) {
        if (time == null) return "";
        long minutes = ChronoUnit.MINUTES.between(time, OffsetDateTime.now());
        if (minutes < 1) return "Ahora";
        if (minutes < 60) return "Hace " + minutes + " min";
        long hours = minutes / 60;
        if (hours < 24) return "Hace " + hours + " h";
        long days = hours / 24;
        if (days == 1) return "Ayer";
        return "Hace " + days + " dias";
    }

    private UUID resolveAuthenticatedUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        Object principal = auth.getPrincipal();
        if (!(principal instanceof String principalStr)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        try {
            return UUID.fromString(principalStr);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
    }

    private UUID parsePrefixedUuid(String value, String prefix) {
        try {
            String raw = value.startsWith(prefix) ? value.substring(prefix.length()) : value;
            return UUID.fromString(raw);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID invalido: " + value);
        }
    }

    public record ConversacionesResponse(List<ChatThreadResponse> conversations) {}

    public record ChatThreadResponse(
            String id,
            String name,
            String product,
            String lastMessage,
            String time,
            Integer unread,
            String avatar,
            List<ChatMessageResponse> messages) {}

    public record ChatMessageResponse(String id, String author, String text, String time) {}

    public record SendMessageRequest(String text, String conversationId, String author) {}
}
