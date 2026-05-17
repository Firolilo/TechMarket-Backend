package com.techmarket.techmarket.empresa.api.admin;

import com.techmarket.techmarket.listings.infrastructure.persistence.jpa.repository.ListingSpringDataRepository;
import com.techmarket.techmarket.tenants.infrastructure.persistence.jpa.entity.TenantJpaEntity;
import com.techmarket.techmarket.tenants.infrastructure.persistence.jpa.repository.TenantSpringDataRepository;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.entity.ClientReviewJpaEntity;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.repository.ClientReviewSpringDataRepository;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.repository.UserSpringDataRepository;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/empresa")
public class EmpresaResenasController {

    private final TenantSpringDataRepository tenantRepository;
    private final ClientReviewSpringDataRepository reviewRepository;
    private final UserSpringDataRepository userRepository;
    private final ListingSpringDataRepository listingRepository;

    public EmpresaResenasController(
            TenantSpringDataRepository tenantRepository,
            ClientReviewSpringDataRepository reviewRepository,
            UserSpringDataRepository userRepository,
            ListingSpringDataRepository listingRepository) {
        this.tenantRepository = tenantRepository;
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.listingRepository = listingRepository;
    }

    @GetMapping("/resenas")
    public Map<String, Object> resenas() {
        UUID userId = resolveAuthenticatedUserId();
        TenantJpaEntity tenant = requireTenant(userId);
        UUID tenantId = tenant.getId();

        List<ClientReviewJpaEntity> reviews = reviewRepository.findAllByTenantId(tenantId);
        reviews.sort(
                (a, b) -> {
                    if (a.getCreatedAt() == null) return 1;
                    if (b.getCreatedAt() == null) return -1;
                    return b.getCreatedAt().compareTo(a.getCreatedAt());
                });

        List<Map<String, Object>> result = new ArrayList<>();
        for (ClientReviewJpaEntity review : reviews) {
            String customerName = resolveUserName(review.getUserId());
            String productName = resolveListingTitle(review.getListingId());
            String dateStr =
                    review.getCreatedAt() != null
                            ? review.getCreatedAt()
                                    .format(DateTimeFormatter.ofPattern("d MMM yyyy"))
                            : "";
            int stars = review.getRating() != null ? review.getRating().intValue() : 0;
            boolean needsFollowUp = stars < 4;
            boolean wasResponded = "RESPONDED".equalsIgnoreCase(review.getModerationStatus());

            Map<String, Object> item = new HashMap<>();
            item.put("id", "REV-" + review.getId());
            item.put("customer", customerName);
            item.put("initials", initials(customerName));
            item.put("productOrService", productName);
            item.put("chatDate", dateStr);
            item.put("reviewDate", dateStr);
            item.put("stars", stars);
            item.put("message", review.getComment() != null ? review.getComment() : "");
            item.put("tags", List.of());
            item.put("needsFollowUp", needsFollowUp);
            item.put("wasResponded", wasResponded);
            result.add(item);
        }

        return Map.of("reviews", result);
    }

    @PostMapping("/resenas/{reviewId}/respuesta")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void responderResena(
            @PathVariable String reviewId, @RequestBody Map<String, Object> payload) {
        resolveAuthenticatedUserId();
        UUID id = parsePrefixedUuid(reviewId, "REV-");
        reviewRepository
                .findById(id)
                .ifPresent(
                        review -> {
                            review.setModerationStatus("RESPONDED");
                            reviewRepository.save(review);
                        });
    }

    @GetMapping("/analiticas")
    public Map<String, Object> analiticas() {
        UUID userId = resolveAuthenticatedUserId();
        TenantJpaEntity tenant = requireTenant(userId);
        UUID tenantId = tenant.getId();

        List<ClientReviewJpaEntity> reviews = reviewRepository.findAllByTenantId(tenantId);
        var listings = listingRepository.findAllByTenantId(tenantId);

        double avgRating =
                reviews.stream()
                        .filter(r -> r.getRating() != null)
                        .mapToDouble(r -> r.getRating().doubleValue())
                        .average()
                        .orElse(0.0);

        Map<Integer, Long> starCounts = new HashMap<>();
        for (int i = 1; i <= 5; i++) starCounts.put(i, 0L);
        for (ClientReviewJpaEntity r : reviews) {
            if (r.getRating() != null) {
                int stars = Math.min(5, Math.max(1, r.getRating().intValue()));
                starCounts.merge(stars, 1L, Long::sum);
            }
        }
        long totalReviews = reviews.size();
        List<Map<String, Object>> ratingLevels = new ArrayList<>();
        for (int stars = 5; stars >= 1; stars--) {
            long count = starCounts.getOrDefault(stars, 0L);
            int percent = totalReviews > 0 ? (int) Math.round(count * 100.0 / totalReviews) : 0;
            Map<String, Object> level = new HashMap<>();
            level.put("stars", stars);
            level.put("percent", percent);
            level.put("users", (int) count);
            ratingLevels.add(level);
        }

        List<Map<String, Object>> userReviews =
                reviews.stream()
                        .filter(r -> r.getCreatedAt() != null)
                        .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                        .limit(3)
                        .map(
                                r -> {
                                    String user = resolveUserName(r.getUserId());
                                    String date =
                                            r.getCreatedAt()
                                                    .format(
                                                            DateTimeFormatter.ofPattern(
                                                                    "d MMM yyyy"));
                                    Map<String, Object> item = new HashMap<>();
                                    item.put("id", "REV-" + r.getId());
                                    item.put("user", user);
                                    item.put(
                                            "stars",
                                            r.getRating() != null ? r.getRating().intValue() : 0);
                                    item.put("text", r.getComment() != null ? r.getComment() : "");
                                    item.put("date", date);
                                    return item;
                                })
                        .toList();

        List<Map<String, Object>> publicationMetrics =
                listings.stream()
                        .limit(5)
                        .map(
                                l -> {
                                    Map<String, Object> item = new HashMap<>();
                                    item.put("id", "PROD-" + l.getId());
                                    item.put("title", l.getTitle() != null ? l.getTitle() : "");
                                    item.put("visits", 0);
                                    item.put("conversion", 0);
                                    return item;
                                })
                        .toList();

        List<Map<String, Object>> growthSeries =
                List.of(
                        Map.of("month", "Ene", "visits", 0),
                        Map.of("month", "Feb", "visits", 0),
                        Map.of("month", "Mar", "visits", 0),
                        Map.of("month", "Abr", "visits", 0),
                        Map.of("month", "May", "visits", 0));

        return Map.of(
                "publicationMetrics", publicationMetrics,
                "ratingLevels", ratingLevels,
                "userReviews", userReviews,
                "userComments", List.of(),
                "growthSeries", growthSeries,
                "ratingAverage", avgRating,
                "growthIndex", 0);
    }

