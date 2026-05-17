package com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.repository;

import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.entity.AmbassadorReferralNoteJpaEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AmbassadorReferralNoteSpringDataRepository
        extends JpaRepository<AmbassadorReferralNoteJpaEntity, UUID> {

    List<AmbassadorReferralNoteJpaEntity> findAllByAmbassadorReferralIdOrderByCreatedAtDesc(
            UUID referralId);
}
