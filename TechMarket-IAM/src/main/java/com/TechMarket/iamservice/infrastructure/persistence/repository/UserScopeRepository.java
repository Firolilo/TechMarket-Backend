package com.techmarket.iamservice.infrastructure.persistence.repository;

import com.techmarket.iamservice.infrastructure.persistence.entity.UserScopeEntity;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserScopeRepository extends JpaRepository<UserScopeEntity, Long> {

    List<UserScopeEntity> findAllByUserId(Long userId);

    List<UserScopeEntity> findAllByUserIdAndTenantId(Long userId, String tenantId);

    List<UserScopeEntity> findAllByUserIdIn(Collection<Long> userIds);

    void deleteByUserId(Long userId);
}
