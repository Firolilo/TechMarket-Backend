package com.techmarket.techmarket.empresa.api.admin;

import com.techmarket.techmarket.listings.infrastructure.persistence.jpa.entity.ListingJpaEntity;
import com.techmarket.techmarket.listings.infrastructure.persistence.jpa.repository.ListingSpringDataRepository;
import com.techmarket.techmarket.tenants.infrastructure.persistence.jpa.entity.TenantJpaEntity;
import com.techmarket.techmarket.tenants.infrastructure.persistence.jpa.repository.TenantSpringDataRepository;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.entity.ClientChatJpaEntity;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.entity.ClientReviewJpaEntity;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.repository.ClientChatSpringDataRepository;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.repository.ClientReviewSpringDataRepository;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/empresa")
public class EmpresaDashboardController {

    private final TenantSpringDataRepository tenantRepository;
    private final ListingSpringDataRepository listingRepository;
    private final ClientReviewSpringDataRepository reviewRepository;
    private final ClientChatSpringDataRepository chatRepository;

    public EmpresaDashboardController(
            TenantSpringDataRepository tenantRepository,
            ListingSpringDataRepository listingRepository,
            ClientReviewSpringDataRepository reviewRepository,
            ClientChatSpringDataRepository chatRepository) {
        this.tenantRepository = tenantRepository;
        this.listingRepository = listingRepository;
        this.reviewRepository = reviewRepository;
        this.chatRepository = chatRepository;
    }

    @GetMapping("/resumen")
    public EmpresaResumenResponse resumen() {
        UUID userId = resolveAuthenticatedUserId();

        TenantJpaEntity tenant = requireTenant(userId);

        UUID tenantId = tenant.getId();

        List<ListingJpaEntity> listings = listingRepository.findAllByTenantId(tenantId);
        List<ClientReviewJpaEntity> reviews = reviewRepository.findAllByTenantId(tenantId);
        List<ClientChatJpaEntity> chats = chatRepository.findAllByTenantId(tenantId);

        long activeListings =
                listings.stream().filter(l -> "ACTIVE".equalsIgnoreCase(l.getStatus())).count();
        long openChats =
                chats.stream()
                        .filter(
                                c ->
                                        "OPEN".equalsIgnoreCase(c.getStatus())
                                                || "PENDING".equalsIgnoreCase(c.getStatus()))
                        .count();
        double avgRating =
                reviews.stream()
                        .filter(r -> r.getRating() != null)
                        .mapToDouble(r -> r.getRating().doubleValue())
                        .average()
                        .orElse(0.0);
        long reviewCount = reviews.size();

        return new EmpresaResumenResponse(
                "EMP-" + tenantId,
                buildMetrics(activeListings, openChats, avgRating, reviewCount),
                buildAlerts(openChats, reviewCount, activeListings),
                buildRecentActivity(reviews),
                buildRadar(avgRating, activeListings, openChats, reviewCount),
                buildRecommendedActions(activeListings, openChats, avgRating),
                new AiRecommendations(
                        List.of(
                                "Que accion me conviene priorizar hoy para vender mas?",
                                "Cuales son mis productos con mas interes de los clientes?",
                                "Como puedo mejorar mi calificacion general?",
                                "Que categorias tienen mas demanda en el mercado?")));
    }

    @GetMapping("/perfil")
    public EmpresaPerfilResponse perfil() {
        UUID userId = resolveAuthenticatedUserId();
        TenantJpaEntity tenant = requireTenant(userId);
        UUID tenantId = tenant.getId();

        List<ClientReviewJpaEntity> reviews = reviewRepository.findAllByTenantId(tenantId);
        double avgRating =
                reviews.stream()
                        .filter(r -> r.getRating() != null)
                        .mapToDouble(r -> r.getRating().doubleValue())
                        .average()
                        .orElse(0.0);

        return new EmpresaPerfilResponse(
                tenant.getBusinessName(),
                logoText(tenant.getBusinessName()),
                tenant.getDescription() != null ? tenant.getDescription() : "",
                tenant.getBusinessType() != null ? tenant.getBusinessType() : "",
                avgRating,
                (int) reviews.stream().filter(r -> r.getRating() != null).count(),
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                new LocationOverview("", "", "", "", "", List.of()),
                List.of());
    }

