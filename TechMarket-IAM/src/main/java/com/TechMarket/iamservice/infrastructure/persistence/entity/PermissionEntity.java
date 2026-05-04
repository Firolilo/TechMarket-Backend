package com.techmarket.iamservice.infrastructure.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "iam_permission")
public class PermissionEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "role_id", nullable = false)
    private RoleEntity role;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "module_id", nullable = false)
    private ModuleEntity module;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "resource_id", nullable = false)
    private ResourceEntity resource;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "action_id", nullable = false)
    private ActionEntity action;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "field_id")
    private FieldEntity field;

    protected PermissionEntity() {}

    public PermissionEntity(
            RoleEntity role,
            ModuleEntity module,
            ResourceEntity resource,
            ActionEntity action,
            FieldEntity field) {
        this.role = role;
        this.module = module;
        this.resource = resource;
        this.action = action;
        this.field = field;
    }

    public RoleEntity getRole() {
        return role;
    }

    public void setRole(RoleEntity role) {
        this.role = role;
    }

    public ModuleEntity getModule() {
        return module;
    }

    public ResourceEntity getResource() {
        return resource;
    }

    public ActionEntity getAction() {
        return action;
    }

    public FieldEntity getField() {
        return field;
    }
}
