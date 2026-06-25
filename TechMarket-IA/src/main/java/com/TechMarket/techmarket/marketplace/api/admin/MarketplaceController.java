package com.techmarket.techmarket.marketplace.api.admin;

import com.techmarket.techmarket.listings.infrastructure.persistence.jpa.entity.ListingJpaEntity;
import com.techmarket.techmarket.listings.infrastructure.persistence.jpa.repository.ListingImageSpringDataRepository;
import com.techmarket.techmarket.listings.infrastructure.persistence.jpa.repository.ListingSpringDataRepository;
import com.techmarket.techmarket.marketplace.api.admin.response.CategoryTreeResponse;
import com.techmarket.techmarket.marketplace.api.admin.response.CompanyDetailResponse;
import com.techmarket.techmarket.marketplace.api.admin.response.CompanySummaryResponse;
import com.techmarket.techmarket.marketplace.api.admin.response.ProductDetailResponse;
import com.techmarket.techmarket.marketplace.api.admin.response.ProductPageResponse;
import com.techmarket.techmarket.marketplace.api.admin.response.ProductSummaryResponse;
import com.techmarket.techmarket.marketplace.infrastructure.persistence.jpa.entity.CatalogCategoryJpaEntity;
import com.techmarket.techmarket.marketplace.infrastructure.persistence.jpa.repository.CatalogCategorySpringDataRepository;
import com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.entity.SpecialistProfileJpaEntity;
import com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.entity.SpecialistServiceJpaEntity;
import com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.repository.SpecialistProfileSpringDataRepository;
import com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.repository.SpecialistServiceSpringDataRepository;
import com.techmarket.techmarket.tenants.infrastructure.persistence.jpa.entity.TenantJpaEntity;
import com.techmarket.techmarket.tenants.infrastructure.persistence.jpa.repository.TenantSpringDataRepository;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.entity.UserJpaEntity;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.repository.ClientReviewSpringDataRepository;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.repository.UserSpringDataRepository;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/marketplace")
public class MarketplaceController {

    private final ListingSpringDataRepository listingRepository;
    private final ListingImageSpringDataRepository listingImageRepository;
    private final CatalogCategorySpringDataRepository categoryRepository;
    private final TenantSpringDataRepository tenantRepository;
    private final ClientReviewSpringDataRepository reviewRepository;
    private final SpecialistServiceSpringDataRepository specialistServiceRepository;
    private final SpecialistProfileSpringDataRepository specialistProfileRepository;
    private final UserSpringDataRepository userRepository;

    public MarketplaceController(
            ListingSpringDataRepository listingRepository,
            ListingImageSpringDataRepository listingImageRepository,
            CatalogCategorySpringDataRepository categoryRepository,
            TenantSpringDataRepository tenantRepository,
            ClientReviewSpringDataRepository reviewRepository,
            SpecialistServiceSpringDataRepository specialistServiceRepository,
            SpecialistProfileSpringDataRepository specialistProfileRepository,
            UserSpringDataRepository userRepository) {
        this.listingRepository = listingRepository;
        this.listingImageRepository = listingImageRepository;
        this.categoryRepository = categoryRepository;
        this.tenantRepository = tenantRepository;
        this.reviewRepository = reviewRepository;
        this.specialistServiceRepository = specialistServiceRepository;
        this.specialistProfileRepository = specialistProfileRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/products")
    public ProductPageResponse products(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "pagina", defaultValue = "1") int pagina) {
        List<ListingJpaEntity> listings =
                listingRepository.findAll().stream()
                        .filter(this::isProductOrOffer)
                        .filter(listing -> matchesSearch(listing, search))
                        .filter(listing -> matchesCategory(listing, category))
                        .sorted(
                                Comparator.comparing(
                                        ListingJpaEntity::getCreatedAt,
                                        Comparator.nullsLast(Comparator.reverseOrder())))
                        .toList();
        return toPage(listings, pagina);
    }

