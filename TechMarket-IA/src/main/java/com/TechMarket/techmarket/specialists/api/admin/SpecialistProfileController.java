package com.techmarket.techmarket.specialists.api.admin;

import com.techmarket.techmarket.specialists.api.admin.request.UpdateSpecialistPhotoRequest;
import com.techmarket.techmarket.specialists.api.admin.request.UpdateSpecialistProfileRequest;
import com.techmarket.techmarket.specialists.api.admin.response.MessageResponse;
import com.techmarket.techmarket.specialists.api.admin.response.SpecialistPhotoResponse;
import com.techmarket.techmarket.specialists.api.admin.response.SpecialistProfileResponse;
import com.techmarket.techmarket.specialists.api.admin.response.SpecialistProfileStatsResponse;
import com.techmarket.techmarket.specialists.application.service.SpecialistIdentitySupport;
import com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.entity.SpecialistProfileJpaEntity;
import com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.repository.SpecialistProfileSpringDataRepository;
import com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.repository.SpecialistReviewStatsProjection;
import com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.repository.SpecialistReviewStatsSpringDataRepository;
import com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.repository.SpecialistServiceAppointmentSpringDataRepository;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.entity.UserJpaEntity;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.repository.UserSpringDataRepository;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/specialists/profile")
public class SpecialistProfileController {

    private final SpecialistIdentitySupport identitySupport;
    private final UserSpringDataRepository userRepository;
    private final SpecialistProfileSpringDataRepository profileRepository;
    private final SpecialistServiceAppointmentSpringDataRepository appointmentRepository;
    private final SpecialistReviewStatsSpringDataRepository reviewStatsRepository;

    public SpecialistProfileController(
            SpecialistIdentitySupport identitySupport,
            UserSpringDataRepository userRepository,
            SpecialistProfileSpringDataRepository profileRepository,
            SpecialistServiceAppointmentSpringDataRepository appointmentRepository,
            SpecialistReviewStatsSpringDataRepository reviewStatsRepository) {
        this.identitySupport = identitySupport;
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.appointmentRepository = appointmentRepository;
        this.reviewStatsRepository = reviewStatsRepository;
    }

    @GetMapping
    public SpecialistProfileResponse profile(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        UserJpaEntity user = identitySupport.requireUser(userId);
        SpecialistProfileJpaEntity profile = profileRepository.findByUserId(user.getId()).orElse(null);
        return toProfileResponse(user, profile, statsFor(user.getId()).calificacionPromedio());
    }

    @PutMapping
    @Transactional
    public MessageResponse updateProfile(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @Valid @RequestBody UpdateSpecialistProfileRequest request) {
        UserJpaEntity user = identitySupport.requireUser(userId);
        SpecialistProfileJpaEntity profile = getOrCreateProfile(user.getId());
        OffsetDateTime now = OffsetDateTime.now();

        if (request.nombre() != null) {
            user.setFirstName(request.nombre());
        }
        if (request.apellido() != null) {
            user.setLastName(request.apellido());
        }
        if (request.especialidad() != null) {
            profile.setSpecialty(request.especialidad());
        }
        if (request.ubicacion() != null) {
            profile.setLocation(request.ubicacion());
        }

        user.setUpdatedAt(now);
        profile.setUpdatedAt(now);
        userRepository.save(user);
        profileRepository.save(profile);
        return new MessageResponse("Perfil actualizado correctamente");
    }

    @GetMapping("/stats")
    public SpecialistProfileStatsResponse stats(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        return statsFor(identitySupport.requireUserId(userId));
    }

    @PostMapping("/photo")
    public SpecialistPhotoResponse updatePhoto(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @Valid @RequestBody UpdateSpecialistPhotoRequest request) {
        UUID currentUserId = identitySupport.requireUserId(userId);
        SpecialistProfileJpaEntity profile = getOrCreateProfile(currentUserId);
        profile.setPhotoUrl(request.url());
        profile.setUpdatedAt(OffsetDateTime.now());
        profileRepository.save(profile);
        return new SpecialistPhotoResponse(request.url());
    }

    private SpecialistProfileStatsResponse statsFor(UUID userId) {
        long completed = appointmentRepository.countCompletedByTechnicianUserId(userId);
        SpecialistReviewStatsProjection reviewStats =
                reviewStatsRepository.findStatsByTechnicianUserId(userId);
        long totalReviews = reviewStats == null || reviewStats.getTotalReviews() == null
                ? 0
                : reviewStats.getTotalReviews();
        BigDecimal averageRating = reviewStats == null ? null : reviewStats.getAverageRating();
        return new SpecialistProfileStatsResponse(
                completed, totalReviews, normalizeAverage(averageRating));
    }

    private SpecialistProfileJpaEntity getOrCreateProfile(UUID userId) {
        return profileRepository
                .findByUserId(userId)
                .orElseGet(
                        () -> {
                            OffsetDateTime now = OffsetDateTime.now();
                            SpecialistProfileJpaEntity profile = new SpecialistProfileJpaEntity();
                            profile.setId(UUID.randomUUID());
                            profile.setUserId(userId);
                            profile.setCreatedAt(now);
                            profile.setUpdatedAt(now);
                            return profile;
                        });
    }

    private SpecialistProfileResponse toProfileResponse(
            UserJpaEntity user, SpecialistProfileJpaEntity profile, BigDecimal averageRating) {
        return new SpecialistProfileResponse(
                identitySupport.formatSpecialistId(user.getId()),
                fullName(user),
                profile == null ? null : profile.getSpecialty(),
                profile == null ? null : profile.getLocation(),
                averageRating);
    }

    private BigDecimal normalizeAverage(BigDecimal averageRating) {
        if (averageRating == null) {
            return BigDecimal.ZERO.setScale(1);
        }
        return averageRating.setScale(1, RoundingMode.HALF_UP);
    }

    private String fullName(UserJpaEntity user) {
        String firstName = user.getFirstName() == null ? "" : user.getFirstName().trim();
        String lastName = user.getLastName() == null ? "" : user.getLastName().trim();
        String fullName = (firstName + " " + lastName).trim();
        return fullName.isBlank() ? user.getEmail() : fullName;
    }
}
