package com.techmarket.iamservice.application.service;

import com.techmarket.core.iam.infrastructure.persistence.entity.UserJpaEntity;

public interface OtpDeliveryService {

    void deliver(String tenantId, UserJpaEntity user, String otpCode, String challengeId);
}
