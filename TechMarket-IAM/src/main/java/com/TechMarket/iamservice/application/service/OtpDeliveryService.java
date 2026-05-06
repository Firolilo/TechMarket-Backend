package com.techmarket.iamservice.application.service;

import com.techmarket.iamservice.infrastructure.persistence.entity.UserEntity;

public interface OtpDeliveryService {

    void deliver(String tenantId, UserEntity user, String otpCode, String challengeId);
}
