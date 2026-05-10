package com.techmarket.techmarket.ambassadors.infrastructure.persistence.mapper;

import com.techmarket.techmarket.ambassadors.domain.model.Ambassador;
import com.techmarket.techmarket.ambassadors.domain.model.AmbassadorCommission;
import com.techmarket.techmarket.ambassadors.domain.model.AmbassadorReferral;
import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.entity.AmbassadorCommissionJpaEntity;
import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.entity.AmbassadorJpaEntity;
import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.entity.AmbassadorReferralJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class AmbassadorPersistenceMapper {

    public AmbassadorJpaEntity toJpa(Ambassador ambassador) {
        AmbassadorJpaEntity entity = new AmbassadorJpaEntity();
        entity.setId(ambassador.id());
        entity.setUserId(ambassador.userId());
        entity.setReferralCode(ambassador.referralCode());
        entity.setStatus(ambassador.status());
        entity.setLevel(ambassador.level());
        entity.setActivatedAt(ambassador.activatedAt());
        return entity;
    }

    public Ambassador toDomain(AmbassadorJpaEntity entity) {
        return new Ambassador(
                entity.getId(),
                entity.getUserId(),
                entity.getReferralCode(),
                entity.getStatus(),
                entity.getLevel(),
                entity.getActivatedAt());
    }

    public AmbassadorReferral toReferralDomain(AmbassadorReferralJpaEntity entity) {
        return new AmbassadorReferral(
                entity.getId(),
                entity.getAmbassadorId(),
                entity.getTenantId(),
                entity.getAttributionChannel(),
                entity.getUsedCode(),
                entity.getStatus(),
                entity.getCreatedAt());
    }

    public AmbassadorCommission toCommissionDomain(AmbassadorCommissionJpaEntity entity) {
        return new AmbassadorCommission(
                entity.getId(),
                entity.getAmbassadorId(),
                entity.getAmbassadorReferralId(),
                entity.getCommissionRuleId(),
                entity.getAttributionType(),
                entity.getEventType(),
                entity.getReferenceType(),
                entity.getReferenceId(),
                entity.getAmount(),
                entity.getStatus(),
                entity.getGeneratedAt());
    }
}
