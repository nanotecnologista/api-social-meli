package com.api.social.meli.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Perfil completo do usuário com estatísticas")
public class UserProfileResponse {
    
    @JsonProperty("userId")
    @Schema(description = "ID do usuário", example = "1")
    private Long userId;
    
    @Schema(description = "Nome completo do usuário", example = "João Silva")
    private String name;
    
    @Schema(description = "Nome de usuário (nickname)", example = "joaosilva")
    private String nickname;
    
    @Schema(description = "Email do usuário", example = "joao.silva@email.com")
    private String email;
    
    @Schema(description = "Roles/perfis do usuário", example = "[\"BUYER\", \"SELLER\"]")
    private Set<String> roles;
    
    @JsonProperty("followersCount")
    @Schema(description = "Quantidade de seguidores", example = "150")
    private Integer followersCount;
    
    @JsonProperty("followingCount")
    @Schema(description = "Quantidade de usuários seguidos", example = "25")
    private Integer followingCount;
}
