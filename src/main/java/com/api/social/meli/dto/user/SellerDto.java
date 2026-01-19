package com.api.social.meli.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Informações de um vendedor")
public class SellerDto {

    @JsonProperty("user_id")
    @Schema(description = "ID do vendedor", example = "2")
    private Long userId;

    @JsonProperty("user_name")
    @Schema(description = "Nome/nickname do vendedor", example = "joaosilva")
    private String userName;

    @JsonProperty("followers_count")
    @Schema(description = "Quantidade de seguidores", example = "150")
    private Integer followersCount;
}
