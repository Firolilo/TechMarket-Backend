package com.techmarket.techmarket.users.api.admin.client;

import com.techmarket.techmarket.users.api.admin.client.response.MessageResponse;
import com.techmarket.techmarket.users.api.admin.client.response.NotificationResponse;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.entity.ClientNotificationJpaEntity;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.repository.ClientNotificationSpringDataRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/clients/notifications")
public class ClientNotificationController {

    private final ClientNotificationSpringDataRepository notificationRepository;

    public ClientNotificationController(
            ClientNotificationSpringDataRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @GetMapping
    public List<NotificationResponse> notifications(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        UUID currentUserId = parseUserId(userId);
        return notificationRepository.findAllByUserIdOrderBySentAtDesc(currentUserId).stream()
                .map(this::toNotificationResponse)
                .toList();
    }

    @PutMapping("/{notificationId}/read")
    public NotificationResponse readNotification(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @PathVariable String notificationId) {
        UUID currentUserId = parseUserId(userId);
        ClientNotificationJpaEntity notification = findNotification(notificationId, currentUserId);
        notification.setRead(true);
        return toNotificationResponse(notificationRepository.save(notification));
    }

    @PutMapping("/read-all")
    @Transactional
    public MessageResponse readAll(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        UUID currentUserId = parseUserId(userId);
        notificationRepository
                .findAllByUserIdAndReadFalse(currentUserId)
                .forEach(
                        notification -> {
                            notification.setRead(true);
                            notificationRepository.save(notification);
                        });
        return new MessageResponse("Todas las notificaciones marcadas como leídas");
    }

    @DeleteMapping("/{notificationId}")
    public MessageResponse deleteNotification(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @PathVariable String notificationId) {
        UUID currentUserId = parseUserId(userId);
        notificationRepository.delete(findNotification(notificationId, currentUserId));
        return new MessageResponse("Notificación eliminada");
    }

    private ClientNotificationJpaEntity findNotification(String notificationId, UUID userId) {
        return notificationRepository
                .findByIdAndUserId(parsePrefixedUuid(notificationId, "NOT-"), userId)
                .orElseThrow(
                        () ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND, "Notification not found"));
    }

    private NotificationResponse toNotificationResponse(ClientNotificationJpaEntity notification) {
        return new NotificationResponse(
                formatNotificationId(notification.getId()),
                notification.getTitle(),
                Boolean.TRUE.equals(notification.getRead()),
                notification.getLinkUrl());
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

    private String formatNotificationId(UUID id) {
        return "NOT-" + id;
    }
}
