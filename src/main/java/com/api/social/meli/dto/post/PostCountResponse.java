package com.api.social.meli.dto.post;

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
@Schema(description = "Contagem de publicações de um vendedor")
public class PostCountResponse {

    @JsonProperty("user_id")
    @Schema(description = "ID do vendedor", example = "1")
    private Long userId;

    @JsonProperty("user_name")
    @Schema(description = "Nome do vendedor", example = "joaosilva")
    private String userName;

    @JsonProperty("posts_count")
    @Schema(description = "Quantidade total de publicações", example = "25")
    private Long postsCount;
}
