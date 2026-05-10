package com.techmarket.techmarket.auth.application.service;

import com.techmarket.techmarket.auth.api.response.LoginResponse;
import com.techmarket.techmarket.security.jwt.JwtTokenProvider;
import com.techmarket.techmarket.users.domain.model.User;
import com.techmarket.techmarket.users.domain.port.UserRepositoryPort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthApplicationService {

    private final UserRepositoryPort userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    public AuthApplicationService(
            UserRepositoryPort userRepository,
            JwtTokenProvider jwtTokenProvider,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponse login(String email, String password) {
        User user =
                userRepository
                        .findByEmail(email)
                        .orElseThrow(
                                () ->
                                        new ResponseStatusException(
                                                HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        if (user.passwordHash() == null
                || !passwordEncoder.matches(password, user.passwordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        String accessToken =
                jwtTokenProvider.generateAccessToken(user.id(), user.email(), "AMBASSADOR");
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.id());

        return new LoginResponse(
                accessToken,
                refreshToken,
                "Bearer",
                86400L,
                user.id(),
                user.email(),
                user.firstName(),
                user.lastName());
    }
}
