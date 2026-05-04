package com.techmarket.iamservice.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "iam_role")
public class RoleEntity extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column private String description;

    @Column(name = "hierarchy_level", nullable = false)
    private Integer hierarchyLevel = 100;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_role_id")
    private RoleEntity parentRole;

    @ManyToMany(mappedBy = "roles")
    private Set<UserEntity> users = new HashSet<>();

    @OneToMany(mappedBy = "role")
    private Set<PermissionEntity> permissions = new HashSet<>();

    protected RoleEntity() {}

    public RoleEntity(String name, String description) {
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

    public Integer getHierarchyLevel() {
        return hierarchyLevel;
    }

    public void setHierarchyLevel(Integer hierarchyLevel) {
        this.hierarchyLevel = hierarchyLevel;
    }

    public RoleEntity getParentRole() {
        return parentRole;
    }

    public void setParentRole(RoleEntity parentRole) {
        this.parentRole = parentRole;
    }

    public Set<UserEntity> getUsers() {
        return users;
    }

    public Set<PermissionEntity> getPermissions() {
        return permissions;
    }

    public void assignPermissions(Set<PermissionEntity> permissions) {
        this.permissions.clear();
        if (permissions != null) {
            this.permissions.addAll(permissions);
        }
    }
}
