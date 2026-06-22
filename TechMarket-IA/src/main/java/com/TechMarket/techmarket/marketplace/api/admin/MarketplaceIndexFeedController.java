package com.techmarket.techmarket.marketplace.api.admin;

import com.techmarket.techmarket.listings.infrastructure.persistence.jpa.entity.ListingJpaEntity;
import com.techmarket.techmarket.listings.infrastructure.persistence.jpa.repository.ListingSpringDataRepository;
import com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.entity.SpecialistServiceJpaEntity;
import com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.repository.SpecialistServiceSpringDataRepository;
import com.techmarket.techmarket.tenants.infrastructure.persistence.jpa.repository.TenantSpringDataRepository;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.entity.UserJpaEntity;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.repository.UserSpringDataRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Feeds the catalog (companies' listings + specialists' services) to TechMarket-AI's marketplace
 * semantic index. Source of truth stays here; the AI service embeds {@code content} (title +
 * description) into pgvector. Field names match the AI {@code MarketplaceDocDto} so the ingestion
 * can forward the payload unchanged to {@code POST /api/v1/ai/marketplace/index}.
 */
@RestController
@RequestMapping("/api/marketplace/catalogo-indexable")
public class MarketplaceIndexFeedController {

    private final ListingSpringDataRepository listingRepository;
    private final TenantSpringDataRepository tenantRepository;
    private final SpecialistServiceSpringDataRepository specialistServiceRepository;
    private final UserSpringDataRepository userRepository;

    public MarketplaceIndexFeedController(
            ListingSpringDataRepository listingRepository,
            TenantSpringDataRepository tenantRepository,
            SpecialistServiceSpringDataRepository specialistServiceRepository,
            UserSpringDataRepository userRepository) {
        this.listingRepository = listingRepository;
        this.tenantRepository = tenantRepository;
        this.specialistServiceRepository = specialistServiceRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<IndexableDoc> catalogoIndexable() {
        List<IndexableDoc> docs = new ArrayList<>();

        for (ListingJpaEntity listing : listingRepository.findAll()) {
            if (!"ACTIVE".equalsIgnoreCase(listing.getStatus())) {
                continue;
            }
            String ownerName =
                    listing.getTenantId() == null
                            ? null
                            : tenantRepository
                                    .findById(listing.getTenantId())
                                    .map(t -> t.getBusinessName())
                                    .orElse(null);
            docs.add(
                    new IndexableDoc(
                            "PROD-" + listing.getId(),
                            "empresa",
                            listing.getTitle(),
                            ownerName,
                            text(listing.getTitle(), listing.getDescription())));
        }

        for (SpecialistServiceJpaEntity service : specialistServiceRepository.findAll()) {
            String ownerName =
                    userRepository.findById(service.getUserId()).map(this::fullName).orElse(null);
            docs.add(
                    new IndexableDoc(
                            "SERV-" + service.getId(),
                            "especialista",
                            service.getName(),
                            ownerName,
                            text(service.getName(), service.getDescription())));
        }

        return docs;
    }

    private String text(String title, String description) {
        String t = title == null ? "" : title.trim();
        String d = description == null ? "" : description.trim();
        return (t + ". " + d).trim();
    }

    private String fullName(UserJpaEntity user) {
        String first = user.getFirstName() == null ? "" : user.getFirstName().trim();
        String last = user.getLastName() == null ? "" : user.getLastName().trim();
        String full = (first + " " + last).trim();
        return full.isEmpty() ? null : full;
    }

    /** Mirrors the AI service's {@code MarketplaceDocDto}. */
    public record IndexableDoc(
            String id, String type, String title, String ownerName, String content) {}
}
