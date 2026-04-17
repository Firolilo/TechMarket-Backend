package com.techmarket.techmarket.users.api.admin;

import com.techmarket.techmarket.users.api.admin.request.CreateUserRequest;
import com.techmarket.techmarket.users.api.admin.response.UserResponse;
import com.techmarket.techmarket.users.application.command.CreateUserCommand;
import com.techmarket.techmarket.users.application.query.GetUserByIdQuery;
import com.techmarket.techmarket.users.application.service.UserApplicationService;
import com.techmarket.techmarket.users.domain.model.User;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/admin/users")
public class UserController {

    private final UserApplicationService service;

    public UserController(UserApplicationService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse create(@Valid @RequestBody CreateUserRequest request) {
        User user =
                service.create(
                        new CreateUserCommand(
                                request.firstName(),
                                request.lastName(),
                                request.email(),
                                request.status()));
        return toResponse(user);
    }

    @GetMapping("/{id}")
    public UserResponse getById(@PathVariable UUID id) {
        User user = service.getById(new GetUserByIdQuery(id));
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        return toResponse(user);
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.id(),
                user.firstName(),
                user.lastName(),
                user.email(),
                user.status(),
                user.createdAt(),
                user.updatedAt());
    }
}
