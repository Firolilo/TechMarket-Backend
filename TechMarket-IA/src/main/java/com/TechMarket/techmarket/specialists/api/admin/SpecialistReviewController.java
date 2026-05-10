package com.techmarket.techmarket.specialists.api.admin;

import com.techmarket.techmarket.specialists.api.admin.request.RespondReviewRequest;
import com.techmarket.techmarket.specialists.api.admin.response.SpecialistReviewDetailResponse;
import com.techmarket.techmarket.specialists.api.admin.response.SpecialistReviewRespondResponse;
import com.techmarket.techmarket.specialists.api.admin.response.SpecialistReviewResponse;
import com.techmarket.techmarket.specialists.application.service.SpecialistIdentitySupport;
import com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.entity.SpecialistReviewJpaEntity;
import com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.repository.SpecialistReviewProjection;
import com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.repository.SpecialistReviewSpringDataRepository;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/specialists/reviews")
public class SpecialistReviewController {

    private final SpecialistIdentitySupport identitySupport;
    private final SpecialistReviewSpringDataRepository reviewRepository;

    public SpecialistReviewController(
            SpecialistIdentitySupport identitySupport,
            SpecialistReviewSpringDataRepository reviewRepository) {
        this.identitySupport = identitySupport;
        this.reviewRepository = reviewRepository;
    }

    @GetMapping
    public List<SpecialistReviewResponse> reviews(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        UUID currentUserId = identitySupport.requireUserId(userId);
        return reviewRepository.findAllByTechnicianUserId(currentUserId).stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/{reviewId}")
    public SpecialistReviewDetailResponse review(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @PathVariable String reviewId) {
        UUID currentUserId = identitySupport.requireUserId(userId);
        UUID id = identitySupport.parsePrefixedUuid(reviewId, "REV-");
        SpecialistReviewProjection review =
                reviewRepository
                        .findByIdAndTechnicianUserId(id, currentUserId)
                        .orElseThrow(
                                () ->
                                        new ResponseStatusException(
                                                HttpStatus.NOT_FOUND, "Review not found"));
        return toDetailResponse(review);
    }

    @PostMapping("/{reviewId}/respond")
    @Transactional
    public SpecialistReviewRespondResponse respond(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @PathVariable String reviewId,
            @Valid @RequestBody RespondReviewRequest request) {
        UUID currentUserId = identitySupport.requireUserId(userId);
        UUID id = identitySupport.parsePrefixedUuid(reviewId, "REV-");
        SpecialistReviewJpaEntity review =
                reviewRepository
                        .findEntityByIdAndTechnicianUserId(id, currentUserId)
                        .orElseThrow(
                                () ->
                                        new ResponseStatusException(
                                                HttpStatus.NOT_FOUND, "Review not found"));
        review.setTechnicianResponse(request.respuesta());
        review.setTechnicianResponseAt(OffsetDateTime.now());
        reviewRepository.save(review);
        return new SpecialistReviewRespondResponse(request.respuesta(), "Respuesta publicada");
    }

    private SpecialistReviewResponse toResponse(SpecialistReviewProjection review) {
        OffsetDateTime createdAt = review.getCreatedAt();
        return new SpecialistReviewResponse(
                "REV-" + review.getId(),
                fullName(review.getCustomerFirstName(), review.getCustomerLastName()),
                stars(review.getRating()),
                review.getComment(),
                review.getServiceName(),
                createdAt == null ? null : createdAt.toLocalDate().toString());
    }

    private SpecialistReviewDetailResponse toDetailResponse(SpecialistReviewProjection review) {
        OffsetDateTime createdAt = review.getCreatedAt();
        return new SpecialistReviewDetailResponse(
                "REV-" + review.getId(),
                fullName(review.getCustomerFirstName(), review.getCustomerLastName()),
                stars(review.getRating()),
                review.getComment(),
                review.getTechnicianResponse(),
                review.getServiceName(),
                createdAt == null ? null : createdAt.toLocalDate().toString());
    }

    private int stars(BigDecimal rating) {
        return rating == null ? 0 : rating.setScale(0, java.math.RoundingMode.HALF_UP).intValue();
    }

    private String fullName(String firstName, String lastName) {
        String normalizedFirstName = firstName == null ? "" : firstName.trim();
        String normalizedLastName = lastName == null ? "" : lastName.trim();
        String fullName = (normalizedFirstName + " " + normalizedLastName).trim();
        return fullName.isBlank() ? null : fullName;
    }
}
