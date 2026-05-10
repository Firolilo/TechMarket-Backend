package com.techmarket.techmarket.ambassadors.application.service;

import com.techmarket.techmarket.ambassadors.application.query.GetAmbassadorByUserIdQuery;
import com.techmarket.techmarket.ambassadors.application.query.GetAmbassadorCommissionsQuery;
import com.techmarket.techmarket.ambassadors.application.query.GetAmbassadorReferralsQuery;
import com.techmarket.techmarket.ambassadors.domain.model.Ambassador;
import com.techmarket.techmarket.ambassadors.domain.model.AmbassadorCommission;
import com.techmarket.techmarket.ambassadors.domain.model.AmbassadorReferral;
import com.techmarket.techmarket.ambassadors.domain.port.AmbassadorRepositoryPort;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AmbassadorApplicationService {

    private final AmbassadorRepositoryPort repository;

    public AmbassadorApplicationService(AmbassadorRepositoryPort repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public Ambassador getByUserId(GetAmbassadorByUserIdQuery query) {
        return repository
                .findByUserId(query.userId())
                .orElseThrow(
                        () ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND, "Ambassador not found"));
    }

    @Transactional(readOnly = true)
    public Ambassador getById(UUID ambassadorId) {
        return repository
                .findById(ambassadorId)
                .orElseThrow(
                        () ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND, "Ambassador not found"));
    }

    @Transactional(readOnly = true)
    public List<AmbassadorReferral> getReferrals(GetAmbassadorReferralsQuery query) {
        return repository.findReferralsByAmbassadorId(query.ambassadorId());
    }

    @Transactional(readOnly = true)
    public List<AmbassadorCommission> getCommissions(GetAmbassadorCommissionsQuery query) {
        return repository.findCommissionsByAmbassadorId(query.ambassadorId());
    }
}
