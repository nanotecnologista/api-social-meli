package com.api.social.meli.dto.user;

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
@Schema(description = "Lista paginada de vendedores")
public class SellerListResponse {

    @Schema(description = "Total de vendedores", example = "10")
    private long total;

    @Schema(description = "Página atual", example = "0")
    private int page;

    @Schema(description = "Tamanho da página", example = "10")
    private int size;

    @Schema(description = "Lista de vendedores")
    private List<SellerDto> sellers;
}
