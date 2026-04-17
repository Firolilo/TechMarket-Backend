package com.techmarket.iamservice.application.service;

import com.techmarket.core.iam.infrastructure.persistence.entity.RoleJpaEntity;
import com.techmarket.core.iam.infrastructure.persistence.entity.UserJpaEntity;
import com.techmarket.core.shared.exceptions.EntityNotFoundException;
import com.techmarket.iamservice.application.dto.CreateUserRequest;
import com.techmarket.iamservice.application.dto.UpdateUserRequest;
import com.techmarket.iamservice.application.dto.UserResponse;
import com.techmarket.iamservice.application.dto.UserScopeResponse;
import com.techmarket.iamservice.application.model.IamConstants;
import com.techmarket.iamservice.application.model.UserScopeType;
import com.techmarket.iamservice.infrastructure.persistence.entity.BranchEntity;
import com.techmarket.iamservice.infrastructure.persistence.entity.UserCredentialEntity;
import com.techmarket.iamservice.infrastructure.persistence.entity.UserScopeEntity;
import com.techmarket.iamservice.infrastructure.persistence.repository.BranchRepository;
import com.techmarket.iamservice.infrastructure.persistence.repository.RoleHierarchyRepository;
import com.techmarket.iamservice.infrastructure.persistence.repository.TenantRoleRepository;
import com.techmarket.iamservice.infrastructure.persistence.repository.TenantUserRepository;
import com.techmarket.iamservice.infrastructure.persistence.repository.UserCredentialRepository;
import com.techmarket.iamservice.infrastructure.persistence.repository.UserScopeRepository;
import com.techmarket.iamservice.infrastructure.persistence.repository.projection.RoleHierarchyView;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserManagementService {

    private final TenantUserRepository tenantUserRepository;
    private final TenantRoleRepository tenantRoleRepository;
    private final UserCredentialRepository userCredentialRepository;
    private final UserScopeRepository userScopeRepository;
    private final BranchRepository branchRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleHierarchyRepository roleHierarchyRepository;
    private final PrincipalAccessService principalAccessService;
    private final AuditTrailService auditTrailService;

    public UserManagementService(
            TenantUserRepository tenantUserRepository,
            TenantRoleRepository tenantRoleRepository,
            UserCredentialRepository userCredentialRepository,
            UserScopeRepository userScopeRepository,
            BranchRepository branchRepository,
            PasswordEncoder passwordEncoder,
            RoleHierarchyRepository roleHierarchyRepository,
            PrincipalAccessService principalAccessService,
            AuditTrailService auditTrailService) {
        this.tenantUserRepository = tenantUserRepository;
        this.tenantRoleRepository = tenantRoleRepository;
        this.userCredentialRepository = userCredentialRepository;
        this.userScopeRepository = userScopeRepository;
        this.branchRepository = branchRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleHierarchyRepository = roleHierarchyRepository;
        this.principalAccessService = principalAccessService;
        this.auditTrailService = auditTrailService;
    }

    @Transactional(readOnly = true)
    public List<UserResponse> findAll(Authentication authentication) {
        List<UserJpaEntity> users = tenantUserRepository.findAll();
        Map<Long, List<UserScopeEntity>> scopesByUserId = loadScopesByUser(users);

        if (!principalAccessService.isGlobalAdmin(authentication)
                && principalAccessService.currentUserId(authentication) != null) {
            users = filterUsersByScope(users, scopesByUserId, authentication);
        }

        return users.stream()
                .map(user -> toResponse(user, scopesByUserId.get(user.getId())))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<UserResponse> findAllByTenant(String tenantId, Authentication authentication) {
        List<UserJpaEntity> users = tenantUserRepository.findAllByTenantId(tenantId);
        Map<Long, List<UserScopeEntity>> scopesByUserId = loadScopesByUser(users);

        if (!principalAccessService.isGlobalAdmin(authentication)
                && principalAccessService.currentUserId(authentication) != null) {
            users = filterUsersByScope(users, scopesByUserId, authentication);
        }

        return users.stream()
                .map(user -> toResponse(user, scopesByUserId.get(user.getId())))
                .toList();
    }

    @Transactional(readOnly = true)
    public UserResponse findById(Long userId, Authentication authentication) {
        UserJpaEntity user =
                tenantUserRepository
                        .findById(userId)
                        .orElseThrow(() -> new EntityNotFoundException("User", userId.toString()));
        validateVisibility(user, authentication);
        List<UserScopeEntity> scopes = userScopeRepository.findAllByUserId(user.getId());
        return toResponse(user, scopes);
    }

    @Transactional(readOnly = true)
    public UserResponse findById(String tenantId, Long userId, Authentication authentication) {
        UserJpaEntity user =
                tenantUserRepository
                        .findByIdAndTenantId(userId, tenantId)
                        .orElseThrow(() -> new EntityNotFoundException("User", userId.toString()));
        validateVisibility(user, authentication);
        List<UserScopeEntity> scopes =
                userScopeRepository.findAllByUserIdAndTenantId(user.getId(), tenantId);
        return toResponse(user, scopes);
    }

    @Transactional
    public UserResponse create(
            String tenantId, CreateUserRequest request, Authentication authentication) {
        requireAuthenticatedActor(authentication);
        String username = request.username().trim();
        String email = request.email().trim();

        if (tenantUserRepository.existsByTenantIdAndUsername(tenantId, username)) {
            throw new IllegalArgumentException("username already exists for tenant");
        }
        if (tenantUserRepository.existsByTenantIdAndEmail(tenantId, email)) {
            throw new IllegalArgumentException("email already exists for tenant");
        }

        Set<RoleJpaEntity> roles = resolveAssignableRoles(tenantId, request.roleIds());
        validateRoleHierarchy(tenantId, roles, authentication);

        UserJpaEntity user =
                new UserJpaEntity(username, email, request.active() == null || request.active());
        user.setTenantId(tenantId);
        user.assignRoles(roles);
        UserJpaEntity savedUser = tenantUserRepository.save(user);

        userCredentialRepository.save(
                new UserCredentialEntity(
                        savedUser.getId(), tenantId, passwordEncoder.encode(request.password())));

        assignScopes(
                savedUser,
                tenantId,
                request.scopeType(),
                request.branchIds(),
                authentication,
                true);

        List<UserScopeEntity> scopes =
                userScopeRepository.findAllByUserIdAndTenantId(savedUser.getId(), tenantId);
        auditTrailService.record(
                "USER_CREATED",
                "User",
                savedUser.getId().toString(),
                tenantId,
                actorIdentifier(authentication));
        return toResponse(savedUser, scopes);
    }

    @Transactional
    public UserResponse update(
            String tenantId,
            Long userId,
            UpdateUserRequest request,
            Authentication authentication) {
        UserJpaEntity user =
                tenantUserRepository
                        .findByIdAndTenantId(userId, tenantId)
                        .orElseThrow(() -> new EntityNotFoundException("User", userId.toString()));
        return updateInternal(user, request, authentication);
    }

    @Transactional
    public UserResponse update(
            Long userId, UpdateUserRequest request, Authentication authentication) {
        UserJpaEntity user =
                tenantUserRepository
                        .findById(userId)
                        .orElseThrow(() -> new EntityNotFoundException("User", userId.toString()));
        return updateInternal(user, request, authentication);
    }

    private UserResponse updateInternal(
            UserJpaEntity user, UpdateUserRequest request, Authentication authentication) {
        requireAuthenticatedActor(authentication);
        validateVisibility(user, authentication);
        String tenantId = user.getTenantId();

        if (request.email() != null && !request.email().isBlank()) {
            user.setEmail(request.email().trim());
        }

        if (request.active() != null) {
            user.setActive(request.active());
        }

        if (request.roleIds() != null) {
            Set<RoleJpaEntity> roles = resolveAssignableRoles(tenantId, request.roleIds());
            validateRoleHierarchy(tenantId, roles, authentication);
            user.assignRoles(roles);
        }

        if (request.scopeType() != null || request.branchIds() != null) {
            List<UserScopeEntity> currentScopes =
                    userScopeRepository.findAllByUserIdAndTenantId(user.getId(), tenantId);
            String currentScopeType =
                    currentScopes.stream()
                                    .anyMatch(
                                            scope ->
                                                    UserScopeType.GLOBAL
                                                            .name()
                                                            .equalsIgnoreCase(scope.getScopeType()))
                            ? UserScopeType.GLOBAL.name()
                            : UserScopeType.BRANCH.name();
            Set<Long> currentBranchIds =
                    currentScopes.stream()
                            .map(UserScopeEntity::getBranch)
                            .filter(branch -> branch != null)
                            .map(BranchEntity::getId)
                            .collect(Collectors.toSet());

            String scopeType = request.scopeType() != null ? request.scopeType() : currentScopeType;
            Set<Long> branchIds =
                    request.branchIds() != null ? request.branchIds() : currentBranchIds;
            assignScopes(user, tenantId, scopeType, branchIds, authentication, false);
        }

        UserJpaEntity updated = tenantUserRepository.save(user);
        List<UserScopeEntity> scopes =
                userScopeRepository.findAllByUserIdAndTenantId(user.getId(), tenantId);
        auditTrailService.record(
                "USER_UPDATED",
                "User",
                updated.getId().toString(),
                tenantId,
                actorIdentifier(authentication));
        return toResponse(updated, scopes);
    }

    private Map<Long, List<UserScopeEntity>> loadScopesByUser(List<UserJpaEntity> users) {
        Map<Long, List<UserScopeEntity>> scopesByUserId = new HashMap<>();
        if (users.isEmpty()) {
            return scopesByUserId;
        }

        List<Long> ids = users.stream().map(UserJpaEntity::getId).toList();
        for (UserScopeEntity scope : userScopeRepository.findAllByUserIdIn(ids)) {
            scopesByUserId
                    .computeIfAbsent(scope.getUser().getId(), ignored -> new ArrayList<>())
                    .add(scope);
        }
        return scopesByUserId;
    }

    private List<UserJpaEntity> filterUsersByScope(
            List<UserJpaEntity> users,
            Map<Long, List<UserScopeEntity>> scopesByUserId,
            Authentication authentication) {
        Long actorUserId = principalAccessService.currentUserId(authentication);
        if (actorUserId == null) {
            return users;
        }

        UserJpaEntity actor =
                users.stream()
                        .filter(user -> actorUserId.equals(user.getId()))
                        .findFirst()
                        .orElse(null);
        if (actor == null) {
            return users;
        }

        ScopeVisibility visibility =
                loadScopeVisibility(
                        actorUserId, actor.getTenantId(), scopesByUserId.get(actorUserId));
        if (visibility.globalScope()) {
            return users;
        }

        Set<Long> actorBranchIds = visibility.branchIds();
        return users.stream()
                .filter(
                        user -> {
                            if (actorUserId.equals(user.getId())) {
                                return true;
                            }
                            if (!actor.getTenantId().equals(user.getTenantId())) {
                                return false;
                            }

                            List<UserScopeEntity> targetScopes = scopesByUserId.get(user.getId());
                            if (targetScopes == null || targetScopes.isEmpty()) {
                                return false;
                            }

                            boolean targetGlobal =
                                    targetScopes.stream()
                                            .anyMatch(
                                                    scope ->
                                                            UserScopeType.GLOBAL
                                                                    .name()
                                                                    .equalsIgnoreCase(
                                                                            scope.getScopeType()));
                            if (targetGlobal) {
                                return false;
                            }

                            Set<Long> targetBranchIds =
                                    targetScopes.stream()
                                            .map(UserScopeEntity::getBranch)
                                            .filter(branch -> branch != null)
                                            .map(BranchEntity::getId)
                                            .collect(Collectors.toSet());
                            return !disjoint(actorBranchIds, targetBranchIds);
                        })
                .toList();
    }

    private void validateVisibility(UserJpaEntity user, Authentication authentication) {
        if (principalAccessService.isGlobalAdmin(authentication)) {
            return;
        }
        Long actorUserId = principalAccessService.currentUserId(authentication);
        if (actorUserId == null) {
            return;
        }
        if (actorUserId.equals(user.getId())) {
            return;
        }

        List<UserScopeEntity> actorScopes =
                userScopeRepository.findAllByUserIdAndTenantId(actorUserId, user.getTenantId());
        ScopeVisibility actorVisibility =
                loadScopeVisibility(actorUserId, user.getTenantId(), actorScopes);
        if (actorVisibility.globalScope()) {
            return;
        }

        List<UserScopeEntity> targetScopes =
                userScopeRepository.findAllByUserIdAndTenantId(user.getId(), user.getTenantId());
        boolean targetGlobal =
                targetScopes.stream()
                        .anyMatch(
                                scope ->
                                        UserScopeType.GLOBAL
                                                .name()
                                                .equalsIgnoreCase(scope.getScopeType()));
        if (targetGlobal) {
            throw new AccessDeniedException("Insufficient scope to access this user");
        }

        Set<Long> targetBranchIds =
                targetScopes.stream()
                        .map(UserScopeEntity::getBranch)
                        .filter(branch -> branch != null)
                        .map(BranchEntity::getId)
                        .collect(Collectors.toSet());

        if (disjoint(actorVisibility.branchIds(), targetBranchIds)) {
            throw new AccessDeniedException("Insufficient scope to access this user");
        }
    }

    private ScopeVisibility loadScopeVisibility(
            Long userId, String tenantId, Collection<UserScopeEntity> existingScopes) {
        List<UserScopeEntity> scopes;
        if (existingScopes == null) {
            scopes = userScopeRepository.findAllByUserIdAndTenantId(userId, tenantId);
        } else {
            scopes = new ArrayList<>(existingScopes);
        }

        boolean globalScope =
                scopes.stream()
                        .anyMatch(
                                scope ->
                                        UserScopeType.GLOBAL
                                                .name()
                                                .equalsIgnoreCase(scope.getScopeType()));
        Set<Long> branchIds =
                scopes.stream()
                        .map(UserScopeEntity::getBranch)
                        .filter(branch -> branch != null)
                        .map(BranchEntity::getId)
                        .collect(Collectors.toSet());
        return new ScopeVisibility(globalScope, branchIds);
    }

    private Set<RoleJpaEntity> resolveAssignableRoles(String tenantId, Set<Long> roleIds) {
        Set<Long> requestedRoleIds = roleIds == null ? Set.of() : roleIds;
        if (requestedRoleIds.isEmpty()) {
            return Set.of();
        }

        List<RoleJpaEntity> foundRoles = tenantRoleRepository.findAllById(requestedRoleIds);
        Set<RoleJpaEntity> validRoles = new HashSet<>();
        for (RoleJpaEntity role : foundRoles) {
            if (tenantId.equals(role.getTenantId())) {
                validRoles.add(role);
                continue;
            }
            if (isGlobalAdminRole(role)) {
                validRoles.add(role);
            }
        }

        if (validRoles.size() != requestedRoleIds.size()) {
            throw new IllegalArgumentException("One or more roles do not exist for tenant");
        }
        return validRoles;
    }

    private void validateRoleHierarchy(
            String tenantId, Set<RoleJpaEntity> targetRoles, Authentication authentication) {
        if (targetRoles == null || targetRoles.isEmpty()) {
            return;
        }
        if (principalAccessService.isGlobalAdmin(authentication)) {
            return;
        }

        Set<String> actorRoleNames = principalAccessService.currentRoleNames(authentication);
        if (actorRoleNames.isEmpty()) {
            return;
        }

        List<RoleHierarchyView> actorHierarchy =
                roleHierarchyRepository.findHierarchyByTenantAndRoleNames(
                        tenantId, IamConstants.GLOBAL_TENANT_ID, actorRoleNames);
        if (actorHierarchy.isEmpty()) {
            throw new AccessDeniedException("Current actor has no assignable roles");
        }

        int actorMinLevel =
                actorHierarchy.stream()
                        .map(RoleHierarchyView::getHierarchyLevel)
                        .filter(level -> level != null)
                        .min(Comparator.naturalOrder())
                        .orElse(IamConstants.DEFAULT_HIERARCHY_LEVEL);

        Set<Long> targetRoleIds =
                targetRoles.stream().map(RoleJpaEntity::getId).collect(Collectors.toSet());
        List<RoleHierarchyView> targetHierarchy =
                roleHierarchyRepository.findHierarchyByRoleIds(targetRoleIds);

        int targetMinLevel =
                targetHierarchy.stream()
                        .map(RoleHierarchyView::getHierarchyLevel)
                        .filter(level -> level != null)
                        .min(Comparator.naturalOrder())
                        .orElse(IamConstants.DEFAULT_HIERARCHY_LEVEL);

        boolean assigningGlobalAdmin =
                targetHierarchy.stream()
                        .anyMatch(
                                role ->
                                        role.getRoleName() != null
                                                && IamConstants.GLOBAL_ADMIN_ROLE.equalsIgnoreCase(
                                                        role.getRoleName()));
        if (assigningGlobalAdmin) {
            throw new AccessDeniedException("Only ADMIN_GLOBAL can assign ADMIN_GLOBAL role");
        }

        if (targetMinLevel < actorMinLevel) {
            throw new AccessDeniedException("Cannot assign roles above your hierarchy");
        }
    }

    private void assignScopes(
            UserJpaEntity user,
            String tenantId,
            String requestedScopeType,
            Set<Long> requestedBranchIds,
            Authentication authentication,
            boolean creatingUser) {
        UserScopeType scopeType = UserScopeType.from(requestedScopeType);
        Set<Long> branchIds = requestedBranchIds == null ? Set.of() : requestedBranchIds;

        if (scopeType == UserScopeType.GLOBAL && !branchIds.isEmpty()) {
            throw new IllegalArgumentException("branchIds must be empty for GLOBAL scope");
        }
        if (scopeType == UserScopeType.BRANCH && branchIds.isEmpty()) {
            throw new IllegalArgumentException("branchIds are required for BRANCH scope");
        }

        if (!principalAccessService.isGlobalAdmin(authentication)) {
            Long actorUserId = principalAccessService.currentUserId(authentication);
            if (actorUserId != null) {
                ScopeVisibility actorVisibility = loadScopeVisibility(actorUserId, tenantId, null);
                if (!actorVisibility.globalScope()) {
                    if (scopeType == UserScopeType.GLOBAL) {
                        throw new AccessDeniedException("Current actor cannot assign GLOBAL scope");
                    }
                    if (!actorVisibility.branchIds().containsAll(branchIds)) {
                        throw new AccessDeniedException(
                                "Current actor cannot assign branches outside its scope");
                    }
                }
            }
        }

        userScopeRepository.deleteByUserId(user.getId());

        if (scopeType == UserScopeType.GLOBAL) {
            userScopeRepository.save(
                    new UserScopeEntity(tenantId, user, null, UserScopeType.GLOBAL.name()));
            return;
        }

        Set<BranchEntity> branches = branchRepository.findByIdInAndTenantId(branchIds, tenantId);
        if (branches.size() != branchIds.size()) {
            throw new IllegalArgumentException("One or more branches do not exist for tenant");
        }

        for (BranchEntity branch : branches) {
            userScopeRepository.save(
                    new UserScopeEntity(tenantId, user, branch, UserScopeType.BRANCH.name()));
        }
    }

    private UserResponse toResponse(UserJpaEntity user, List<UserScopeEntity> scopes) {
        Set<Long> roleIds =
                user.getRoles().stream().map(RoleJpaEntity::getId).collect(Collectors.toSet());

        Set<UserScopeResponse> scopeResponses = new LinkedHashSet<>();
        if (scopes != null) {
            for (UserScopeEntity scope : scopes) {
                scopeResponses.add(
                        new UserScopeResponse(
                                scope.getScopeType(),
                                scope.getBranch() != null ? scope.getBranch().getId() : null,
                                scope.getBranch() != null ? scope.getBranch().getCode() : null));
            }
        }

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.isActive(),
                user.getTenantId(),
                roleIds,
                scopeResponses);
    }

    private boolean isGlobalAdminRole(RoleJpaEntity role) {
        return role != null
                && role.getName() != null
                && IamConstants.GLOBAL_ADMIN_ROLE.equalsIgnoreCase(role.getName());
    }

    private String actorIdentifier(Authentication authentication) {
        Long userId = principalAccessService.currentUserId(authentication);
        return userId == null ? "system" : userId.toString();
    }

    private boolean disjoint(Set<Long> left, Set<Long> right) {
        if (left == null || right == null || left.isEmpty() || right.isEmpty()) {
            return true;
        }
        for (Long value : left) {
            if (right.contains(value)) {
                return false;
            }
        }
        return true;
    }

    private void requireAuthenticatedActor(Authentication authentication) {
        if (principalAccessService.isGlobalAdmin(authentication)) {
            return;
        }

        if (principalAccessService.currentUserId(authentication) == null) {
            throw new AccessDeniedException(
                    "Authenticated actor is required to manage users in university mode");
        }
    }

    private record ScopeVisibility(boolean globalScope, Set<Long> branchIds) {}
}