    @PutMapping("/perfil")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updatePerfil(@RequestBody UpdatePerfilRequest request) {
        UUID userId = resolveAuthenticatedUserId();
        TenantJpaEntity tenant = requireTenant(userId);
        if (request.name() != null && !request.name().isBlank()) {
            tenant.setBusinessName(request.name().trim());
        }
        if (request.description() != null) {
            tenant.setDescription(request.description().trim());
        }
        if (request.businessType() != null && !request.businessType().isBlank()) {
            tenant.setBusinessType(request.businessType().trim());
        }
        tenantRepository.save(tenant);
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

    private String logoText(String businessName) {
        if (businessName == null || businessName.isBlank()) return "TM";
        String[] parts = businessName.trim().split("\\s+");
        StringBuilder initials = new StringBuilder();
        for (int i = 0; i < Math.min(2, parts.length); i++) {
            if (!parts[i].isEmpty()) {
                initials.append(parts[i].charAt(0));
            }
        }
        String result = initials.toString().toUpperCase(Locale.ROOT);
        return result.isEmpty() ? "TM" : result;
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
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "No se pudo identificar el usuario");
        }
    }

    private List<MetricItem> buildMetrics(
            long activeListings, long openChats, double avgRating, long reviewCount) {
        return List.of(
                new MetricItem(
                        "m-listings",
                        "Publicaciones activas",
                        String.valueOf(activeListings),
                        activeListings > 0
                                ? "Productos y servicios visibles"
                                : "Sin publicaciones activas",
                        activeListings > 0 ? "positive" : "neutral",
                        "/empresa/publicaciones"),
                new MetricItem(
                        "m-reviews",
                        "Resenas recibidas",
                        String.valueOf(reviewCount),
                        reviewCount > 0
                                ? String.format("Promedio %.1f / 5.0", avgRating)
                                : "Sin resenas aun",
                        reviewCount > 0 ? "positive" : "neutral",
                        "/empresa/resenas"),
                new MetricItem(
                        "m-chats",
                        "Chats activos",
                        String.valueOf(openChats),
                        openChats > 0 ? "Consultas pendientes de atencion" : "Sin chats pendientes",
                        openChats == 0 ? "positive" : "neutral",
                        "/empresa/chat"),
                new MetricItem(
                        "m-rating",
                        "Calificacion",
                        avgRating > 0 ? String.format("%.1f", avgRating) : "N/A",
                        avgRating >= 4.0
                                ? "Por encima del promedio"
                                : avgRating > 0
                                        ? "Puede mejorar con mas resenas"
                                        : "Sin calificacion aun",
                        avgRating >= 4.0 ? "positive" : "neutral",
                        "/empresa/resenas"));
    }

    private List<AlertItem> buildAlerts(long openChats, long reviewCount, long activeListings) {
        List<AlertItem> alerts = new ArrayList<>();
        if (openChats > 0) {
            alerts.add(
                    new AlertItem(
                            "a-chats",
                            "Chats sin atender",
                            openChats + " consulta(s) de clientes esperan respuesta.",
                            "alta",
                            "/empresa/chat"));
        }
        if (activeListings == 0) {
            alerts.add(
                    new AlertItem(
                            "a-listings",
                            "Sin publicaciones activas",
                            "No tienes productos o servicios visibles en el marketplace.",
                            "alta",
                            "/empresa/publicaciones"));
        }
        if (reviewCount == 0) {
            alerts.add(
                    new AlertItem(
                            "a-reviews",
                            "Sin resenas recibidas",
                            "Solicita resenas a tus clientes para mejorar tu credibilidad.",
                            "media",
                            "/empresa/resenas"));
        }
        return alerts;
    }

    private List<ActivityItem> buildRecentActivity(List<ClientReviewJpaEntity> reviews) {
        return reviews.stream()
                .filter(r -> r.getCreatedAt() != null)
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .limit(5)
                .map(
                        r -> {
                            String comment =
                                    (r.getComment() != null && !r.getComment().isBlank())
                                            ? r.getComment()
                                            : "Sin comentario";
                            String shortComment =
                                    comment.length() > 80
                                            ? comment.substring(0, 77) + "..."
                                            : comment;
                            String ratingStr =
                                    r.getRating() != null
                                            ? String.format(
                                                    "%.1f estrellas", r.getRating().doubleValue())
                                            : "";
                            String detail =
                                    ratingStr.isBlank()
                                            ? shortComment
                                            : ratingStr + " — " + shortComment;
                            return new ActivityItem(
                                    "ra-" + r.getId(),
                                    "Nueva resena recibida",
                                    detail,
                                    formatRelativeTime(r.getCreatedAt()));
                        })
                .toList();
    }

    private List<RadarItem> buildRadar(
            double avgRating, long activeListings, long openChats, long reviewCount) {
        int ratingScore = avgRating > 0 ? (int) Math.round(avgRating / 5.0 * 100) : 0;
        int listingsScore = (int) Math.min(activeListings * 10, 100);
        int reviewScore = (int) Math.min(reviewCount * 8, 100);
        int chatScore = openChats == 0 ? 85 : (int) Math.max(0, 85 - openChats * 10);
        return List.of(
                new RadarItem("Calificacion", ratingScore),
                new RadarItem("Catalogo", listingsScore),
                new RadarItem("Resenas", reviewScore),
                new RadarItem("Respuesta", chatScore));
    }

    private List<ActionItem> buildRecommendedActions(
            long activeListings, long openChats, double avgRating) {
        List<ActionItem> actions = new ArrayList<>();
        if (activeListings < 5) {
            actions.add(
                    new ActionItem(
                            "act-catalog",
                            "Amplia tu catalogo",
                            "Publicar mas productos o servicios aumenta tu visibilidad en el marketplace.",
                            "Alta: mas publicaciones generan mas clientes potenciales",
                            "/empresa/publicaciones",
                            "Ir al catalogo"));
        }
        if (openChats > 0) {
            actions.add(
                    new ActionItem(
                            "act-chats",
                            "Responde los chats pendientes",
                            "Los clientes que esperan respuesta pueden perder el interes rapidamente.",
                            "Alta: la velocidad de respuesta impacta directamente en la conversion",
                            "/empresa/chat",
                            "Ir a chats"));
        }
        if (avgRating < 4.0) {
            actions.add(
                    new ActionItem(
                            "act-reviews",
                            "Mejora tu reputacion",
                            "Solicita resenas a clientes satisfechos para elevar tu calificacion promedio.",
                            "Media: las resenas positivas aumentan la confianza de nuevos clientes",
                            "/empresa/resenas",
                            "Ver resenas"));
        }
        if (actions.isEmpty()) {
            actions.add(
                    new ActionItem(
                            "act-profile",
                            "Optimiza tu perfil de empresa",
                            "Un perfil completo con descripcion, especialidades y contactos genera mas confianza.",
                            "Media: los perfiles completos tienen mayor tasa de contacto",
                            "/empresa/perfil",
                            "Editar perfil"));
        }
        return actions;
    }

    private String formatRelativeTime(OffsetDateTime time) {
        if (time == null) return "Fecha desconocida";
        long days = ChronoUnit.DAYS.between(time, OffsetDateTime.now());
        if (days == 0) return "hoy";
        if (days == 1) return "ayer";
        if (days < 7) return "hace " + days + " dias";
        if (days < 30) return "hace " + (days / 7) + " semana(s)";
        return "hace " + (days / 30) + " mes(es)";
    }

    public record EmpresaResumenResponse(
            String id,
            List<MetricItem> metrics,
            List<AlertItem> alerts,
            List<ActivityItem> recentActivity,
            List<RadarItem> radar,
            List<ActionItem> recommendedActions,
            AiRecommendations ai) {}

    public record MetricItem(
            String id, String label, String value, String trend, String tone, String href) {}

    public record AlertItem(String id, String title, String detail, String priority, String href) {}

    public record ActivityItem(String id, String title, String detail, String time) {}

    public record RadarItem(String label, int value) {}

    public record ActionItem(
            String id, String title, String description, String impact, String href, String cta) {}

    public record AiRecommendations(List<String> recommendedQuestions) {}

    public record EmpresaPerfilResponse(
            String name,
            String logoText,
            String description,
            String businessType,
            double rating,
            int reviewCount,
            List<String> about,
            List<String> specialties,
            List<String> coverageAreas,
            List<Object> contacts,
            List<Object> socialLinks,
            List<Object> schedules,
            List<Object> branches,
            LocationOverview locationOverview,
            List<Object> addresses) {}

    public record LocationOverview(
            String mainAddressShort,
            String mainAddressLong,
            String city,
            String zone,
            String reference,
            List<String> mapAreas) {}

    public record UpdatePerfilRequest(String name, String description, String businessType) {}
}
