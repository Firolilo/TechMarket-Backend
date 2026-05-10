package com.techmarket.techmarket.ambassadors.infrastructure.persistence.adapter;

import com.techmarket.techmarket.ambassadors.domain.model.Ambassador;
import com.techmarket.techmarket.ambassadors.domain.model.AmbassadorCommission;
import com.techmarket.techmarket.ambassadors.domain.model.AmbassadorReferral;
import com.techmarket.techmarket.ambassadors.domain.port.AmbassadorRepositoryPort;
import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.repository.AmbassadorCommissionSpringDataRepository;
import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.repository.AmbassadorReferralSpringDataRepository;
import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.repository.AmbassadorSpringDataRepository;
import com.techmarket.techmarket.ambassadors.infrastructure.persistence.mapper.AmbassadorPersistenceMapper;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class AmbassadorRepositoryAdapter implements AmbassadorRepositoryPort {

    private final AmbassadorSpringDataRepository ambassadorRepo;
    private final AmbassadorReferralSpringDataRepository referralRepo;
    private final AmbassadorCommissionSpringDataRepository commissionRepo;
    private final AmbassadorPersistenceMapper mapper;

    public AmbassadorRepositoryAdapter(
            AmbassadorSpringDataRepository ambassadorRepo,
            AmbassadorReferralSpringDataRepository referralRepo,
            AmbassadorCommissionSpringDataRepository commissionRepo,
            AmbassadorPersistenceMapper mapper) {
        this.ambassadorRepo = ambassadorRepo;
        this.referralRepo = referralRepo;
        this.commissionRepo = commissionRepo;
        this.mapper = mapper;
    }

    @Override
    public Ambassador save(Ambassador ambassador) {
        return mapper.toDomain(ambassadorRepo.save(mapper.toJpa(ambassador)));
    }

    @Override
    public Optional<Ambassador> findByUserId(UUID userId) {
        return ambassadorRepo.findByUserId(userId).map(mapper::toDomain);
    }

    @Override
    public Optional<Ambassador> findById(UUID id) {
        return ambassadorRepo.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<AmbassadorReferral> findReferralsByAmbassadorId(UUID ambassadorId) {
        return referralRepo.findByAmbassadorId(ambassadorId).stream()
                .map(mapper::toReferralDomain)
                .toList();
    }

    @Override
    public List<AmbassadorCommission> findCommissionsByAmbassadorId(UUID ambassadorId) {
        return commissionRepo.findByAmbassadorId(ambassadorId).stream()
                .map(mapper::toCommissionDomain)
                .toList();
    }
}
