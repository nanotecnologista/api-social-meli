package com.api.social.meli.dto.user;

import com.api.social.meli.dto.post.PostDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Perfil completo de um vendedor com posts paginados")
public class SellerProfileResponse {

    @Schema(description = "ID do vendedor", example = "2")
    private Long userId;

    @Schema(description = "Nome do vendedor", example = "João Silva")
    private String name;

    @Schema(description = "Nickname do vendedor", example = "joaosilva")
    private String userName;

    @Schema(description = "Quantidade de seguidores", example = "150")
    private Integer followersCount;

    @Schema(description = "Quantidade de usuários seguindo", example = "25")
    private Integer followingCount;

    @Schema(description = "Total de posts do vendedor", example = "50")
    private long totalPosts;

    @Schema(description = "Total de posts ativos (últimos 14 dias)", example = "15")
    private long activePostsCount;

    @Schema(description = "Página atual dos posts", example = "0")
    private int page;

    @Schema(description = "Tamanho da página dos posts", example = "10")
    private int size;

    @Schema(description = "Lista de posts do vendedor")
    private List<PostDto> posts;
}
