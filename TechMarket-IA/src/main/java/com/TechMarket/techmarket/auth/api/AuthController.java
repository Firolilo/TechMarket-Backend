package com.techmarket.techmarket.auth.api;

import com.techmarket.techmarket.auth.api.request.LoginRequest;
import com.techmarket.techmarket.auth.api.response.LoginResponse;
import com.techmarket.techmarket.auth.application.service.AuthApplicationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthApplicationService service;

    public AuthController(AuthApplicationService service) {
        this.service = service;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return service.login(request.email(), request.password());
    }
}
