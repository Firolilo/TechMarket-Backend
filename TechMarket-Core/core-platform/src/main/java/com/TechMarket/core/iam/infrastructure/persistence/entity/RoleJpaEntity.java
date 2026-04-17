package com.techmarket.core.iam.infrastructure.persistence.entity;

import com.techmarket.core.shared.base.BaseEntity;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * JPA entity for Role persistence.
 *
 * <p>This class handles all JPA/database concerns, keeping the domain model pure.
 */
@Entity
@Table(name = "iam_role")
public class RoleJpaEntity extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column private String description;

    @Column(name = "hierarchy_level", nullable = false)
    private Integer hierarchyLevel = 100;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_role_id")
    private RoleJpaEntity parentRole;

    @ManyToMany(mappedBy = "roles")
    private Set<UserJpaEntity> users = new HashSet<>();

    @OneToMany(mappedBy = "role")
    private Set<PermissionJpaEntity> permissions = new HashSet<>();

    protected RoleJpaEntity() {
        // JPA required constructor
    }

    public RoleJpaEntity(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<UserJpaEntity> getUsers() {
        return users;
    }

    public Integer getHierarchyLevel() {
        return hierarchyLevel;
    }

    public void setHierarchyLevel(Integer hierarchyLevel) {
        this.hierarchyLevel = hierarchyLevel;
    }

    public RoleJpaEntity getParentRole() {
        return parentRole;
    }

    public void setParentRole(RoleJpaEntity parentRole) {
        this.parentRole = parentRole;
    }

    public Set<PermissionJpaEntity> getPermissions() {
        return permissions;
    }

    public void assignPermissions(Set<PermissionJpaEntity> permissions) {
        this.permissions.clear();
        if (permissions != null) {
            this.permissions.addAll(permissions);
        }
    }
}
