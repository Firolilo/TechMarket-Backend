package com.techmarket.techmarket.tenants.application.service;

import com.techmarket.techmarket.tenants.application.command.CreateTenantCommand;
import com.techmarket.techmarket.tenants.application.query.GetTenantByIdQuery;
import com.techmarket.techmarket.tenants.domain.model.Tenant;
import com.techmarket.techmarket.tenants.domain.port.TenantRepositoryPort;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TenantApplicationService {

    private final TenantRepositoryPort repository;

    public TenantApplicationService(TenantRepositoryPort repository) {
        this.repository = repository;
    }

    @Transactional
    public Tenant create(CreateTenantCommand command) {
        OffsetDateTime now = OffsetDateTime.now();
        Tenant tenant =
                new Tenant(
                        UUID.randomUUID(),
                        command.businessName(),
                        command.legalName(),
                        command.taxId(),
                        command.status(),
                        now,
                        now);
        return repository.save(tenant);
    }

    @Transactional(readOnly = true)
    public Tenant getById(GetTenantByIdQuery query) {
        return repository.findById(query.id()).orElse(null);
    }
}
