package com.api.social.meli.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
@Schema(description = "Resposta de registro com dados completos do usuário")
public class RegisterResponse {
    
    @JsonProperty("userId")
    @Schema(description = "ID do usuário", example = "1")
    private Long userId;
    
    @Schema(description = "Nome completo do usuário", example = "João Silva")
    private String name;
    
    @Schema(description = "Nome de usuário (nickname)", example = "joaosilva")
    private String nickname;
    
    @Schema(description = "Email do usuário", example = "joao.silva@email.com")
    private String email;
    
    @Schema(description = "Roles/perfis do usuário", example = "[\"BUYER\"]")
    private Set<String> roles;
    
    @JsonProperty("followersCount")
    @Schema(description = "Quantidade de seguidores", example = "0")
    private Integer followersCount;
    
    @JsonProperty("followingCount")
    @Schema(description = "Quantidade de usuários seguidos", example = "0")
    private Integer followingCount;
}