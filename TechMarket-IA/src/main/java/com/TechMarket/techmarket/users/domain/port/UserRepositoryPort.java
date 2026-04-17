package com.techmarket.techmarket.users.domain.port;

import com.techmarket.techmarket.users.domain.model.User;
import java.util.Optional;
import java.util.UUID;

public interface UserRepositoryPort {

    User save(User user);

    Optional<User> findById(UUID id);
}
