package com.techmarket.techmarket.tenants.infrastructure.persistence.adapter;

import com.techmarket.techmarket.tenants.domain.model.Tenant;
import com.techmarket.techmarket.tenants.domain.port.TenantRepositoryPort;
import com.techmarket.techmarket.tenants.infrastructure.persistence.jpa.repository.TenantSpringDataRepository;
import com.techmarket.techmarket.tenants.infrastructure.persistence.mapper.TenantPersistenceMapper;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class TenantRepositoryAdapter implements TenantRepositoryPort {

    private final TenantSpringDataRepository springDataRepository;
    private final TenantPersistenceMapper mapper;

    public TenantRepositoryAdapter(
            TenantSpringDataRepository springDataRepository, TenantPersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public Tenant save(Tenant tenant) {
        return mapper.toDomain(springDataRepository.save(mapper.toJpa(tenant)));
    }

    @Override
    public Optional<Tenant> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }
}
