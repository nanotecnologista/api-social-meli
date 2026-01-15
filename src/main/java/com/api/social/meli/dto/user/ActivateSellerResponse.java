package com.api.social.meli.dto.user;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class ActivateSellerResponse {
    private Long userId;
    private Set<String> roles;
}