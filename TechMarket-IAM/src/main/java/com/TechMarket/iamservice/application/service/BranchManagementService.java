package com.techmarket.iamservice.application.service;

import com.techmarket.iamservice.application.dto.BranchResponse;
import com.techmarket.iamservice.application.dto.CreateBranchRequest;
import com.techmarket.iamservice.infrastructure.persistence.entity.BranchEntity;
import com.techmarket.iamservice.infrastructure.persistence.repository.BranchRepository;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BranchManagementService {

    private final BranchRepository branchRepository;
    private final PrincipalAccessService principalAccessService;
    private final AuditTrailService auditTrailService;

    public BranchManagementService(
            BranchRepository branchRepository,
            PrincipalAccessService principalAccessService,
            AuditTrailService auditTrailService) {
        this.branchRepository = branchRepository;
        this.principalAccessService = principalAccessService;
        this.auditTrailService = auditTrailService;
    }

    @Transactional(readOnly = true)
    public List<BranchResponse> findAllByTenant(String tenantId) {
        return branchRepository.findAllByTenantId(tenantId).stream().map(this::toResponse).toList();
    }

    @Transactional
    public BranchResponse create(
            String tenantId, CreateBranchRequest request, Authentication authentication) {
        String code = request.code().trim();
        if (branchRepository.existsByTenantIdAndCode(tenantId, code)) {
            throw new IllegalArgumentException("branch code already exists for tenant");
        }

        BranchEntity branch =
                new BranchEntity(
                        tenantId,
                        code,
                        request.name().trim(),
                        request.active() == null || request.active());
        BranchEntity saved = branchRepository.save(branch);
        Long actorUserId = principalAccessService.currentUserId(authentication);
        auditTrailService.record(
                "BRANCH_CREATED",
                "Branch",
                saved.getId().toString(),
                tenantId,
                actorUserId == null ? "system" : actorUserId.toString());
        return toResponse(saved);
    }

    private BranchResponse toResponse(BranchEntity branch) {
        return new BranchResponse(
                branch.getId(),
                branch.getTenantId(),
                branch.getCode(),
                branch.getName(),
                branch.isActive());
    }
}
