package com.techmarket.iamservice.application.service;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class AccessTokenRevocationService {

    private static final Logger log = LoggerFactory.getLogger(AccessTokenRevocationService.class);
    private static final String REDIS_KEY_PREFIX = "iam:blacklist:access:";

    private final StringRedisTemplate redisTemplate;
    private final Map<String, Instant> localRevokedTokens = new ConcurrentHashMap<>();

    public AccessTokenRevocationService(ObjectProvider<StringRedisTemplate> redisTemplateProvider) {
        this.redisTemplate = redisTemplateProvider.getIfAvailable();
    }

    public void revoke(String tokenId, Instant expiresAt) {
        if (tokenId == null || tokenId.isBlank() || expiresAt == null) {
            return;
        }

        Instant now = Instant.now();
        if (!expiresAt.isAfter(now)) {
            localRevokedTokens.remove(tokenId);
            return;
        }

        long ttlSeconds = Duration.between(now, expiresAt).getSeconds();
        if (ttlSeconds <= 0) {
            localRevokedTokens.remove(tokenId);
            return;
        }

        if (redisTemplate != null) {
            try {
                redisTemplate
                        .opsForValue()
                        .set(REDIS_KEY_PREFIX + tokenId, "revoked", Duration.ofSeconds(ttlSeconds));
            } catch (Exception ex) {
                log.warn("event=IAM_REDIS_REVOKE_FALLBACK tokenId={}", tokenId, ex);
                localRevokedTokens.put(tokenId, expiresAt);
            }
        } else {
            localRevokedTokens.put(tokenId, expiresAt);
        }
    }

    public boolean isRevoked(String tokenId) {
        if (tokenId == null || tokenId.isBlank()) {
            return false;
        }

        cleanupExpiredLocalEntries();

        Instant localExpiry = localRevokedTokens.get(tokenId);
        if (localExpiry != null && localExpiry.isAfter(Instant.now())) {
            return true;
        }

        if (redisTemplate == null) {
            return false;
        }

        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(REDIS_KEY_PREFIX + tokenId));
        } catch (Exception ex) {
            log.warn("event=IAM_REDIS_LOOKUP_FALLBACK tokenId={}", tokenId, ex);
            return localExpiry != null && localExpiry.isAfter(Instant.now());
        }
    }

    private void cleanupExpiredLocalEntries() {
        Instant now = Instant.now();
        localRevokedTokens.entrySet().removeIf(entry -> !entry.getValue().isAfter(now));
    }
}
