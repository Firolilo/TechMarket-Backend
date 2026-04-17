package com.techmarket.techmarket.users.application.service;

import com.techmarket.techmarket.users.application.command.CreateUserCommand;
import com.techmarket.techmarket.users.application.query.GetUserByIdQuery;
import com.techmarket.techmarket.users.domain.model.User;
import com.techmarket.techmarket.users.domain.port.UserRepositoryPort;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserApplicationService {

    private final UserRepositoryPort repository;

    public UserApplicationService(UserRepositoryPort repository) {
        this.repository = repository;
    }

    @Transactional
    public User create(CreateUserCommand command) {
        OffsetDateTime now = OffsetDateTime.now();
        User user =
                new User(
                        UUID.randomUUID(),
                        command.firstName(),
                        command.lastName(),
                        command.email(),
                        command.status(),
                        now,
                        now);
        return repository.save(user);
    }

    @Transactional(readOnly = true)
    public User getById(GetUserByIdQuery query) {
        return repository.findById(query.id()).orElse(null);
    }
}
