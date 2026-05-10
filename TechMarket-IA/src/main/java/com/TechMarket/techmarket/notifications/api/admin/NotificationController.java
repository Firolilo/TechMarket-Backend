package com.techmarket.techmarket.notifications.api.admin;

import com.techmarket.techmarket.notifications.infrastructure.persistence.jpa.entity.UserNotificationPreferenceJpaEntity;
import com.techmarket.techmarket.notifications.infrastructure.persistence.jpa.repository.UserNotificationPreferenceSpringDataRepository;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.entity.ClientNotificationJpaEntity;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.repository.ClientNotificationSpringDataRepository;
import jakarta.validation.Valid;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final ClientNotificationSpringDataRepository notificationRepository;
    private final UserNotificationPreferenceSpringDataRepository preferenceRepository;

    public NotificationController(
            ClientNotificationSpringDataRepository notificationRepository,
            UserNotificationPreferenceSpringDataRepository preferenceRepository) {
        this.notificationRepository = notificationRepository;
        this.preferenceRepository = preferenceRepository;
    }

    @GetMapping
    public List<NotificationResponse> notifications(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        UUID currentUserId = parseUserId(userId);
        return notificationRepository.findAllByUserIdOrderBySentAtDesc(currentUserId).stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/unread-count")
    public UnreadCountResponse unreadCount(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        return new UnreadCountResponse(
                notificationRepository.countByUserIdAndReadFalse(parseUserId(userId)));
    }

    @PutMapping("/{notificationId}/read")
    public ReadNotificationResponse read(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @PathVariable String notificationId) {
        ClientNotificationJpaEntity notification =
                findNotification(notificationId, parseUserId(userId));
        notification.setRead(true);
        ClientNotificationJpaEntity saved = notificationRepository.save(notification);
        return new ReadNotificationResponse(formatNotificationId(saved.getId()), true);
    }

    @PutMapping("/read-all")
    @Transactional
    public MessageResponse readAll(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        UUID currentUserId = parseUserId(userId);
        notificationRepository.findAllByUserIdAndReadFalse(currentUserId).forEach(
                notification -> {
                    notification.setRead(true);
                    notificationRepository.save(notification);
                });
        return new MessageResponse("Todas las notificaciones marcadas como leidas");
    }

    @DeleteMapping("/{notificationId}")
    public MessageResponse delete(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @PathVariable String notificationId) {
        notificationRepository.delete(findNotification(notificationId, parseUserId(userId)));
        return new MessageResponse("Notificacion eliminada");
    }

    @GetMapping("/preferences")
    public NotificationPreferenceResponse preferences(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        return toPreferenceResponse(resolvePreferences(parseUserId(userId)));
    }

    @PutMapping("/preferences")
    public MessageResponse updatePreferences(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @Valid @RequestBody UpdateNotificationPreferenceRequest request) {
        UserNotificationPreferenceJpaEntity preferences = resolvePreferences(parseUserId(userId));
        preferences.setEmailEnabled(request.email());
        preferences.setPushEnabled(request.push());
        preferences.setInAppEnabled(request.inApp());
        preferences.setUpdatedAt(OffsetDateTime.now());
        preferenceRepository.save(preferences);
        return new MessageResponse("Preferencias actualizadas");
    }

    private UserNotificationPreferenceJpaEntity resolvePreferences(UUID userId) {
        return preferenceRepository
                .findByUserId(userId)
                .orElseGet(
                        () -> {
                            OffsetDateTime now = OffsetDateTime.now();
                            UserNotificationPreferenceJpaEntity created =
                                    new UserNotificationPreferenceJpaEntity();
                            created.setId(UUID.randomUUID());
                            created.setUserId(userId);
                            created.setEmailEnabled(true);
                            created.setPushEnabled(true);
                            created.setInAppEnabled(true);
                            created.setCreatedAt(now);
                            created.setUpdatedAt(now);
                            return preferenceRepository.save(created);
                        });
    }

    private ClientNotificationJpaEntity findNotification(String notificationId, UUID userId) {
        return notificationRepository
                .findByIdAndUserId(parsePrefixedUuid(notificationId, "NOT-"), userId)
                .orElseThrow(
                        () ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND, "Notification not found"));
    }

    private NotificationResponse toResponse(ClientNotificationJpaEntity notification) {
        return new NotificationResponse(
                formatNotificationId(notification.getId()),
                notification.getTitle(),
                notification.getMessage(),
                Boolean.TRUE.equals(notification.getRead()),
                notification.getSentAt());
    }

    private NotificationPreferenceResponse toPreferenceResponse(
            UserNotificationPreferenceJpaEntity preferences) {
        return new NotificationPreferenceResponse(
                preferences.isEmailEnabled(),
                preferences.isPushEnabled(),
                preferences.isInAppEnabled());
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

    public record NotificationResponse(
            String id, String titulo, String mensaje, boolean leida, OffsetDateTime fecha) {}

    public record UnreadCountResponse(long noLeidas) {}

    public record ReadNotificationResponse(String id, boolean leida) {}

    public record NotificationPreferenceResponse(boolean email, boolean push, boolean inApp) {}

    public record UpdateNotificationPreferenceRequest(boolean email, boolean push, boolean inApp) {}

    public record MessageResponse(String mensaje) {}
}
