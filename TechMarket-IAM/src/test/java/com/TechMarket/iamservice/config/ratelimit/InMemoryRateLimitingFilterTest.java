package com.techmarket.iamservice.config.ratelimit;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.techmarket.iamservice.api.exception.ErrorCodes;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class InMemoryRateLimitingFilterTest {

    @Test
    void shouldReturnTooManyRequestsWhenLimitIsExceeded() throws ServletException, IOException {
        InMemoryRateLimitingFilter filter = buildFilter(1, 60, List.of("/auth/login"));

        MockHttpServletRequest firstRequest = new MockHttpServletRequest("POST", "/auth/login");
        firstRequest.setRemoteAddr("127.0.0.1");
        MockHttpServletResponse firstResponse = new MockHttpServletResponse();

        filter.doFilter(firstRequest, firstResponse, new MockFilterChain());

        MockHttpServletRequest secondRequest = new MockHttpServletRequest("POST", "/auth/login");
        secondRequest.setRemoteAddr("127.0.0.1");
        MockHttpServletResponse secondResponse = new MockHttpServletResponse();

        filter.doFilter(secondRequest, secondResponse, new MockFilterChain());

        assertThat(secondResponse.getStatus()).isEqualTo(429);
        assertThat(secondResponse.getHeader("Retry-After")).isNotBlank();
        assertThat(secondResponse.getContentAsString())
                .contains(ErrorCodes.RATE_LIMIT_EXCEEDED)
                .contains("/auth/login");
    }

    @Test
    void shouldSkipRateLimitingForNonConfiguredPaths() throws ServletException, IOException {
        InMemoryRateLimitingFilter filter = buildFilter(1, 60, List.of("/auth/login"));

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/users/1");
        request.setRemoteAddr("127.0.0.1");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        filter.doFilter(request, response, filterChain);

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getHeader("X-RateLimit-Limit")).isNull();
    }

    private InMemoryRateLimitingFilter buildFilter(
            int maxRequests, long windowSeconds, List<String> paths) {
        RateLimitProperties properties =
                new RateLimitProperties(true, maxRequests, windowSeconds, paths);
        ObjectMapper objectMapper = JsonMapper.builder().findAndAddModules().build();
        return new InMemoryRateLimitingFilter(properties, objectMapper);
    }
}
