package com.techmarket.iamservice.infrastructure.persistence.entity;

import com.techmarket.core.iam.infrastructure.persistence.entity.UserJpaEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "iam_user_scope")
public class UserScopeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserJpaEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id")
    private BranchEntity branch;

    @Column(name = "scope_type", nullable = false, length = 20)
    private String scopeType;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected UserScopeEntity() {}

    public UserScopeEntity(
            String tenantId, UserJpaEntity user, BranchEntity branch, String scopeType) {
        this.tenantId = tenantId;
        this.user = user;
        this.branch = branch;
        this.scopeType = scopeType;
    }

    public Long getId() {
        return id;
    }

    public String getTenantId() {
        return tenantId;
    }

    public UserJpaEntity getUser() {
        return user;
    }

    public BranchEntity getBranch() {
        return branch;
    }

    public String getScopeType() {
        return scopeType;
    }

    @jakarta.persistence.PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
