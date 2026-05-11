package com.techmarket.techmarket.ambassadors.api.mobile;

import com.techmarket.techmarket.ambassadors.api.mobile.response.OpportunityResponse;
import com.techmarket.techmarket.ambassadors.domain.model.Ambassador;
import com.techmarket.techmarket.ambassadors.domain.port.AmbassadorRepositoryPort;
import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.entity.AmbassadorOpportunityJpaEntity;
import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.repository.AmbassadorOpportunitySpringDataRepository;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/ambassadors/me/opportunities")
public class OpportunityController {

    private final AmbassadorRepositoryPort ambassadorRepo;
    private final AmbassadorOpportunitySpringDataRepository opportunityRepo;

    public OpportunityController(
            AmbassadorRepositoryPort ambassadorRepo,
            AmbassadorOpportunitySpringDataRepository opportunityRepo) {
        this.ambassadorRepo = ambassadorRepo;
        this.opportunityRepo = opportunityRepo;
    }

    @GetMapping
    public List<OpportunityResponse> list(Authentication auth) {
        UUID ambassadorId = findAmbassador(auth).id();
        return opportunityRepo
                .findByAmbassadorIdOrderByDetectedAtDesc(ambassadorId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @PatchMapping("/{id}/status")
    public OpportunityResponse updateStatus(
            Authentication auth,
            @PathVariable UUID id,
            @RequestBody Map<String, String> body) {
        UUID ambassadorId = findAmbassador(auth).id();
        AmbassadorOpportunityJpaEntity entity = opportunityRepo
                .findByIdAndAmbassadorId(id, ambassadorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        String newStatus = body.get("status");
        if (newStatus != null) {
            entity.setStatus(newStatus);
            entity.setUpdatedAt(OffsetDateTime.now());
            opportunityRepo.save(entity);
        }
        return toResponse(entity);
    }

    @PatchMapping("/{id}/save")
    public OpportunityResponse toggleSave(
            Authentication auth,
            @PathVariable UUID id,
            @RequestBody Map<String, Boolean> body) {
        UUID ambassadorId = findAmbassador(auth).id();
        AmbassadorOpportunityJpaEntity entity = opportunityRepo
                .findByIdAndAmbassadorId(id, ambassadorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        Boolean saved = body.get("saved");
        if (saved != null) {
            entity.setSaved(saved);
            entity.setUpdatedAt(OffsetDateTime.now());
            opportunityRepo.save(entity);
        }
        return toResponse(entity);
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private Ambassador findAmbassador(Authentication auth) {
        UUID userId = UUID.fromString((String) auth.getPrincipal());
        return ambassadorRepo
                .findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ambassador not found"));
    }

    private OpportunityResponse toResponse(AmbassadorOpportunityJpaEntity e) {
        return new OpportunityResponse(
                e.getId(),
                e.getOpportunityType(),
                e.getZone(),
                e.getDescription(),
                e.getPotential(),
                e.getDataSource(),
                e.getStatus(),
                e.isSaved());
    }
}
