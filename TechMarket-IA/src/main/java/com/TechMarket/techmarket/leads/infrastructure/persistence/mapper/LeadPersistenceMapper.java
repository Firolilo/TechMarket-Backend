package com.techmarket.techmarket.leads.infrastructure.persistence.mapper;

import com.techmarket.techmarket.leads.domain.model.Lead;
import com.techmarket.techmarket.leads.infrastructure.persistence.jpa.entity.LeadJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class LeadPersistenceMapper {

    public LeadJpaEntity toJpa(Lead lead) {
        LeadJpaEntity entity = new LeadJpaEntity();
        entity.setId(lead.id());
        entity.setTenantId(lead.tenantId());
        entity.setBranchId(lead.branchId());
        entity.setUserId(lead.userId());
        entity.setListingId(lead.listingId());
        entity.setSourceChannel(lead.sourceChannel());
        entity.setStatus(lead.status());
        entity.setLeadScore(lead.leadScore());
        entity.setNotes(lead.notes());
        entity.setCreatedAt(lead.createdAt());
        entity.setUpdatedAt(lead.updatedAt());
        return entity;
    }

    public Lead toDomain(LeadJpaEntity entity) {
        return new Lead(
                entity.getId(),
                entity.getTenantId(),
                entity.getBranchId(),
                entity.getUserId(),
                entity.getListingId(),
                entity.getSourceChannel(),
                entity.getStatus(),
                entity.getLeadScore(),
                entity.getNotes(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
