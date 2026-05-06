package com.techmarket.techmarket.users.api.admin.client;

import com.techmarket.techmarket.listings.infrastructure.persistence.jpa.entity.ListingJpaEntity;
import com.techmarket.techmarket.listings.infrastructure.persistence.jpa.repository.ListingSpringDataRepository;
import com.techmarket.techmarket.tenants.infrastructure.persistence.jpa.repository.TenantSpringDataRepository;
import com.techmarket.techmarket.users.api.admin.client.request.UpsertReviewRequest;
import com.techmarket.techmarket.users.api.admin.client.response.CreateReviewResponse;
import com.techmarket.techmarket.users.api.admin.client.response.MessageResponse;
import com.techmarket.techmarket.users.api.admin.client.response.ReviewResponse;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.entity.ClientReviewJpaEntity;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.repository.ClientReviewSpringDataRepository;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/clients/reviews")
public class ClientReviewController {

    private final ClientReviewSpringDataRepository reviewRepository;
    private final ListingSpringDataRepository listingRepository;
    private final TenantSpringDataRepository tenantRepository;

    public ClientReviewController(
            ClientReviewSpringDataRepository reviewRepository,
            ListingSpringDataRepository listingRepository,
            TenantSpringDataRepository tenantRepository) {
        this.reviewRepository = reviewRepository;
        this.listingRepository = listingRepository;
        this.tenantRepository = tenantRepository;
    }

    @PostMapping("/products/{productId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CreateReviewResponse createProductReview(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @PathVariable String productId,
            @Valid @RequestBody UpsertReviewRequest request) {
        UUID currentUserId = parseUserId(userId);
        ListingJpaEntity listing =
                listingRepository
                        .findById(parsePrefixedUuid(productId, "PROD-"))
                        .orElseThrow(
                                () ->
                                        new ResponseStatusException(
                                                HttpStatus.NOT_FOUND, "Product not found"));
        ClientReviewJpaEntity review =
                newReview(currentUserId, listing.getTenantId(), listing.getId(), request);
        return new CreateReviewResponse(
                formatReviewId(reviewRepository.save(review).getId()), "Reseña publicada");
    }

    @PutMapping("/{reviewId}")
    public ReviewResponse updateReview(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @PathVariable String reviewId,
            @Valid @RequestBody UpsertReviewRequest request) {
        UUID currentUserId = parseUserId(userId);
        ClientReviewJpaEntity review =
                reviewRepository
                        .findByIdAndUserId(parsePrefixedUuid(reviewId, "REV-"), currentUserId)
                        .orElseThrow(
                                () ->
                                        new ResponseStatusException(
                                                HttpStatus.NOT_FOUND, "Review not found"));
        applyReviewFields(review, request);
        review.setUpdatedAt(OffsetDateTime.now());
        return toReviewResponse(reviewRepository.save(review));
    }

    @DeleteMapping("/{reviewId}")
    public MessageResponse deleteReview(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @PathVariable String reviewId) {
        UUID currentUserId = parseUserId(userId);
        ClientReviewJpaEntity review =
                reviewRepository
                        .findByIdAndUserId(parsePrefixedUuid(reviewId, "REV-"), currentUserId)
                        .orElseThrow(
                                () ->
                                        new ResponseStatusException(
                                                HttpStatus.NOT_FOUND, "Review not found"));
        reviewRepository.delete(review);
        return new MessageResponse("Reseña eliminada");
    }

    @PostMapping("/companies/{companyId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CreateReviewResponse createCompanyReview(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @PathVariable String companyId,
            @Valid @RequestBody UpsertReviewRequest request) {
        UUID currentUserId = parseUserId(userId);
        UUID tenantId = parsePrefixedUuid(companyId, "EMP-");
        if (!tenantRepository.existsById(tenantId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Company not found");
        }
        ClientReviewJpaEntity review = newReview(currentUserId, tenantId, null, request);
        return new CreateReviewResponse(
                formatReviewId(reviewRepository.save(review).getId()), "Reseña publicada");
    }

    private ClientReviewJpaEntity newReview(
            UUID userId, UUID tenantId, UUID listingId, UpsertReviewRequest request) {
        OffsetDateTime now = OffsetDateTime.now();
        ClientReviewJpaEntity review = new ClientReviewJpaEntity();
        review.setId(UUID.randomUUID());
        review.setUserId(userId);
        review.setTenantId(tenantId);
        review.setListingId(listingId);
        review.setModerationStatus("Publicado");
        review.setCreatedAt(now);
        review.setUpdatedAt(now);
        applyReviewFields(review, request);
        return review;
    }

    private void applyReviewFields(ClientReviewJpaEntity review, UpsertReviewRequest request) {
        review.setRating(BigDecimal.valueOf(request.calificacion()));
        review.setComment(request.comentario());
    }

    private ReviewResponse toReviewResponse(ClientReviewJpaEntity review) {
        return new ReviewResponse(
                formatReviewId(review.getId()),
                review.getRating() == null ? null : review.getRating().intValue(),
                review.getComment(),
                review.getCreatedAt() == null ? null : review.getCreatedAt().toLocalDate());
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

    private String formatReviewId(UUID id) {
        return "REV-" + id;
    }
}
