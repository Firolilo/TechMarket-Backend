package com.techmarket.ai.bootstrap.config;

import jakarta.servlet.DispatcherType;
import java.util.Arrays;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, Environment env)
            throws Exception {
        var profiles = Arrays.asList(env.getActiveProfiles());
        boolean devOrDocker = profiles.contains("dev") || profiles.contains("docker");
        boolean jwtEnabled = env.getProperty("security.jwt.enabled", Boolean.class, false);

        http.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(
                        a -> {
                            // Let the container render error responses instead of the security
                            // layer masking unhandled exceptions as 401 on the /error dispatch.
                            a.dispatcherTypeMatchers(DispatcherType.ERROR).permitAll();

                            a.requestMatchers("/actuator/health/**", "/actuator/info/**")
                                    .permitAll();

                            if (devOrDocker) {
                                a.requestMatchers(
                                                "/v3/api-docs/**",
                                                "/swagger-ui/**",
                                                "/swagger-ui.html")
                                        .permitAll();
                                // dev/docker: let the AI endpoints be tried directly from Swagger.
                                a.requestMatchers(
                                                "/api/v1/ai/**",
                                                "/api/empresa/ia/**",
                                                "/api/ambassadors/ai/**",
                                                "/api/specialists/ai/**")
                                        .permitAll();
                            }

                            a.anyRequest().authenticated();
                        })
                .formLogin(AbstractHttpConfigurer::disable);

        if (jwtEnabled) {
            // JWT Resource Server: requires a configured issuer-uri / jwk-set-uri.
            http.oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
                    .exceptionHandling(
                            e ->
                                    e.authenticationEntryPoint(
                                                    new BearerTokenAuthenticationEntryPoint())
                                            .accessDeniedHandler(
                                                    new BearerTokenAccessDeniedHandler()));
        } else {
            // Default: HTTP Basic until the security step enables JWT.
            http.httpBasic(Customizer.withDefaults());
        }

        return http.build();
    }
}
