package com.api.social.meli.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank
    @NotNull
    private String name;

    @NotBlank
    @NotNull
    @Size(max = 30)
    private String nickname;

    @NotBlank
    @NotNull
    @Email
    @Size(max = 100)
    private String email;

    @NotBlank
    @NotNull
    @Size(min = 6, max = 100)
    private String password;
}