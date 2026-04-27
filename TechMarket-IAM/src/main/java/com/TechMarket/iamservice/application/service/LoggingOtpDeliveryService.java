package com.techmarket.iamservice.application.service;

import com.techmarket.core.iam.infrastructure.persistence.entity.UserJpaEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class LoggingOtpDeliveryService implements OtpDeliveryService {

    private static final Logger log = LoggerFactory.getLogger(LoggingOtpDeliveryService.class);

    @Override
    public void deliver(String tenantId, UserJpaEntity user, String otpCode, String challengeId) {
        log.info(
                "event=OTP_CODE_DISPATCHED tenantId={} userId={} username={} email={} challengeId={} otpCode={}",
                tenantId,
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                challengeId,
                otpCode);
    }
}
