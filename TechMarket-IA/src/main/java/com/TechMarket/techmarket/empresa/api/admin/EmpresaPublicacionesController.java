package com.techmarket.techmarket.empresa.api.admin;

import com.techmarket.techmarket.listings.infrastructure.persistence.jpa.entity.ListingImageJpaEntity;
import com.techmarket.techmarket.listings.infrastructure.persistence.jpa.entity.ListingJpaEntity;
import com.techmarket.techmarket.listings.infrastructure.persistence.jpa.repository.ListingImageSpringDataRepository;
import com.techmarket.techmarket.listings.infrastructure.persistence.jpa.repository.ListingSpringDataRepository;
import com.techmarket.techmarket.tenants.infrastructure.persistence.jpa.entity.TenantJpaEntity;
import com.techmarket.techmarket.tenants.infrastructure.persistence.jpa.repository.TenantSpringDataRepository;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/empresa")
public class EmpresaPublicacionesController {

    private final TenantSpringDataRepository tenantRepository;
    private final ListingSpringDataRepository listingRepository;
    private final ListingImageSpringDataRepository imageRepository;

    public EmpresaPublicacionesController(
            TenantSpringDataRepository tenantRepository,
            ListingSpringDataRepository listingRepository,
            ListingImageSpringDataRepository imageRepository) {
        this.tenantRepository = tenantRepository;
        this.listingRepository = listingRepository;
        this.imageRepository = imageRepository;
    }

