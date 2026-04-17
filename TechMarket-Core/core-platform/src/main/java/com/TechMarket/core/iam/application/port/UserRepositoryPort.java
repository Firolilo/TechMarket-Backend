package com.techmarket.core.iam.application.port;

import com.techmarket.core.iam.domain.model.User;
import java.util.Optional;

public interface UserRepositoryPort {
    Optional<User> findById(Long id);

    User save(User user);
}
