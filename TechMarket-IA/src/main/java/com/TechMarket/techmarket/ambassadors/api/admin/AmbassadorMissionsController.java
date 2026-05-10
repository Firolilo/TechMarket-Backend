package com.techmarket.techmarket.ambassadors.api.admin;

import com.techmarket.techmarket.ambassadors.api.admin.response.AmbassadorMessageResponse;
import com.techmarket.techmarket.ambassadors.api.admin.response.AmbassadorMissionResponse;
import com.techmarket.techmarket.ambassadors.application.service.AmbassadorIdentitySupport;
import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.entity.AmbassadorMissionJpaEntity;
import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.repository.AmbassadorMissionSpringDataRepository;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/// Endpoints de Misiones para el módulo Embajador.
@RestController
@RequestMapping("/api/ambassadors/missions")
public class AmbassadorMissionsController {

    private final AmbassadorIdentitySupport identitySupport;
    private final AmbassadorMissionSpringDataRepository missionRepository;

    public AmbassadorMissionsController(
            AmbassadorIdentitySupport identitySupport,
            AmbassadorMissionSpringDataRepository missionRepository) {
        this.identitySupport = identitySupport;
        this.missionRepository = missionRepository;
    }

    // ── GET /api/ambassadors/missions ──────────────────────────────────────
    @GetMapping
    public List<AmbassadorMissionResponse> listMissions(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        UUID ambassadorId = identitySupport.requireAmbassadorId(userId);
        List<AmbassadorMissionJpaEntity> missions =
                missionRepository.findAllByAmbassadorIdOrderByCreatedAtDesc(ambassadorId);
        if (missions.isEmpty()) {
            missions = seedDefaultMissions(ambassadorId);
        }
        return missions.stream().map(this::toResponse).toList();
    }

    // ── PATCH /api/ambassadors/missions/{id}/start ─────────────────────────
    @PatchMapping("/{missionId}/start")
    public AmbassadorMessageResponse startMission(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @PathVariable String missionId) {
        UUID ambassadorId = identitySupport.requireAmbassadorId(userId);
        UUID id = identitySupport.parsePrefixedUuid(missionId, "MSN-");
        AmbassadorMissionJpaEntity mission =
                missionRepository
                        .findByIdAndAmbassadorId(id, ambassadorId)
                        .orElseThrow(
                                () ->
                                        new ResponseStatusException(
                                                HttpStatus.NOT_FOUND, "Mission not found"));
        if (!"disponible".equalsIgnoreCase(mission.getStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Mission is not in disponible state");
        }
        mission.setStatus("enProgreso");
        mission.setProgress(BigDecimal.ZERO);
        mission.setUpdatedAt(OffsetDateTime.now());
        missionRepository.save(mission);
        return new AmbassadorMessageResponse("Misión iniciada correctamente");
    }

    // ── PATCH /api/ambassadors/missions/{id}/complete ──────────────────────
    @PatchMapping("/{missionId}/complete")
    public AmbassadorMessageResponse completeMission(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @PathVariable String missionId) {
        UUID ambassadorId = identitySupport.requireAmbassadorId(userId);
        UUID id = identitySupport.parsePrefixedUuid(missionId, "MSN-");
        AmbassadorMissionJpaEntity mission =
                missionRepository
                        .findByIdAndAmbassadorId(id, ambassadorId)
                        .orElseThrow(
                                () ->
                                        new ResponseStatusException(
                                                HttpStatus.NOT_FOUND, "Mission not found"));
        mission.setStatus("completada");
        mission.setProgress(BigDecimal.ONE);
        mission.setUpdatedAt(OffsetDateTime.now());
        missionRepository.save(mission);
        return new AmbassadorMessageResponse("Misión completada correctamente");
    }

    // ── Helpers ────────────────────────────────────────────────────────────

    private AmbassadorMissionResponse toResponse(AmbassadorMissionJpaEntity m) {
        return new AmbassadorMissionResponse(
                identitySupport.formatMissionId(m.getId()),
                m.getTitle(),
                m.getDescription(),
                m.getBenefit(),
                m.getMissionType() != null ? m.getMissionType() : "hardware",
                m.getPriority() != null ? m.getPriority() : "normal",
                m.getStatus() != null ? m.getStatus() : "disponible",
                parseSteps(m.getSteps()),
                m.getCompletionCriteria(),
                m.getProgress() != null ? m.getProgress().doubleValue() : null);
    }

    private List<String> parseSteps(String stepsText) {
        if (stepsText == null || stepsText.isBlank()) return Collections.emptyList();
        return Arrays.stream(stepsText.split("\\|"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }

    private List<AmbassadorMissionJpaEntity> seedDefaultMissions(UUID ambassadorId) {
        OffsetDateTime now = OffsetDateTime.now();
        List<AmbassadorMissionJpaEntity> defaults =
                List.of(
                        buildMission(
                                ambassadorId,
                                "Registra tu primer referido",
                                "Invita a una empresa o cliente a registrarse en TechMarket usando tu código.",
                                "Comisión de Bs 50 al confirmar el registro",
                                "hardware",
                                "alta",
                                "disponible",
                                "Busca prospectos|Envía tu código de referido|Confirma el registro",
                                "Referido confirmado en plataforma",
                                now),
                        buildMission(
                                ambassadorId,
                                "Completa tu perfil de embajador",
                                "Agrega foto, descripción y métodos de pago a tu perfil.",
                                "Acceso a comisiones avanzadas",
                                "software",
                                "normal",
                                "disponible",
                                "Agrega foto de perfil|Escribe tu descripción|Configura método de pago",
                                "Perfil completado al 100%",
                                now.minusSeconds(10)),
                        buildMission(
                                ambassadorId,
                                "Genera impacto en hardware",
                                "Refiere al menos 3 negocios del sector tecnológico en hardware.",
                                "Bono de Bs 200 al completar",
                                "hardware",
                                "alta",
                                "disponible",
                                "Identifica 3 negocios de hardware|Contacta y presenta TechMarket|Confirma sus registros",
                                "3 referidos de hardware activos",
                                now.minusSeconds(20)),
                        buildMission(
                                ambassadorId,
                                "Activa un referido en software",
                                "Ayuda a un negocio de software a completar su onboarding.",
                                "Comisión extra del 2%",
                                "software",
                                "normal",
                                "disponible",
                                "Selecciona un prospecto de software|Guía el onboarding|Confirma la activación",
                                "Referido con onboarding al 100%",
                                now.minusSeconds(30)));
        return missionRepository.saveAll(defaults);
    }

    private AmbassadorMissionJpaEntity buildMission(
            UUID ambassadorId,
            String title,
            String description,
            String benefit,
            String type,
            String priority,
            String status,
            String steps,
            String criteria,
            OffsetDateTime createdAt) {
        AmbassadorMissionJpaEntity m = new AmbassadorMissionJpaEntity();
        m.setId(UUID.randomUUID());
        m.setAmbassadorId(ambassadorId);
        m.setTitle(title);
        m.setDescription(description);
        m.setBenefit(benefit);
        m.setMissionType(type);
        m.setPriority(priority);
        m.setStatus(status);
        m.setSteps(steps);
        m.setCompletionCriteria(criteria);
        m.setProgress(null);
        m.setCreatedAt(createdAt);
        m.setUpdatedAt(createdAt);
        return m;
    }
}
