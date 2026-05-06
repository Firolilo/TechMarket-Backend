package com.techmarket.iamservice.application.service;

import com.techmarket.iamservice.infrastructure.persistence.entity.UserEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class LoggingOtpDeliveryService implements OtpDeliveryService {

    private static final Logger log = LoggerFactory.getLogger(LoggingOtpDeliveryService.class);

    @Override
    public void deliver(String tenantId, UserEntity user, String otpCode, String challengeId) {
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
