package com.techmarket.techmarket.marketplace.api.admin;

import com.techmarket.techmarket.listings.infrastructure.persistence.jpa.repository.ListingSpringDataRepository;
import com.techmarket.techmarket.marketplace.api.admin.response.ProductReviewResponse;
import com.techmarket.techmarket.marketplace.api.admin.response.ReviewCustomerResponse;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.entity.ClientReviewJpaEntity;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.entity.UserJpaEntity;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.repository.ClientReviewSpringDataRepository;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.repository.UserSpringDataRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/marketplace")
public class MarketplaceReviewController {

    private final ListingSpringDataRepository listingRepository;
    private final ClientReviewSpringDataRepository reviewRepository;
    private final UserSpringDataRepository userRepository;

    public MarketplaceReviewController(
            ListingSpringDataRepository listingRepository,
            ClientReviewSpringDataRepository reviewRepository,
            UserSpringDataRepository userRepository) {
        this.listingRepository = listingRepository;
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/products/{productId}/reviews")
    public List<ProductReviewResponse> productReviews(@PathVariable String productId) {
        UUID listingId = parsePrefixedUuid(productId, "PROD-");
        if (!listingRepository.existsById(listingId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        }
        return reviewRepository.findAllByListingIdOrderByCreatedAtDesc(listingId).stream()
                .map(this::toProductReviewResponse)
                .toList();
    }

    private ProductReviewResponse toProductReviewResponse(ClientReviewJpaEntity review) {
        UserJpaEntity user =
                review.getUserId() == null ? null : userRepository.findById(review.getUserId()).orElse(null);
        return new ProductReviewResponse(
                formatReviewId(review.getId()),
                new ReviewCustomerResponse(customerName(user), null),
                review.getRating() == null ? null : review.getRating().intValue(),
                review.getComment(),
                review.getCreatedAt() == null ? null : review.getCreatedAt().toLocalDate());
    }

    private String customerName(UserJpaEntity user) {
        if (user == null) {
            return "Cliente";
        }
        String first = user.getFirstName() == null ? "" : user.getFirstName().trim();
        String lastInitial =
                user.getLastName() == null || user.getLastName().isBlank()
                        ? ""
                        : " " + user.getLastName().trim().charAt(0) + ".";
        String name = (first + lastInitial).trim();
        return name.isBlank() ? "Cliente" : name;
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
