package com.techmarket.techmarket.ambassadors.api.admin;

import com.techmarket.techmarket.ambassadors.api.response.AmbassadorCommissionResponse;
import com.techmarket.techmarket.ambassadors.api.response.AmbassadorReferralResponse;
import com.techmarket.techmarket.ambassadors.api.response.AmbassadorResponse;
import com.techmarket.techmarket.ambassadors.application.query.GetAmbassadorByUserIdQuery;
import com.techmarket.techmarket.ambassadors.application.query.GetAmbassadorCommissionsQuery;
import com.techmarket.techmarket.ambassadors.application.query.GetAmbassadorReferralsQuery;
import com.techmarket.techmarket.ambassadors.application.service.AmbassadorApplicationService;
import com.techmarket.techmarket.ambassadors.domain.model.Ambassador;
import com.techmarket.techmarket.ambassadors.domain.model.AmbassadorCommission;
import com.techmarket.techmarket.ambassadors.domain.model.AmbassadorReferral;
import java.util.List;
import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ambassadors")
public class AmbassadorController {

    private final AmbassadorApplicationService service;

    public AmbassadorController(AmbassadorApplicationService service) {
        this.service = service;
    }

    @GetMapping("/me")
    public AmbassadorResponse getMe(Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        Ambassador ambassador = service.getByUserId(new GetAmbassadorByUserIdQuery(userId));
        return toResponse(ambassador);
    }

    @GetMapping("/{id}")
    public AmbassadorResponse getById(@PathVariable UUID id) {
        return toResponse(service.getById(id));
    }

    @GetMapping("/{id}/referrals")
    public List<AmbassadorReferralResponse> getReferrals(@PathVariable UUID id) {
        return service.getReferrals(new GetAmbassadorReferralsQuery(id)).stream()
                .map(this::toReferralResponse)
                .toList();
    }

    @GetMapping("/{id}/commissions")
    public List<AmbassadorCommissionResponse> getCommissions(@PathVariable UUID id) {
        return service.getCommissions(new GetAmbassadorCommissionsQuery(id)).stream()
                .map(this::toCommissionResponse)
                .toList();
    }

    private AmbassadorResponse toResponse(Ambassador a) {
        return new AmbassadorResponse(
                a.id(), a.userId(), a.referralCode(), a.status(), a.level(), a.activatedAt());
    }

    private AmbassadorReferralResponse toReferralResponse(AmbassadorReferral r) {
        return new AmbassadorReferralResponse(
                r.id(),
                r.ambassadorId(),
                r.tenantId(),
                r.attributionChannel(),
                r.usedCode(),
                r.status(),
                r.createdAt());
    }

    private AmbassadorCommissionResponse toCommissionResponse(AmbassadorCommission c) {
        return new AmbassadorCommissionResponse(
                c.id(),
                c.ambassadorId(),
                c.ambassadorReferralId(),
                c.attributionType(),
                c.eventType(),
                c.referenceType(),
                c.referenceId(),
                c.amount(),
                c.status(),
                c.generatedAt());
    }
}
