package com.techmarket.techmarket.empresa.api.admin;

import com.techmarket.techmarket.listings.infrastructure.persistence.jpa.entity.ListingJpaEntity;
import com.techmarket.techmarket.listings.infrastructure.persistence.jpa.repository.ListingSpringDataRepository;
import com.techmarket.techmarket.tenants.infrastructure.persistence.jpa.entity.TenantJpaEntity;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.entity.ClientChatJpaEntity;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.entity.ClientReviewJpaEntity;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.repository.ClientChatSpringDataRepository;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.repository.ClientReviewSpringDataRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * Real-data context that grounds the company AI assistant. TechMarket-AI is a stateless service
 * with no database, so the LLM can only reason about what it is given: this endpoint exposes the
 * company's actual reputation, catalog and activity (from TechMarket-IA) so the Next.js proxy can
 * forward it as the {@code context} of the AI consultation. See the AI service's EmpresaAiService.
 */
@RestController
@RequestMapping("/api/empresa/ia-contexto")
public class EmpresaIaContextController {

    private static final int MAX_ITEMS = 5;
    private static final int MAX_REVIEWS = 3;

    private final ListingSpringDataRepository listingRepository;
    private final ClientReviewSpringDataRepository reviewRepository;
    private final ClientChatSpringDataRepository chatRepository;
    private final EmpresaTenantProvisioner tenantProvisioner;

    public EmpresaIaContextController(
            ListingSpringDataRepository listingRepository,
            ClientReviewSpringDataRepository reviewRepository,
            ClientChatSpringDataRepository chatRepository,
            EmpresaTenantProvisioner tenantProvisioner) {
        this.listingRepository = listingRepository;
        this.reviewRepository = reviewRepository;
        this.chatRepository = chatRepository;
        this.tenantProvisioner = tenantProvisioner;
    }

    @GetMapping
    public EmpresaIaContexto contexto() {
        TenantJpaEntity tenant = tenantProvisioner.resolveOrCreate(resolveAuthenticatedUserId());
        UUID tenantId = tenant.getId();

        List<ListingJpaEntity> listings = listingRepository.findAllByTenantId(tenantId);
        List<ClientReviewJpaEntity> reviews = reviewRepository.findAllByTenantId(tenantId);
        List<ClientChatJpaEntity> chats = chatRepository.findAllByTenantId(tenantId);

        List<ClientReviewJpaEntity> rated =
                reviews.stream().filter(r -> r.getRating() != null).toList();
        double avgRating =
                rated.stream().mapToDouble(r -> r.getRating().doubleValue()).average().orElse(0.0);

        List<CatalogoItem> items =
                listings.stream()
                        .filter(l -> "ACTIVE".equalsIgnoreCase(l.getStatus()))
                        .limit(MAX_ITEMS)
                        .map(
                                l ->
                                        new CatalogoItem(
                                                l.getTitle(), l.getListingType(), l.getBasePrice()))
                        .toList();
        long activeListings =
                listings.stream().filter(l -> "ACTIVE".equalsIgnoreCase(l.getStatus())).count();

        long openChats =
                chats.stream()
                        .filter(
                                c ->
                                        "OPEN".equalsIgnoreCase(c.getStatus())
                                                || "PENDING".equalsIgnoreCase(c.getStatus()))
                        .count();

        List<ResenaReciente> recientes =
                rated.stream()
                        .filter(r -> r.getComment() != null && !r.getComment().isBlank())
                        .sorted(
                                Comparator.comparing(
                                        ClientReviewJpaEntity::getCreatedAt,
                                        Comparator.nullsLast(Comparator.reverseOrder())))
                        .limit(MAX_REVIEWS)
                        .map(r -> new ResenaReciente(r.getRating().doubleValue(), r.getComment()))
                        .toList();

        return new EmpresaIaContexto(
                new Negocio(
                        tenant.getBusinessName(),
                        tenant.getBusinessType(),
                        tenant.getDescription()),
                new Reputacion(round1(avgRating), (long) rated.size()),
                new Catalogo(activeListings, items),
                new Actividad(openChats),
                recientes);
    }

    private double round1(double value) {
        return BigDecimal.valueOf(value).setScale(1, RoundingMode.HALF_UP).doubleValue();
    }

    private UUID resolveAuthenticatedUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof String s)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        try {
            return UUID.fromString(s);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "No se pudo identificar el usuario");
        }
    }

    public record EmpresaIaContexto(
            Negocio negocio,
            Reputacion reputacion,
            Catalogo catalogo,
            Actividad actividad,
            List<ResenaReciente> resenasRecientes) {}

    public record Negocio(String nombre, String tipo, String descripcion) {}

    public record Reputacion(double calificacionPromedio, long totalResenas) {}

    public record Catalogo(long publicacionesActivas, List<CatalogoItem> items) {}

    public record CatalogoItem(String titulo, String tipo, BigDecimal precio) {}

    public record Actividad(long chatsAbiertos) {}

    public record ResenaReciente(double calificacion, String comentario) {}
}
