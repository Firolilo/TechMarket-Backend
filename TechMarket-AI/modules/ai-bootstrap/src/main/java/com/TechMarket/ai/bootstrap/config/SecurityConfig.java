package com.techmarket.ai.bootstrap.config;

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
        boolean dev = Arrays.asList(env.getActiveProfiles()).contains("dev");
        boolean jwtEnabled = env.getProperty("security.jwt.enabled", Boolean.class, false);

        http.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(
                        a -> {
                            a.requestMatchers("/actuator/health/**", "/actuator/info/**")
                                    .permitAll();

                            if (dev) {
                                a.requestMatchers(
                                                "/v3/api-docs/**",
                                                "/swagger-ui/**",
                                                "/swagger-ui.html")
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
