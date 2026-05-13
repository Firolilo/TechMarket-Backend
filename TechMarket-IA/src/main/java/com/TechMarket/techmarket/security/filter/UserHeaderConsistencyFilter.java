package com.techmarket.techmarket.security.filter;

import com.techmarket.techmarket.security.jwt.JwtTokenProvider;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.repository.UserSpringDataRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class UserHeaderConsistencyFilter extends OncePerRequestFilter {

    private static final String USER_ID_HEADER = "X-User-Id";

    private final JwtTokenProvider jwtTokenProvider;
    private final UserSpringDataRepository userRepository;

    public UserHeaderConsistencyFilter(
            JwtTokenProvider jwtTokenProvider, UserSpringDataRepository userRepository) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String headerUserId = request.getHeader(USER_ID_HEADER);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<UUID> authenticatedUserId =
                authentication == null
                        ? Optional.empty()
                        : authenticatedUserId(request, authentication);

        if (headerUserId != null
                && authentication != null
                && authentication.isAuthenticated()
                && authenticatedUserId.isPresent()
                && !authenticatedUserId.get().toString().equals(headerUserId.trim())) {
            response.sendError(
                    HttpStatus.FORBIDDEN.value(), "X-User-Id does not match JWT subject");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private Optional<UUID> authenticatedUserId(
            HttpServletRequest request, Authentication authentication) {
        if (authentication.getPrincipal() instanceof String subject && isUuid(subject)) {
            return Optional.of(UUID.fromString(subject));
        }

        String token = extractToken(request);
        if (token == null) {
            return Optional.empty();
        }

        Claims claims = jwtTokenProvider.parseToken(token);
        String email =
                firstPresent(
                        claims.get("email", String.class), claims.get("username", String.class));
        if (email == null) {
            return Optional.empty();
        }

        return userRepository.findByEmailIgnoreCase(email).map(user -> user.getId());
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }

    private String firstPresent(String first, String second) {
        if (first != null && !first.isBlank()) {
            return first.trim();
        }
        if (second != null && !second.isBlank()) {
            return second.trim();
        }
        return null;
    }

    private boolean isUuid(String value) {
        try {
            UUID.fromString(value);
            return true;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }
}
