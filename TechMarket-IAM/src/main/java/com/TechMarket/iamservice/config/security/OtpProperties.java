package com.techmarket.iamservice.config.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security.otp")
public record OtpProperties(
        boolean enabledByDefault, int codeLength, long expiresMinutes, int maxAttempts) {}
