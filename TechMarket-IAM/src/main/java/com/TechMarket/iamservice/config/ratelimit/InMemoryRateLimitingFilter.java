package com.techmarket.iamservice.config.ratelimit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techmarket.iamservice.api.exception.ErrorCodes;
import com.techmarket.iamservice.api.exception.dto.ApiErrorResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class InMemoryRateLimitingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(InMemoryRateLimitingFilter.class);
    private static final String X_FORWARDED_FOR = "X-Forwarded-For";
    private static final String HEADER_LIMIT = "X-RateLimit-Limit";
    private static final String HEADER_REMAINING = "X-RateLimit-Remaining";
    private static final String HEADER_RETRY_AFTER = "Retry-After";

    private final RateLimitProperties properties;
    private final ObjectMapper objectMapper;
    private final Map<String, FixedWindowCounter> counters = new ConcurrentHashMap<>();

    public InMemoryRateLimitingFilter(RateLimitProperties properties, ObjectMapper objectMapper) {
        this.properties = properties;
        this.objectMapper = objectMapper;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if (!properties.enabled()) {
            return true;
        }

        String path = request.getRequestURI();
        return properties.paths().stream().noneMatch(path::equals);
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String clientId = resolveClientIdentifier(request);
        String key = clientId + ":" + request.getRequestURI();
        long now = System.currentTimeMillis();

        FixedWindowCounter counter =
                counters.computeIfAbsent(key, ignored -> new FixedWindowCounter(now));
        RateLimitDecision decision =
                counter.tryAcquire(now, properties.maxRequests(), properties.windowSeconds());

        response.setHeader(HEADER_LIMIT, String.valueOf(properties.maxRequests()));
        response.setHeader(HEADER_REMAINING, String.valueOf(decision.remainingRequests()));

        if (!decision.allowed()) {
            response.setHeader(HEADER_RETRY_AFTER, String.valueOf(decision.retryAfterSeconds()));
            log.warn(
                    "event=IAM_RATE_LIMIT_EXCEEDED clientId={} path={} retryAfterSeconds={}",
                    clientId,
                    request.getRequestURI(),
                    decision.retryAfterSeconds());
            writeErrorResponse(response, request);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String resolveClientIdentifier(HttpServletRequest request) {
        String forwardedFor = request.getHeader(X_FORWARDED_FOR);
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }

        return request.getRemoteAddr() != null ? request.getRemoteAddr() : "unknown";
    }

    private void writeErrorResponse(HttpServletResponse response, HttpServletRequest request)
            throws IOException {
        ApiErrorResponse errorResponse =
                new ApiErrorResponse(
                        ErrorCodes.RATE_LIMIT_EXCEEDED, Instant.now(), request.getRequestURI());

        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), errorResponse);
    }

    private static final class FixedWindowCounter {

        private long windowStartedAtMillis;
        private int requests;

        private FixedWindowCounter(long now) {
            this.windowStartedAtMillis = now;
            this.requests = 0;
        }

        private synchronized RateLimitDecision tryAcquire(
                long now, int maxRequests, long windowSeconds) {
            long windowMillis = Duration.ofSeconds(windowSeconds).toMillis();
            if (now - windowStartedAtMillis >= windowMillis) {
                windowStartedAtMillis = now;
                requests = 0;
            }

            if (requests >= maxRequests) {
                long retryAfterMillis = Math.max(0, windowMillis - (now - windowStartedAtMillis));
                long retryAfterSeconds =
                        Math.max(1, Duration.ofMillis(retryAfterMillis).toSeconds());
                return new RateLimitDecision(false, 0, retryAfterSeconds);
            }

            requests++;
            return new RateLimitDecision(true, Math.max(0, maxRequests - requests), 0);
        }
    }

    private record RateLimitDecision(
            boolean allowed, int remainingRequests, long retryAfterSeconds) {}
}
