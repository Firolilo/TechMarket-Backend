package com.techmarket.iamservice.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techmarket.iamservice.api.exception.ErrorCodes;
import com.techmarket.iamservice.api.exception.dto.ApiErrorResponse;
import com.techmarket.iamservice.config.ratelimit.InMemoryRateLimitingFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * Security configuration for IAM Service.
 *
 * <p>Provides two security filter chains: 1. JWT-enabled mode (security.jwt.enabled=true): Requires
 * JWT authentication for /api/** 2. JWT-disabled mode (security.jwt.enabled=false, default): No
 * authentication required (DEV mode)
 *
 * <p>Both modes allow public access to: - /actuator/health/**, /actuator/info/** - /v3/api-docs/**,
 * /swagger-ui/**, /swagger-ui.html
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

    public SecurityConfig(JwtProperties jwtProperties) {
        if (jwtProperties.enabled()) {
            log.info("event=SECURITY_CONFIG_JWT_ENABLED enabled=true");
        } else {
            log.info("event=SECURITY_CONFIG_JWT_DISABLED enabled=false");
        }
    }

    /**
     * Security filter chain for JWT-enabled mode. Requires JWT authentication for /api/**
     * endpoints.
     */
    @Bean
    @ConditionalOnProperty(name = "security.jwt.enabled", havingValue = "true")
    public SecurityFilterChain jwtSecurityFilterChain(
            HttpSecurity http, InMemoryRateLimitingFilter rateLimitingFilter) throws Exception {
        log.info("event=SECURITY_CONFIG_JWT_ENABLED");

        http
                // Disable CSRF for stateless REST API
                // REST APIs are stateless and use JWT tokens, so CSRF protection is not needed
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())

                // Stateless session management (JWT tokens, no server-side sessions)
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(
                        auth ->
                                auth
                                        // Public endpoints: Actuator health/info
                                        .requestMatchers(
                                                new AntPathRequestMatcher("/actuator/health/**"),
                                                new AntPathRequestMatcher("/actuator/info/**"))
                                        .permitAll()

                                        // Protected endpoint: Prometheus requires JWT +
                                        // SCOPE_operations.read
                                        .requestMatchers(
                                                new AntPathRequestMatcher("/actuator/prometheus"))
                                        .hasAuthority("SCOPE_operations.read")

                                        // Public endpoints: OpenAPI/Swagger documentation
                                        .requestMatchers(
                                                new AntPathRequestMatcher("/v3/api-docs/**"),
                                                new AntPathRequestMatcher("/swagger-ui/**"),
                                                new AntPathRequestMatcher("/swagger-ui.html"))
                                        .permitAll()
                                        .requestMatchers(
                                                new AntPathRequestMatcher("/auth/login"),
                                                new AntPathRequestMatcher("/auth/refresh"))
                                        .permitAll()
                                        .requestMatchers(new AntPathRequestMatcher("/auth/logout"))
                                        .authenticated()

                                        // Public endpoint: Error handling
                                        .requestMatchers(new AntPathRequestMatcher("/error"))
                                        .permitAll()

                                        // Protected endpoints
                                        .requestMatchers(
                                                new AntPathRequestMatcher(
                                                        "/users/**", HttpMethod.GET.name()),
                                                new AntPathRequestMatcher(
                                                        "/roles/**", HttpMethod.GET.name()),
                                                new AntPathRequestMatcher(
                                                        "/permissions/**", HttpMethod.GET.name()),
                                                new AntPathRequestMatcher(
                                                        "/branches/**", HttpMethod.GET.name()))
                                        .hasAnyAuthority(
                                                "ROLE_ADMIN_GLOBAL",
                                                "SCOPE_iam.users.read",
                                                "SCOPE_iam.roles.read",
                                                "SCOPE_iam.permissions.read",
                                                "SCOPE_iam.branches.read")
                                        .requestMatchers(
                                                new AntPathRequestMatcher(
                                                        "/api/v1/iam/**", HttpMethod.POST.name()),
                                                new AntPathRequestMatcher(
                                                        "/api/v1/iam/**", HttpMethod.PUT.name()),
                                                new AntPathRequestMatcher(
                                                        "/api/v1/iam/**", HttpMethod.PATCH.name()),
                                                new AntPathRequestMatcher(
                                                        "/api/v1/iam/**", HttpMethod.DELETE.name()))
                                        .hasAnyAuthority(
                                                "ROLE_ADMIN_GLOBAL", "SCOPE_iam.permissions.write")
                                        .requestMatchers(
                                                new AntPathRequestMatcher(
                                                        "/users/**", HttpMethod.POST.name()),
                                                new AntPathRequestMatcher(
                                                        "/users/**", HttpMethod.PUT.name()),
                                                new AntPathRequestMatcher(
                                                        "/users/**", HttpMethod.PATCH.name()),
                                                new AntPathRequestMatcher(
                                                        "/users/**", HttpMethod.DELETE.name()),
                                                new AntPathRequestMatcher(
                                                        "/roles/**", HttpMethod.POST.name()),
                                                new AntPathRequestMatcher(
                                                        "/roles/**", HttpMethod.PUT.name()),
                                                new AntPathRequestMatcher(
                                                        "/roles/**", HttpMethod.PATCH.name()),
                                                new AntPathRequestMatcher(
                                                        "/roles/**", HttpMethod.DELETE.name()),
                                                new AntPathRequestMatcher(
                                                        "/permissions/**", HttpMethod.POST.name()),
                                                new AntPathRequestMatcher(
                                                        "/permissions/**", HttpMethod.PUT.name()),
                                                new AntPathRequestMatcher(
                                                        "/permissions/**", HttpMethod.PATCH.name()),
                                                new AntPathRequestMatcher(
                                                        "/permissions/**",
                                                        HttpMethod.DELETE.name()),
                                                new AntPathRequestMatcher(
                                                        "/branches/**", HttpMethod.POST.name()),
                                                new AntPathRequestMatcher(
                                                        "/branches/**", HttpMethod.PUT.name()),
                                                new AntPathRequestMatcher(
                                                        "/branches/**", HttpMethod.PATCH.name()),
                                                new AntPathRequestMatcher(
                                                        "/branches/**", HttpMethod.DELETE.name()))
                                        .hasAnyAuthority(
                                                "ROLE_ADMIN_GLOBAL",
                                                "SCOPE_iam.users.write",
                                                "SCOPE_iam.roles.write",
                                                "SCOPE_iam.permissions.write",
                                                "SCOPE_iam.branches.write")

                                        // Backward compatibility for legacy protected endpoints
                                        .requestMatchers(new AntPathRequestMatcher("/api/**"))
                                        .authenticated()

                                        // Deny all other requests
                                        .anyRequest()
                                        .denyAll())

                // Configure OAuth2 Resource Server with JWT
                // JWT decoder/encoder are configured in TokenSecurityConfig
                .oauth2ResourceServer(
                        oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtConverter())))
                .addFilterBefore(rateLimitingFilter, UsernamePasswordAuthenticationFilter.class)

                // Configure exception handling to return consistent ApiErrorResponse
                .exceptionHandling(
                        exceptions ->
                                exceptions
                                        .authenticationEntryPoint(authenticationEntryPoint())
                                        .accessDeniedHandler(accessDeniedHandler()));

        return http.build();
    }

    /**
     * Security filter chain for JWT-disabled mode (DEV mode). Allows all requests without
     * authentication.
     */
    @Bean
    @ConditionalOnProperty(
            name = "security.jwt.enabled",
            havingValue = "false",
            matchIfMissing = true)
    public SecurityFilterChain devSecurityFilterChain(
            HttpSecurity http, InMemoryRateLimitingFilter rateLimitingFilter) throws Exception {
        log.info("event=SECURITY_CONFIG_JWT_DISABLED mode=DEV");

        http
                // Disable CSRF for stateless REST API
                // In DEV mode, we still disable CSRF for consistency with JWT mode
                .csrf(AbstractHttpConfigurer::disable)

                // Stateless session management
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(rateLimitingFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(
                        auth ->
                                auth
                                        // Allow all requests in DEV mode
                                        .anyRequest()
                                        .permitAll());

        return http.build();
    }

    /**
     * AuthenticationEntryPoint for 401 Unauthorized responses. Returns ApiErrorResponse with
     * errorCode="UNAUTHORIZED".
     */
    private AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) -> {
            log.warn(
                    "event=SECURITY_AUTHENTICATION_FAILED path={}",
                    request.getRequestURI(),
                    authException);
            writeErrorResponse(
                    response,
                    request,
                    ErrorCodes.UNAUTHORIZED,
                    HttpServletResponse.SC_UNAUTHORIZED);
        };
    }

    /**
     * AccessDeniedHandler for 403 Forbidden responses. Returns ApiErrorResponse with
     * errorCode="FORBIDDEN".
     */
    private AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            log.warn(
                    "event=SECURITY_ACCESS_DENIED path={}",
                    request.getRequestURI(),
                    accessDeniedException);
            writeErrorResponse(
                    response, request, ErrorCodes.FORBIDDEN, HttpServletResponse.SC_FORBIDDEN);
        };
    }

    private Converter<Jwt, ? extends AbstractAuthenticationToken> jwtConverter() {
        JwtGrantedAuthoritiesConverter scopesConverter = new JwtGrantedAuthoritiesConverter();

        JwtGrantedAuthoritiesConverter rolesConverter = new JwtGrantedAuthoritiesConverter();
        rolesConverter.setAuthorityPrefix("ROLE_");
        rolesConverter.setAuthoritiesClaimName("roles");

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(
                jwt -> {
                    Collection<GrantedAuthority> authorities = new ArrayList<>();
                    authorities.addAll(scopesConverter.convert(jwt));
                    authorities.addAll(rolesConverter.convert(jwt));
                    return authorities;
                });

        converter.setPrincipalClaimName("username");

        return jwt -> {
            AbstractAuthenticationToken token = converter.convert(jwt);
            if (token == null) {
                return new JwtAuthenticationToken(
                        jwt, scopesConverter.convert(jwt), jwt.getSubject());
            }
            return token;
        };
    }

    /** Writes ApiErrorResponse as JSON to the HTTP response. */
    private void writeErrorResponse(
            HttpServletResponse response,
            HttpServletRequest request,
            String errorCode,
            int httpStatus)
            throws IOException {
        ApiErrorResponse errorResponse =
                new ApiErrorResponse(errorCode, Instant.now(), request.getRequestURI());

        response.setStatus(httpStatus);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(response.getWriter(), errorResponse);
    }
}
