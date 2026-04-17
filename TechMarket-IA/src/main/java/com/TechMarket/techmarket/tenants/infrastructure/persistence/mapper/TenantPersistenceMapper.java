package com.techmarket.techmarket.tenants.infrastructure.persistence.mapper;

import com.techmarket.techmarket.tenants.domain.model.Tenant;
import com.techmarket.techmarket.tenants.infrastructure.persistence.jpa.entity.TenantJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class TenantPersistenceMapper {

    public TenantJpaEntity toJpa(Tenant tenant) {
        TenantJpaEntity entity = new TenantJpaEntity();
        entity.setId(tenant.id());
        entity.setBusinessName(tenant.businessName());
        entity.setLegalName(tenant.legalName());
        entity.setTaxId(tenant.taxId());
        entity.setStatus(tenant.status());
        entity.setCreatedAt(tenant.createdAt());
        entity.setUpdatedAt(tenant.updatedAt());
        return entity;
    }

    public Tenant toDomain(TenantJpaEntity entity) {
        return new Tenant(
                entity.getId(),
                entity.getBusinessName(),
                entity.getLegalName(),
                entity.getTaxId(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