    @GetMapping("/publicaciones")
    public PublicacionesResponse publicaciones() {
        UUID userId = resolveAuthenticatedUserId();
        TenantJpaEntity tenant = requireTenant(userId);
        UUID tenantId = tenant.getId();

        List<ListingJpaEntity> listings = listingRepository.findAllByTenantId(tenantId);

        Map<UUID, String> primaryImages = new HashMap<>();
        for (ListingJpaEntity l : listings) {
            imageRepository
                    .findFirstByListingIdAndIsPrimaryTrue(l.getId())
                    .ifPresent(img -> primaryImages.put(l.getId(), img.getImageUrl()));
        }

        List<Map<String, Object>> products = new ArrayList<>();
        List<Map<String, Object>> services = new ArrayList<>();
        List<Map<String, Object>> offers = new ArrayList<>();
        List<Map<String, Object>> surveys = new ArrayList<>();
        List<Map<String, Object>> posts = new ArrayList<>();
        List<Map<String, Object>> textPosts = new ArrayList<>();

        for (ListingJpaEntity l : listings) {
            String img = primaryImages.getOrDefault(l.getId(), "");
            String type = l.getListingType() != null ? l.getListingType().toUpperCase() : "PRODUCT";
            String price =
                    l.getBasePrice() != null
                            ? (l.getCurrency() != null ? l.getCurrency() + " " : "Bs ")
                                    + l.getBasePrice().toPlainString()
                            : "Consultar";
            String dateStr =
                    l.getCreatedAt() != null
                            ? l.getCreatedAt().format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
                            : "";
            String listingId = "PROD-" + l.getId();
            String status = l.getStatus() != null ? l.getStatus() : "ACTIVE";

            switch (type) {
                case "SERVICE" -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("id", listingId);
                    item.put("name", l.getTitle() != null ? l.getTitle() : "");
                    item.put("description", l.getDescription() != null ? l.getDescription() : "");
                    item.put("price", price);
                    item.put("image", img);
                    services.add(item);
                }
                case "OFFER" -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("id", listingId);
                    item.put("title", l.getTitle() != null ? l.getTitle() : "");
                    item.put("description", l.getDescription() != null ? l.getDescription() : "");
                    item.put("currentPrice", price);
                    item.put("previousPrice", "");
                    item.put("label", "Oferta");
                    item.put("image", img);
                    offers.add(item);
                }
                case "SURVEY" -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("id", listingId);
                    item.put("question", l.getTitle() != null ? l.getTitle() : "");
                    item.put("options", List.of());
                    item.put("votes", 0);
                    surveys.add(item);
                }
                case "POST" -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("id", listingId);
                    item.put("title", l.getTitle() != null ? l.getTitle() : "");
                    item.put("message", l.getDescription() != null ? l.getDescription() : "");
                    item.put("date", dateStr);
                    item.put("image", img);
                    posts.add(item);
                }
                case "TEXT" -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("id", listingId);
                    item.put("title", l.getTitle() != null ? l.getTitle() : "");
                    item.put("message", l.getDescription() != null ? l.getDescription() : "");
                    item.put("date", dateStr);
                    item.put("image", img);
                    textPosts.add(item);
                }
                default -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("id", listingId);
                    item.put("name", l.getTitle() != null ? l.getTitle() : "");
                    item.put("description", l.getDescription() != null ? l.getDescription() : "");
                    item.put("price", price);
                    item.put("status", toStatusLabel(status));
                    item.put("image", img);
                    products.add(item);
                }
            }
        }

        return new PublicacionesResponse(
                products, services, offers, surveys, posts, textPosts, List.of(), null);
    }

    @PostMapping("/publicaciones")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> createPublicacion(@RequestBody Map<String, Object> payload) {
        UUID userId = resolveAuthenticatedUserId();
        TenantJpaEntity tenant = requireTenant(userId);

        ListingJpaEntity listing = new ListingJpaEntity();
        listing.setId(UUID.randomUUID());
        listing.setTenantId(tenant.getId());
        listing.setTitle(asString(payload.get("title"), asString(payload.get("name"), "")));
        listing.setDescription(asString(payload.get("description"), ""));
        listing.setStatus("ACTIVE");
        listing.setCreatedAt(OffsetDateTime.now());
        listing.setUpdatedAt(OffsetDateTime.now());

        String type =
                asString(payload.get("type"), asString(payload.get("targetFilter"), "PRODUCT"));
        listing.setListingType(normalizeType(type));

        String priceStr = asString(payload.get("price"), asString(payload.get("currentPrice"), ""));
        if (!priceStr.isBlank()) {
            try {
                listing.setBasePrice(new BigDecimal(priceStr.replaceAll("[^\\d.]", "")));
                listing.setCurrency("Bs");
            } catch (NumberFormatException ignored) {
            }
        }

        listing = listingRepository.save(listing);

        String imageUrl = asString(payload.get("imageUrl"), asString(payload.get("image"), ""));
        if (!imageUrl.isBlank()) {
            ListingImageJpaEntity img = new ListingImageJpaEntity();
            img.setId(UUID.randomUUID());
            img.setListingId(listing.getId());
            img.setImageUrl(imageUrl);
            img.setIsPrimary(true);
            imageRepository.save(img);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("id", "PROD-" + listing.getId());
        result.put("title", listing.getTitle());
        result.put("status", "Disponible");
        return result;
    }

    @PutMapping("/productos/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateProduct(@PathVariable String id, @RequestBody Map<String, Object> payload) {
        UUID userId = resolveAuthenticatedUserId();
        TenantJpaEntity tenant = requireTenant(userId);
        ListingJpaEntity listing = requireListing(id, tenant.getId());
        if (payload.containsKey("name"))
            listing.setTitle(asString(payload.get("name"), listing.getTitle()));
        if (payload.containsKey("description"))
            listing.setDescription(asString(payload.get("description"), listing.getDescription()));
        if (payload.containsKey("status"))
            listing.setStatus(normalizeStatus(asString(payload.get("status"), "")));
        updatePrice(listing, payload);
        listing.setUpdatedAt(OffsetDateTime.now());
        listingRepository.save(listing);
        updateImage(listing.getId(), payload);
    }

    @PutMapping("/servicios/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateService(@PathVariable String id, @RequestBody Map<String, Object> payload) {
        UUID userId = resolveAuthenticatedUserId();
        TenantJpaEntity tenant = requireTenant(userId);
        ListingJpaEntity listing = requireListing(id, tenant.getId());
        if (payload.containsKey("name"))
            listing.setTitle(asString(payload.get("name"), listing.getTitle()));
        if (payload.containsKey("description"))
            listing.setDescription(asString(payload.get("description"), listing.getDescription()));
        updatePrice(listing, payload);
        listing.setUpdatedAt(OffsetDateTime.now());
        listingRepository.save(listing);
        updateImage(listing.getId(), payload);
    }

    @PutMapping("/ofertas/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateOffer(@PathVariable String id, @RequestBody Map<String, Object> payload) {
        UUID userId = resolveAuthenticatedUserId();
        TenantJpaEntity tenant = requireTenant(userId);
        ListingJpaEntity listing = requireListing(id, tenant.getId());
        if (payload.containsKey("title"))
            listing.setTitle(asString(payload.get("title"), listing.getTitle()));
        if (payload.containsKey("description"))
            listing.setDescription(asString(payload.get("description"), listing.getDescription()));
        updatePrice(listing, payload);
        listing.setUpdatedAt(OffsetDateTime.now());
        listingRepository.save(listing);
        updateImage(listing.getId(), payload);
    }

    @PostMapping("/publicaciones/{id}/likes")
    public Map<String, Object> toggleLike(
            @PathVariable String id, @RequestBody Map<String, Object> payload) {
        resolveAuthenticatedUserId();
        return Map.of("liked", payload.getOrDefault("liked", false), "publicationId", id);
    }

    @PostMapping("/publicaciones/{id}/comentarios")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> addComment(
            @PathVariable String id, @RequestBody Map<String, Object> payload) {
        resolveAuthenticatedUserId();
        return Map.of(
                "id",
                "com-" + UUID.randomUUID(),
                "publicationId",
                id,
                "text",
                payload.getOrDefault("text", ""),
                "author",
                payload.getOrDefault("authorName", "Empresa"),
                "time",
                "Ahora");
    }

    @PostMapping("/encuestas")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> createSurvey(@RequestBody Map<String, Object> payload) {
        UUID userId = resolveAuthenticatedUserId();
        TenantJpaEntity tenant = requireTenant(userId);

        ListingJpaEntity listing = new ListingJpaEntity();
        listing.setId(UUID.randomUUID());
        listing.setTenantId(tenant.getId());
        listing.setTitle(
                asString(payload.get("question"), asString(payload.get("title"), "Encuesta")));
        listing.setDescription(asString(payload.get("description"), ""));
        listing.setListingType("SURVEY");
        listing.setStatus("ACTIVE");
        listing.setCreatedAt(OffsetDateTime.now());
        listing.setUpdatedAt(OffsetDateTime.now());
        listing = listingRepository.save(listing);

        return Map.of(
                "id",
                "PROD-" + listing.getId(),
                "question",
                listing.getTitle(),
                "options",
                payload.getOrDefault("options", List.of()),
                "votes",
                0);
    }

    @PostMapping("/archivos/imagenes")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "folder", required = false, defaultValue = "empresa")
                    String folder) {
        resolveAuthenticatedUserId();
        String uuid = UUID.randomUUID().toString();
        String fileName =
                file.getOriginalFilename() != null ? file.getOriginalFilename() : "imagen.jpg";
        String url = "/uploads/" + folder + "/" + uuid + "/" + fileName;
        return Map.of(
                "id",
                "IMG-" + uuid,
                "fileName",
                fileName,
                "url",
                url,
                "mimeType",
                file.getContentType() != null ? file.getContentType() : "image/jpeg",
                "size",
                file.getSize());
    }

    // ---- helpers ----

    private ListingJpaEntity requireListing(String idParam, UUID tenantId) {
        UUID listingId = parsePrefixedUuid(idParam, "PROD-");
        return listingRepository
                .findById(listingId)
                .filter(l -> tenantId.equals(l.getTenantId()))
                .orElseThrow(
                        () ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND, "Publicacion no encontrada"));
    }

    private void updatePrice(ListingJpaEntity listing, Map<String, Object> payload) {
        String priceKey =
                payload.containsKey("price")
                        ? "price"
                        : payload.containsKey("currentPrice") ? "currentPrice" : null;
        if (priceKey != null) {
            String priceStr = asString(payload.get(priceKey), "");
            if (!priceStr.isBlank()) {
                try {
                    listing.setBasePrice(new BigDecimal(priceStr.replaceAll("[^\\d.]", "")));
                    listing.setCurrency("Bs");
                } catch (NumberFormatException ignored) {
                }
            }
        }
    }

    private void updateImage(UUID listingId, Map<String, Object> payload) {
        String img = asString(payload.get("image"), asString(payload.get("imageUrl"), ""));
        if (!img.isBlank()) {
            imageRepository
                    .findFirstByListingIdAndIsPrimaryTrue(listingId)
                    .ifPresentOrElse(
                            existing -> {
                                existing.setImageUrl(img);
                                imageRepository.save(existing);
                            },
                            () -> {
                                ListingImageJpaEntity newImg = new ListingImageJpaEntity();
                                newImg.setId(UUID.randomUUID());
                                newImg.setListingId(listingId);
                                newImg.setImageUrl(img);
                                newImg.setIsPrimary(true);
                                imageRepository.save(newImg);
                            });
        }
    }

    private String normalizeType(String type) {
        if (type == null) return "PRODUCT";
        return switch (type.toUpperCase()) {
            case "SERVICIOS", "SERVICIOS DISPONIBLES", "SERVICE" -> "SERVICE";
            case "OFERTAS", "OFERTAS Y PROMOCIONES", "OFFER" -> "OFFER";
            case "ENCUESTAS", "SURVEY" -> "SURVEY";
            case "POST", "PUBLICACIONES DE INTERACCIÓN", "PUBLICACION" -> "POST";
            case "TEXTO", "TEXT", "PUBLICACIONES DE TEXTO" -> "TEXT";
            default -> "PRODUCT";
        };
    }

    private String normalizeStatus(String status) {
        if (status == null || status.isBlank()) return "ACTIVE";
        return switch (status.toUpperCase()) {
            case "DISPONIBLE", "AVAILABLE", "ACTIVE" -> "ACTIVE";
            case "INACTIVO", "INACTIVE" -> "INACTIVE";
            case "DRAFT", "BORRADOR" -> "DRAFT";
            default -> "ACTIVE";
        };
    }

    private String toStatusLabel(String status) {
        return switch (status.toUpperCase()) {
            case "ACTIVE" -> "Disponible";
            case "INACTIVE" -> "No disponible";
            case "DRAFT" -> "Borrador";
            default -> status;
        };
    }

    private String asString(Object value, String fallback) {
        return value instanceof String s ? s : fallback;
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

    public record PublicacionesResponse(
            List<Map<String, Object>> products,
            List<Map<String, Object>> services,
            List<Map<String, Object>> offers,
            List<Map<String, Object>> surveys,
            List<Map<String, Object>> posts,
            List<Map<String, Object>> textPosts,
            List<Object> users,
            Object latestInteractionNotification) {}
}
