package com.techmarket.techmarket.security.filter;

import com.techmarket.techmarket.security.jwt.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = extractToken(request);
        if (token != null && jwtTokenProvider.isTokenValid(token)) {
            Claims claims = jwtTokenProvider.parseToken(token);
            List<SimpleGrantedAuthority> authorities = extractAuthorities(claims);
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            claims.getSubject(), null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }

    private List<SimpleGrantedAuthority> extractAuthorities(Claims claims) {
        String role = claims.get("role", String.class);
        if (role != null && !role.isBlank()) {
            return List.of(new SimpleGrantedAuthority(formatRole(role)));
        }

        Object roles = claims.get("roles");
        if (roles instanceof Collection<?> roleCollection && !roleCollection.isEmpty()) {
            return roleCollection.stream()
                    .map(String::valueOf)
                    .filter(value -> !value.isBlank())
                    .map(this::formatRole)
                    .map(SimpleGrantedAuthority::new)
                    .toList();
        }

        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    private String formatRole(String role) {
        String normalized = role.trim().toUpperCase(Locale.ROOT);
        return normalized.startsWith("ROLE_") ? normalized : "ROLE_" + normalized;
    }
}
