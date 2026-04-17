package com.techmarket.core.iam.application.usecase;

import com.techmarket.core.iam.application.command.CreateRoleCommand;
import com.techmarket.core.iam.application.port.RoleRepositoryPort;
import com.techmarket.core.iam.domain.model.Role;

public class CreateRoleUseCase {

    private final RoleRepositoryPort roleRepository;

    public CreateRoleUseCase(RoleRepositoryPort roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Role execute(CreateRoleCommand command) {
        Role role = new Role(command.name(), command.description());
        return roleRepository.save(role);
    }
}
