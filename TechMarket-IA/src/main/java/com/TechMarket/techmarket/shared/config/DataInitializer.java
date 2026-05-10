package com.techmarket.techmarket.shared.config;

import com.techmarket.techmarket.ambassadors.domain.model.Ambassador;
import com.techmarket.techmarket.ambassadors.domain.port.AmbassadorRepositoryPort;
import com.techmarket.techmarket.users.domain.model.User;
import com.techmarket.techmarket.users.domain.port.UserRepositoryPort;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private static final String ADMIN_EMAIL = "admin@gmail.com";
    private static final String ADMIN_PASSWORD = "admin";

    private final UserRepositoryPort userRepository;
    private final AmbassadorRepositoryPort ambassadorRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(
            UserRepositoryPort userRepository,
            AmbassadorRepositoryPort ambassadorRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.ambassadorRepository = ambassadorRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.findByEmail(ADMIN_EMAIL).isPresent()) {
            log.info("Seed: usuario admin ya existe, omitiendo.");
            return;
        }

        OffsetDateTime now = OffsetDateTime.now();
        UUID userId = UUID.randomUUID();
        UUID ambassadorId = UUID.randomUUID();

        User user =
                new User(
                        userId,
                        "Admin",
                        "TechMarket",
                        ADMIN_EMAIL,
                        passwordEncoder.encode(ADMIN_PASSWORD),
                        "ACTIVE",
                        now,
                        now);
        userRepository.save(user);

        Ambassador ambassador =
                new Ambassador(
                        ambassadorId,
                        userId,
                        "AF-ADMIN",
                        "ACTIVE",
                        "BRONCE",
                        now);
        ambassadorRepository.save(ambassador);

        log.info("Seed: usuario admin@gmail.com creado (ambassador id={}).", ambassadorId);
    }
}
