package com.techmarket.iamservice.config.ratelimit;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "iam.rate-limit")
public record RateLimitProperties(
        boolean enabled, int maxRequests, long windowSeconds, List<String> paths) {

    public RateLimitProperties {
        paths = paths == null ? List.of("/auth/login", "/auth/refresh") : List.copyOf(paths);
    }
}
