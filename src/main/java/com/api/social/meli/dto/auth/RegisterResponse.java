package com.api.social.meli.dto.auth;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class RegisterResponse {
    private Long userId;
    private String nickname;
    private String email;
    private Set<String> roles;
}