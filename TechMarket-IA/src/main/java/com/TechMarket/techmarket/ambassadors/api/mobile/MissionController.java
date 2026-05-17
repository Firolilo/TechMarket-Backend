package com.techmarket.techmarket.ambassadors.api.mobile;

import com.techmarket.techmarket.ambassadors.api.mobile.response.MissionResponse;
import com.techmarket.techmarket.ambassadors.domain.model.Ambassador;
import com.techmarket.techmarket.ambassadors.domain.port.AmbassadorRepositoryPort;
import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.entity.AmbassadorMissionJpaEntity;
import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.repository.AmbassadorMissionSpringDataRepository;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/ambassadors/me/missions")
public class MissionController {

    private final AmbassadorRepositoryPort ambassadorRepo;
    private final AmbassadorMissionSpringDataRepository missionRepo;

    public MissionController(
            AmbassadorRepositoryPort ambassadorRepo,
            AmbassadorMissionSpringDataRepository missionRepo) {
        this.ambassadorRepo = ambassadorRepo;
        this.missionRepo = missionRepo;
    }

    @GetMapping
    public List<MissionResponse> list(Authentication auth) {
        UUID ambassadorId = findAmbassador(auth).id();
        return missionRepo.findAllByAmbassadorIdOrderByCreatedAtDesc(ambassadorId).stream()
                .map(this::toResponse)
                .toList();
    }

    @PatchMapping("/{id}/start")
    public MissionResponse start(Authentication auth, @PathVariable UUID id) {
        UUID ambassadorId = findAmbassador(auth).id();
        AmbassadorMissionJpaEntity mission = findMission(id, ambassadorId);
        if (!"disponible".equalsIgnoreCase(mission.getStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Mission is not in disponible state");
        }
        mission.setStatus("enProgreso");
        mission.setProgress(BigDecimal.ZERO);
        mission.setUpdatedAt(OffsetDateTime.now());
        return toResponse(missionRepo.save(mission));
    }

    @PatchMapping("/{id}/complete")
    public MissionResponse complete(Authentication auth, @PathVariable UUID id) {
        UUID ambassadorId = findAmbassador(auth).id();
        AmbassadorMissionJpaEntity mission = findMission(id, ambassadorId);
        mission.setStatus("completada");
        mission.setProgress(BigDecimal.ONE);
        mission.setUpdatedAt(OffsetDateTime.now());
        return toResponse(missionRepo.save(mission));
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private Ambassador findAmbassador(Authentication auth) {
        UUID userId = UUID.fromString((String) auth.getPrincipal());
        return ambassadorRepo
                .findByUserId(userId)
                .orElseThrow(
                        () ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND, "Ambassador not found"));
    }

    private AmbassadorMissionJpaEntity findMission(UUID id, UUID ambassadorId) {
        return missionRepo
                .findByIdAndAmbassadorId(id, ambassadorId)
                .orElseThrow(
                        () ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND, "Mission not found"));
    }

    private MissionResponse toResponse(AmbassadorMissionJpaEntity m) {
        return new MissionResponse(
                m.getId(),
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
}
