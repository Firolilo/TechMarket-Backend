package com.techmarket.techmarket.ambassadors.api.admin;

import com.techmarket.techmarket.ambassadors.application.service.AmbassadorIdentitySupport;
import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.entity.AmbassadorJpaEntity;
import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.repository.AmbassadorCommissionSpringDataRepository;
import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.repository.AmbassadorReferralSpringDataRepository;
import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Real-data context that grounds the ambassador AI assistant. TechMarket-AI is stateless (no DB),
 * so this exposes the ambassador's actual level, referral portfolio and commissions so the Next.js
 * client can forward it as the AI {@code context}. See the AI service's AmbassadorAiService.
 */
@RestController
@RequestMapping("/api/ambassadors/ia-contexto")
public class AmbassadorIaContextController {

    private final AmbassadorIdentitySupport identitySupport;
    private final AmbassadorReferralSpringDataRepository referralRepository;
    private final AmbassadorCommissionSpringDataRepository commissionRepository;

    public AmbassadorIaContextController(
            AmbassadorIdentitySupport identitySupport,
            AmbassadorReferralSpringDataRepository referralRepository,
            AmbassadorCommissionSpringDataRepository commissionRepository) {
        this.identitySupport = identitySupport;
        this.referralRepository = referralRepository;
        this.commissionRepository = commissionRepository;
    }

    @GetMapping
    public AmbassadorIaContexto contexto(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        AmbassadorJpaEntity ambassador = identitySupport.requireAmbassador(userId);
        UUID ambassadorId = ambassador.getId();

        long totalReferidos = referralRepository.countByAmbassadorId(ambassadorId);
        long activos =
                referralRepository.countByAmbassadorIdAndStatusIgnoreCase(ambassadorId, "activo");
        long prospectos =
                referralRepository.countByAmbassadorIdAndStatusIgnoreCase(ambassadorId, "prospecto");

        BigDecimal totalComisiones = commissionRepository.sumAmountByAmbassadorId(ambassadorId);
        long cantidadComisiones = commissionRepository.findByAmbassadorId(ambassadorId).size();

        return new AmbassadorIaContexto(
                new Perfil(
                        ambassador.getLevel(),
                        ambassador.getReferralCode(),
                        ambassador.getStatus(),
                        ambassador.getCity(),
                        ambassador.getCountry()),
                new Referidos(totalReferidos, activos, prospectos),
                new Comisiones(totalComisiones, cantidadComisiones));
    }

    public record AmbassadorIaContexto(
            Perfil perfil, Referidos referidos, Comisiones comisiones) {}

    public record Perfil(
            String nivel, String codigoReferido, String estado, String ciudad, String pais) {}

    public record Referidos(long total, long activos, long prospectos) {}

    public record Comisiones(BigDecimal totalBs, long cantidad) {}
}