    @PostMapping("/ia/consulta")
    public Map<String, Object> iaConsulta(@RequestBody Map<String, Object> payload) {
        resolveAuthenticatedUserId();
        String question = payload.get("question") instanceof String s ? s.toLowerCase() : "";

        String summary;
        List<String> dataPoints;
        String advice;
        String nextStep;

        if (question.contains("vender")
                || question.contains("conversion")
                || question.contains("priorizar")) {
            summary =
                    "Para vender mas hoy, responde rapidamente los chats con alta intencion de compra.";
            dataPoints =
                    List.of(
                            "Los clientes que esperan respuesta mas de 15 minutos pierden interes.",
                            "Responder rapido aumenta la probabilidad de conversion.",
                            "Mantener publicaciones activas aumenta la visibilidad.");
            advice = "Prioriza los chats de clientes que preguntaron por precio o disponibilidad.";
            nextStep = "Abre el modulo de chats y atiende las conversaciones pendientes.";
        } else if (question.contains("producto")
                || question.contains("interes")
                || question.contains("demanda")) {
            summary =
                    "Los productos con mejor desempeno son los que tienen descripcion clara y precio visible.";
            dataPoints =
                    List.of(
                            "Publicaciones con imagen tienen mas visitas.",
                            "Incluir precio atrae mas consultas directas.",
                            "Titulos descriptivos mejoran la busqueda.");
            advice = "Revisa tus publicaciones y actualiza las que no tienen imagen o precio.";
            nextStep = "Ve al modulo de publicaciones y edita las fichas incompletas.";
        } else if (question.contains("calificacion")
                || question.contains("reputacion")
                || question.contains("resena")) {
            summary = "Para mejorar tu calificacion, solicita resenas a clientes satisfechos.";
            dataPoints =
                    List.of(
                            "Las resenas positivas aumentan la confianza de nuevos clientes.",
                            "Responder resenas muestra compromiso con el servicio.",
                            "Una calificacion mayor a 4.5 aumenta el contacto organico.");
            advice =
                    "Envia un mensaje de seguimiento a clientes recientes y pideles que dejen su opinion.";
            nextStep = "Revisa el modulo de resenas y responde las que aun no tienen respuesta.";
        } else if (question.contains("categoria") || question.contains("mercado")) {
            summary =
                    "El mercado tecnologico local muestra mayor demanda en soporte, reparacion y equipos de oficina.";
            dataPoints =
                    List.of(
                            "El soporte tecnico y mantenimiento tienen alta demanda constante.",
                            "Los equipos de oficina y laptops tienen consultas frecuentes.",
                            "Los servicios de redes crecen con las empresas en expansion.");
            advice = "Considera destacar tus servicios de mayor demanda en el marketplace.";
            nextStep = "Crea una publicacion destacada para tu servicio mas popular.";
        } else {
            summary =
                    "Mantener tu perfil activo y responder rapido son las claves del exito en el marketplace.";
            dataPoints =
                    List.of(
                            "Perfil completo con descripcion atrae mas contactos.",
                            "Responder en menos de 1 hora mejora conversion.",
                            "Publicaciones regulares mantienen visibilidad.");
            advice =
                    "Completa tu perfil, manten publicaciones activas y atiende los chats oportunamente.";
            nextStep = "Revisa el resumen de tu empresa para identificar areas de mejora.";
        }

        return Map.of(
                "summary", summary,
                "dataPoints", dataPoints,
                "advice", advice,
                "nextStep", nextStep);
    }

    // ---- helpers ----

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

    private String resolveListingTitle(UUID listingId) {
        if (listingId == null) return "Consulta general";
        return listingRepository
                .findById(listingId)
                .map(l -> l.getTitle() != null ? l.getTitle() : "Producto/Servicio")
                .orElse("Consulta general");
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

    private TenantJpaEntity requireTenant(UUID userId) {
        return tenantRepository
                .findFirstByMemberUserId(userId)
                .orElseThrow(
                        () ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "No se encontro empresa para este usuario"));
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
}
