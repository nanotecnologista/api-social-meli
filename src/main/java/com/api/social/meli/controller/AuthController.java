package com.api.social.meli.controller;

import com.api.social.meli.dto.auth.LoginRequest;
import com.api.social.meli.dto.auth.LoginResponse;
import com.api.social.meli.dto.auth.RegisterRequest;
import com.api.social.meli.dto.auth.RegisterResponse;
import com.api.social.meli.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public RegisterResponse register(@Valid @RequestBody RegisterRequest req) {
        return authService.register(req);
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest req) {
        return authService.login(req);
    }
}