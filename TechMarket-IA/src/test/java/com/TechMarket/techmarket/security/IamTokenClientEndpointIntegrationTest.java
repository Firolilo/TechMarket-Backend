package com.techmarket.techmarket.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.techmarket.techmarket.users.infrastructure.persistence.jpa.entity.UserJpaEntity;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.repository.UserSpringDataRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class IamTokenClientEndpointIntegrationTest {

    private static final String SHARED_SECRET = "test-shared-iam-secret-value-32-bytes-minimum";

    @Autowired private MockMvc mockMvc;

    @Autowired private UserSpringDataRepository userRepository;

    private UUID userId;
    private String token;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        userId = UUID.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();
        UserJpaEntity user = new UserJpaEntity();
        user.setId(userId);
        user.setFirstName("IAM");
        user.setLastName("User");
        user.setEmail("iam-user@techmarket.com");
        user.setStatus("ACTIVE");
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        userRepository.save(user);

        token = iamAccessToken("77");
    }

    @Test
    void clientEndpoints_shouldAcceptIamAccessTokenWithUserHeader() throws Exception {
        assertAuthenticatedGet("/api/clients/profile");
        assertAuthenticatedGet("/api/clients/addresses");
        assertAuthenticatedGet("/api/clients/chats");
        assertAuthenticatedGet("/api/clients/notifications");
    }

    @Test
    void clientEndpoint_shouldRejectUserHeaderThatDoesNotMatchIamTokenUser() throws Exception {
        mockMvc.perform(
                        get("/api/clients/profile")
                                .header("Authorization", "Bearer " + token)
                                .header("X-User-Id", UUID.randomUUID().toString()))
                .andExpect(status().isForbidden());
    }

    private void assertAuthenticatedGet(String path) throws Exception {
        mockMvc.perform(
                        get(path)
                                .header("Authorization", "Bearer " + token)
                                .header("X-User-Id", userId.toString()))
                .andExpect(status().isOk());
    }

    private String iamAccessToken(String subject) {
        Date now = new Date();
        SecretKey key = Keys.hmacShaKeyFor(SHARED_SECRET.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(subject)
                .issuer("iam-service")
                .issuedAt(now)
                .expiration(new Date(now.getTime() + 900_000))
                .claim("token_type", "access")
                .claim("tenant_id", UUID.randomUUID().toString())
                .claim("username", "iam-user@techmarket.com")
                .claim("roles", List.of("cliente"))
                .claim("scope", "profile:read")
                .claim("scopes", List.of("GLOBAL"))
                .signWith(key)
                .compact();
    }
}
