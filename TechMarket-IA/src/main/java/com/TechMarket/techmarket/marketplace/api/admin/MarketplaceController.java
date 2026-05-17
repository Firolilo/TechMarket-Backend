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
import com.techmarket.techmarket.tenants.infrastructure.persistence.jpa.entity.TenantJpaEntity;
import com.techmarket.techmarket.tenants.infrastructure.persistence.jpa.repository.TenantSpringDataRepository;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.entity.ClientReviewJpaEntity;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.repository.ClientReviewSpringDataRepository;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    public MarketplaceController(
            ListingSpringDataRepository listingRepository,
            ListingImageSpringDataRepository listingImageRepository,
            CatalogCategorySpringDataRepository categoryRepository,
            TenantSpringDataRepository tenantRepository,
            ClientReviewSpringDataRepository reviewRepository) {
        this.listingRepository = listingRepository;
        this.listingImageRepository = listingImageRepository;
        this.categoryRepository = categoryRepository;
        this.tenantRepository = tenantRepository;
        this.reviewRepository = reviewRepository;
    }

    @GetMapping("/products")
    public ProductPageResponse products(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "pagina", defaultValue = "1") int pagina) {
        List<ListingJpaEntity> listings =
                listingRepository.findAll().stream()
                        .filter(listing -> matchesSearch(listing, search))
                        .filter(listing -> matchesCategory(listing, category))
                        .sorted(
                                Comparator.comparing(
                                        ListingJpaEntity::getCreatedAt,
                                        Comparator.nullsLast(Comparator.reverseOrder())))
                        .toList();
        return toPage(listings, pagina);
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
        String imagenPrincipal = listingImageRepository
                .findFirstByListingIdAndIsPrimaryTrue(listing.getId())
                .map(img -> img.getImageUrl())
                .orElse(null);

        double calificacion = reviewRepository
                .findAllByListingIdOrderByCreatedAtDesc(listing.getId())
                .stream()
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
}
