package com.techmarket.techmarket.users.infrastructure.persistence.adapter;

import com.techmarket.techmarket.users.domain.model.User;
import com.techmarket.techmarket.users.domain.port.UserRepositoryPort;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.repository.UserSpringDataRepository;
import com.techmarket.techmarket.users.infrastructure.persistence.mapper.UserPersistenceMapper;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final UserSpringDataRepository springDataRepository;
    private final UserPersistenceMapper mapper;

    public UserRepositoryAdapter(
            UserSpringDataRepository springDataRepository, UserPersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public User save(User user) {
        return mapper.toDomain(springDataRepository.save(mapper.toJpa(user)));
    }

    @Override
    public Optional<User> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }
}
