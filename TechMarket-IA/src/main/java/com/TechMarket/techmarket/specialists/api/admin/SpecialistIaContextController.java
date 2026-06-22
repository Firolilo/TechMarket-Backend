package com.techmarket.techmarket.specialists.api.admin;

import com.techmarket.techmarket.specialists.application.service.SpecialistIdentitySupport;
import com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.entity.SpecialistProfileJpaEntity;
import com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.entity.SpecialistServiceJpaEntity;
import com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.repository.SpecialistProfileSpringDataRepository;
import com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.repository.SpecialistReviewStatsProjection;
import com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.repository.SpecialistReviewStatsSpringDataRepository;
import com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.repository.SpecialistServiceAppointmentSpringDataRepository;
import com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.repository.SpecialistServiceSpringDataRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Real-data context that grounds the specialist AI assistant. TechMarket-AI is stateless (no DB),
 * so this exposes the specialist's actual reputation, catalog of services and activity so the
 * Next.js proxy can forward it as the AI {@code context}. See the AI service's EspecialistaAiService.
 */
@RestController
@RequestMapping("/api/specialists/ia-contexto")
public class SpecialistIaContextController {

    private static final int MAX_SERVICES = 5;

    private final SpecialistIdentitySupport identitySupport;
    private final SpecialistProfileSpringDataRepository profileRepository;
    private final SpecialistReviewStatsSpringDataRepository reviewStatsRepository;
    private final SpecialistServiceAppointmentSpringDataRepository appointmentRepository;
    private final SpecialistServiceSpringDataRepository serviceRepository;

    public SpecialistIaContextController(
            SpecialistIdentitySupport identitySupport,
            SpecialistProfileSpringDataRepository profileRepository,
            SpecialistReviewStatsSpringDataRepository reviewStatsRepository,
            SpecialistServiceAppointmentSpringDataRepository appointmentRepository,
            SpecialistServiceSpringDataRepository serviceRepository) {
        this.identitySupport = identitySupport;
        this.profileRepository = profileRepository;
        this.reviewStatsRepository = reviewStatsRepository;
        this.appointmentRepository = appointmentRepository;
        this.serviceRepository = serviceRepository;
    }

    @GetMapping
    public SpecialistIaContexto contexto(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        UUID currentUserId = identitySupport.requireUserId(userId);

        SpecialistProfileJpaEntity profile =
                profileRepository.findByUserId(currentUserId).orElse(null);

        SpecialistReviewStatsProjection stats =
                reviewStatsRepository.findStatsByTechnicianUserId(currentUserId);
        long totalReviews =
                stats == null || stats.getTotalReviews() == null ? 0L : stats.getTotalReviews();
        double avgRating =
                stats == null || stats.getAverageRating() == null
                        ? 0.0
                        : round1(stats.getAverageRating());

        long completed = appointmentRepository.countCompletedByTechnicianUserId(currentUserId);
        long pendingRequests =
                appointmentRepository.findRequestsByTechnicianUserId(currentUserId).size();

        List<SpecialistServiceJpaEntity> services =
                serviceRepository.findAllByUserIdOrderByCreatedAtDesc(currentUserId);
        List<ServicioItem> servicios =
                services.stream()
                        .limit(MAX_SERVICES)
                        .map(
                                s ->
                                        new ServicioItem(
                                                s.getName(),
                                                s.getServiceType(),
                                                s.getPrice(),
                                                s.isFeatured()))
                        .toList();

        return new SpecialistIaContexto(
                new Perfil(
                        profile == null ? null : profile.getSpecialty(),
                        profile == null ? null : profile.getLocation()),
                new Reputacion(avgRating, totalReviews),
                new Actividad(services.size(), completed, pendingRequests),
                servicios);
    }

    private double round1(BigDecimal value) {
        return value.setScale(1, RoundingMode.HALF_UP).doubleValue();
    }

    public record SpecialistIaContexto(
            Perfil perfil, Reputacion reputacion, Actividad actividad, List<ServicioItem> servicios) {}

    public record Perfil(String especialidad, String ubicacion) {}

    public record Reputacion(double calificacionPromedio, long totalResenas) {}

    public record Actividad(long serviciosPublicados, long trabajosCompletados, long solicitudesPendientes) {}

    public record ServicioItem(String nombre, String tipo, BigDecimal precio, boolean destacado) {}
}