    @GetMapping("/specialist-services")
    public ProductPageResponse specialistServices(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "pagina", defaultValue = "1") int pagina) {
        List<SpecialistServiceJpaEntity> services =
                specialistServiceRepository.findAll().stream()
                        .filter(service -> matchesSpecialistServiceSearch(service, search))
                        .sorted(
                                Comparator.comparing(
                                        SpecialistServiceJpaEntity::getCreatedAt,
                                        Comparator.nullsLast(Comparator.reverseOrder())))
                        .toList();
        int safePage = Math.max(1, pagina);
        List<ProductSummaryResponse> summaries =
                services.stream()
                        .map(
                                service ->
                                        new ProductSummaryResponse(
                                                "SERV-" + service.getId(),
                                                service.getName(),
                                                service.getPrice(),
                                                null,
                                                0.0))
                        .toList();
        return new ProductPageResponse(services.size(), safePage, summaries);
    }

    @GetMapping("/specialist-services/{serviceId}")
    public Map<String, Object> specialistServiceDetail(@PathVariable String serviceId) {
        String normalized = serviceId.trim();
        if (normalized.regionMatches(true, 0, "SERV-", 0, 5)) {
            normalized = normalized.substring(5);
        }
        UUID id;
        try {
            id = UUID.fromString(normalized);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Identifier is invalid");
        }
        SpecialistServiceJpaEntity service =
                specialistServiceRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new ResponseStatusException(
                                                HttpStatus.NOT_FOUND, "Service not found"));
        UserJpaEntity user = userRepository.findById(service.getUserId()).orElse(null);
        SpecialistProfileJpaEntity profile =
                specialistProfileRepository.findByUserId(service.getUserId()).orElse(null);
        String firstName = user != null && user.getFirstName() != null ? user.getFirstName() : "";
        String lastName = user != null && user.getLastName() != null ? user.getLastName() : "";
        String fullName = (firstName + " " + lastName).trim();
        Map<String, Object> result = new HashMap<>();
        result.put("id", "SERV-" + service.getId());
        result.put("nombre", service.getName() != null ? service.getName() : "");
        result.put("descripcion", service.getDescription());
        result.put("precio", service.getPrice());
        result.put("moneda", service.getCurrency() != null ? service.getCurrency() : "Bs");
        result.put("tipo", service.getServiceType());
        result.put("destacado", service.isFeatured());
        result.put("especialistaId", service.getUserId().toString());
        result.put("especialistaNombre", fullName.isEmpty() ? "Especialista" : fullName);
        result.put("especialistaEspecialidad", profile != null ? profile.getSpecialty() : null);
        result.put("especialistaUbicacion", profile != null ? profile.getLocation() : null);
        return result;
    }

    @GetMapping("/services")
    public ProductPageResponse services(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "pagina", defaultValue = "1") int pagina) {
        List<ListingJpaEntity> listings =
                listingRepository.findAll().stream()
                        .filter(listing -> "SERVICE".equals(listing.getListingType()))
                        .filter(listing -> matchesSearch(listing, search))
                        .sorted(
                                Comparator.comparing(
                                        ListingJpaEntity::getCreatedAt,
                                        Comparator.nullsLast(Comparator.reverseOrder())))
                        .toList();
        return toPage(listings, pagina);
    }

    @GetMapping("/products/{productId}")
    public ProductDetailResponse product(@PathVariable String productId) {
        ListingJpaEntity listing =
                listingRepository
                        .findById(parsePrefixedUuid(productId, "PROD-"))
                        .orElseThrow(
                                () ->
                                        new ResponseStatusException(
                                                HttpStatus.NOT_FOUND, "Product not found"));
        TenantJpaEntity tenant =
                listing.getTenantId() == null
                        ? null
                        : tenantRepository.findById(listing.getTenantId()).orElse(null);
        return new ProductDetailResponse(
                formatProductId(listing.getId()),
                listing.getTitle(),
                listing.getDescription(),
                listing.getBasePrice(),
                List.of(),
                toCompanySummary(tenant),
                0);
    }

    /**
     * Real-data grounding for the AI "Versus" comparator: given the product ids the client picked,
     * returns each listing enriched with price, description, average rating + review count and the
     * seller's reputation. The AI service (TechMarket-AI) is stateless, so this is what makes its
     * verdict reflect actual marketplace data instead of guesses. Missing values are returned as
     * {@code null} so the AI can treat them as uncertainty rather than invent them.
     */
    @PostMapping("/versus-contexto")
    public Map<String, Object> versusContext(@RequestBody VersusContextRequest request) {
        List<String> ids =
                request == null || request.productIds() == null ? List.of() : request.productIds();
        List<Map<String, Object>> productos = new ArrayList<>();
        for (String rawId : ids) {
            UUID listingId;
            try {
                listingId = parsePrefixedUuid(rawId, "PROD-");
            } catch (ResponseStatusException ex) {
                continue;
            }
            listingRepository
                    .findById(listingId)
                    .map(this::toVersusProduct)
                    .ifPresent(productos::add);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("productos", productos);
        return result;
    }

    private Map<String, Object> toVersusProduct(ListingJpaEntity listing) {
        List<Double> ratings =
                reviewRepository.findAllByListingIdOrderByCreatedAtDesc(listing.getId()).stream()
                        .filter(r -> r.getRating() != null)
                        .map(r -> r.getRating().doubleValue())
                        .toList();
        Double calificacion =
                ratings.isEmpty()
                        ? null
                        : ratings.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);

        TenantJpaEntity tenant =
                listing.getTenantId() == null
                        ? null
                        : tenantRepository.findById(listing.getTenantId()).orElse(null);
        String empresa = tenant == null ? null : tenant.getBusinessName();
        Double reputacionVendedor = null;
        if (tenant != null) {
            double rep = averageRatingForTenant(tenant.getId());
            reputacionVendedor = rep > 0 ? rep : null;
        }

        Map<String, Object> p = new HashMap<>();
        p.put("id", formatProductId(listing.getId()));
        p.put("nombre", listing.getTitle());
        p.put("precio", listing.getBasePrice());
        p.put("descripcion", listing.getDescription());
        p.put("calificacion", calificacion);
        p.put("totalResenas", ratings.size());
        p.put("reputacionVendedor", reputacionVendedor);
        p.put("stock", null);
        p.put("empresa", empresa);
        return p;
    }

    @GetMapping("/categories")
    public List<CategoryTreeResponse> categories() {
        return categoryRepository.findAllByParentCategoryIdIsNull().stream()
                .map(this::toCategoryTree)
                .toList();
    }

    @GetMapping("/categories/{categoryId}/products")
    public ProductPageResponse categoryProducts(
            @PathVariable String categoryId,
            @RequestParam(value = "pagina", defaultValue = "1") int pagina) {
        UUID parsedCategoryId = parsePrefixedUuid(categoryId, "CAT-");
        return toPage(listingRepository.findAllByCategoryId(parsedCategoryId), pagina);
    }

    @GetMapping("/companies")
    public List<CompanySummaryResponse> companies() {
        return tenantRepository.findAll().stream().map(this::toCompanySummary).toList();
    }

    @GetMapping("/companies/{companyId}")
    public CompanyDetailResponse company(@PathVariable String companyId) {
        TenantJpaEntity tenant = findTenant(parsePrefixedUuid(companyId, "EMP-"));
        double rating = averageRatingForTenant(tenant.getId());
        return new CompanyDetailResponse(
                formatCompanyId(tenant.getId()),
                tenant.getBusinessName(),
                tenant.getDescription(),
                tenant.getCreatedAt() == null ? null : tenant.getCreatedAt().toLocalDate(),
                0);
    }

    @GetMapping("/companies/{companyId}/publications")
    public List<Map<String, Object>> companyPublications(@PathVariable String companyId) {
        UUID tenantId = parsePrefixedUuid(companyId, "EMP-");
        TenantJpaEntity tenant = findTenant(tenantId);
        String tenantName = tenant.getBusinessName() != null ? tenant.getBusinessName() : "Empresa";

        List<Map<String, Object>> result = new ArrayList<>();
        for (ListingJpaEntity l : listingRepository.findAllByTenantId(tenantId)) {
            String type = l.getListingType() != null ? l.getListingType().toUpperCase() : "";
            if (!"POST".equals(type) && !"TEXT".equals(type)) continue;

            Map<String, Object> item = new HashMap<>();
            item.put("id", "PROD-" + l.getId());
            item.put("title", l.getTitle() != null ? l.getTitle() : "");
            item.put("body", l.getDescription() != null ? l.getDescription() : "");
            item.put("type", type);
            item.put(
                    "createdAt",
                    l.getCreatedAt() != null
                            ? l.getCreatedAt().toString()
                            : java.time.OffsetDateTime.now().toString());
            item.put("companyId", companyId);
            item.put("companyName", tenantName);

            listingImageRepository
                    .findFirstByListingIdAndIsPrimaryTrue(l.getId())
                    .ifPresent(img -> item.put("image", img.getImageUrl()));

            result.add(item);
        }

        result.sort(
                Comparator.comparing(
                        m -> m.getOrDefault("createdAt", "").toString(),
                        Comparator.reverseOrder()));

        return result;
    }

    @GetMapping("/companies/{companyId}/products")
    public ProductPageResponse companyProducts(
            @PathVariable String companyId,
            @RequestParam(value = "pagina", defaultValue = "1") int pagina) {
        UUID tenantId = parsePrefixedUuid(companyId, "EMP-");
        findTenant(tenantId);
        return toPage(listingRepository.findAllByTenantId(tenantId), pagina);
    }

    private ProductPageResponse toPage(List<ListingJpaEntity> listings, int pagina) {
        int safePage = Math.max(1, pagina);
        return new ProductPageResponse(
                listings.size(), safePage, listings.stream().map(this::toProductSummary).toList());
    }

    private ProductSummaryResponse toProductSummary(ListingJpaEntity listing) {
        String imagenPrincipal =
                listingImageRepository
                        .findFirstByListingIdAndIsPrimaryTrue(listing.getId())
                        .map(img -> img.getImageUrl())
                        .orElse(null);

        double calificacion =
                reviewRepository.findAllByListingIdOrderByCreatedAtDesc(listing.getId()).stream()
                        .filter(r -> r.getRating() != null)
                        .mapToDouble(r -> r.getRating().doubleValue())
                        .average()
                        .orElse(0.0);

        return new ProductSummaryResponse(
                formatProductId(listing.getId()),
                listing.getTitle(),
                listing.getBasePrice(),
                imagenPrincipal,
                calificacion);
    }

    private CategoryTreeResponse toCategoryTree(CatalogCategoryJpaEntity category) {
        List<CategoryTreeResponse> children =
                categoryRepository.findAllByParentCategoryId(category.getId()).stream()
                        .map(this::toCategoryTree)
                        .toList();
        return new CategoryTreeResponse(
                formatCategoryId(category.getId()), category.getName(), children);
    }

    private CompanySummaryResponse toCompanySummary(TenantJpaEntity tenant) {
        if (tenant == null) {
            return null;
        }
        double rating = averageRatingForTenant(tenant.getId());
        return new CompanySummaryResponse(
                formatCompanyId(tenant.getId()),
                tenant.getBusinessName(),
                null,
                rating,
                tenant.getDescription(),
                tenant.getBusinessType());
    }

    private double averageRatingForTenant(UUID tenantId) {
        return reviewRepository.findAllByTenantId(tenantId).stream()
                .filter(r -> r.getRating() != null)
                .mapToDouble(r -> r.getRating().doubleValue())
                .average()
                .orElse(0.0);
    }

    private TenantJpaEntity findTenant(UUID tenantId) {
        return tenantRepository
                .findById(tenantId)
                .orElseThrow(
                        () ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND, "Company not found"));
    }

    private boolean isProductOrOffer(ListingJpaEntity listing) {
        String type =
                listing.getListingType() == null
                        ? ""
                        : listing.getListingType().toUpperCase(Locale.ROOT);
        return type.isEmpty() || "PRODUCT".equals(type) || "OFFER".equals(type);
    }

    private boolean matchesSpecialistServiceSearch(
            SpecialistServiceJpaEntity service, String search) {
        if (search == null || search.isBlank()) {
            return true;
        }
        String term = search.trim().toLowerCase(Locale.ROOT);
        String name = (service.getName() == null ? "" : service.getName()).toLowerCase(Locale.ROOT);
        String desc =
                (service.getDescription() == null ? "" : service.getDescription())
                        .toLowerCase(Locale.ROOT);
        return name.contains(term) || desc.contains(term);
    }

    private boolean matchesSearch(ListingJpaEntity listing, String search) {
        if (search == null || search.isBlank()) {
            return true;
        }
        String text =
                (listing.getTitle() == null ? "" : listing.getTitle()).toLowerCase(Locale.ROOT);
        return text.contains(search.trim().toLowerCase(Locale.ROOT));
    }

    private boolean matchesCategory(ListingJpaEntity listing, String category) {
        if (category == null || category.isBlank()) {
            return true;
        }
        UUID parsedCategory = parsePrefixedUuid(category, "CAT-");
        return parsedCategory.equals(listing.getCategoryId());
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

    private String formatProductId(UUID id) {
        return "PROD-" + id;
    }

    private String formatCategoryId(UUID id) {
        return "CAT-" + id;
    }

    private String formatCompanyId(UUID id) {
        return "EMP-" + id;
    }

    /** Request body for {@link #versusContext}: the marketplace product ids to compare. */
    public record VersusContextRequest(List<String> productIds) {}
}
