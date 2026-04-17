package com.techmarket.techmarket.leads.application.service;

import com.techmarket.techmarket.leads.application.command.CreateLeadCommand;
import com.techmarket.techmarket.leads.application.query.GetLeadByIdQuery;
import com.techmarket.techmarket.leads.domain.model.Lead;
import com.techmarket.techmarket.leads.domain.port.LeadRepositoryPort;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LeadApplicationService {

    private final LeadRepositoryPort repository;

    public LeadApplicationService(LeadRepositoryPort repository) {
        this.repository = repository;
    }

    @Transactional
    public Lead create(CreateLeadCommand command) {
        OffsetDateTime now = OffsetDateTime.now();
        Lead lead =
                new Lead(
                        UUID.randomUUID(),
                        command.tenantId(),
                        command.branchId(),
                        command.userId(),
                        command.listingId(),
                        command.sourceChannel(),
                        command.status(),
                        command.leadScore(),
                        command.notes(),
                        now,
                        now);
        return repository.save(lead);
    }

    @Transactional(readOnly = true)
    public Lead getById(GetLeadByIdQuery query) {
        return repository.findById(query.id()).orElse(null);
    }
}
