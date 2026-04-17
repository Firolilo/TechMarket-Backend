package com.techmarket.techmarket.tenants.domain.port;

import com.techmarket.techmarket.tenants.domain.model.Tenant;
import java.util.Optional;
import java.util.UUID;

public interface TenantRepositoryPort {

    Tenant save(Tenant tenant);

    Optional<Tenant> findById(UUID id);
}
