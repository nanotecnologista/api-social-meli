package com.api.social.meli.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank
    private String login; // email OU nickname

    @NotBlank
    private String password;
}