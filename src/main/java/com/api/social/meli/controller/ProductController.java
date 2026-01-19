package com.api.social.meli.controller;

import com.api.social.meli.config.AuthUserInterceptor;
import com.api.social.meli.dto.common.PaginationParams;
import com.api.social.meli.dto.product.ProductCreateRequest;
import com.api.social.meli.dto.product.ProductPageResponse;
import com.api.social.meli.dto.product.ProductResponse;
import com.api.social.meli.dto.product.ProductUpdateRequest;
import com.api.social.meli.model.mysql.RoleName;
import com.api.social.meli.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Tag(name = "Produtos", description = "Gerenciamento de produtos")
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Criar produto",
            description = "Cria um novo produto. Requer autenticação via header X-user-id"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Produto criado com sucesso",
                    content = @Content(schema = @Schema(implementation = ProductResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Usuário não autenticado"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Usuário não tem permissão para criar produtos"
            )
    })
    public ProductResponse create(
            @Valid @RequestBody ProductCreateRequest req,
            @Parameter(hidden = true) HttpServletRequest request) {
        Long authUserId = (Long) request.getAttribute(AuthUserInterceptor.REQ_ATTR_AUTH_USER_ID);
        Set<RoleName> roles = extractRoles(request);
        return productService.create(authUserId, roles, req);
    }

    @PutMapping("/{productId}")
    @Operation(
            summary = "Atualizar produto",
            description = "Atualiza um produto existente. Requer autenticação via header X-user-id"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Produto atualizado com sucesso",
                    content = @Content(schema = @Schema(implementation = ProductResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Usuário não autenticado"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Usuário não tem permissão para atualizar este produto"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Produto não encontrado"
            )
    })
    public ProductResponse update(
            @Parameter(description = "ID do produto", required = true)
            @PathVariable Long productId,
            @Valid @RequestBody ProductUpdateRequest req,
            @Parameter(hidden = true) HttpServletRequest request) {
        Long authUserId = (Long) request.getAttribute(AuthUserInterceptor.REQ_ATTR_AUTH_USER_ID);
        Set<RoleName> roles = extractRoles(request);
        return productService.update(authUserId, roles, productId, req);
    }

    @DeleteMapping("/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Deletar produto",
            description = "Remove um produto. Requer autenticação via header X-user-id"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Produto deletado com sucesso"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Usuário não autenticado"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Usuário não tem permissão para deletar este produto"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Produto não encontrado"
            )
    })
    public void delete(
            @Parameter(description = "ID do produto", required = true)
            @PathVariable Long productId,
            @Parameter(hidden = true) HttpServletRequest request) {
        Long authUserId = (Long) request.getAttribute(AuthUserInterceptor.REQ_ATTR_AUTH_USER_ID);
        Set<RoleName> roles = extractRoles(request);
        productService.delete(authUserId, roles, productId);
    }

    @GetMapping("/{productId}")
    @Operation(
            summary = "Buscar produto por ID",
            description = "Retorna os detalhes de um produto específico"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Produto encontrado com sucesso",
                    content = @Content(schema = @Schema(implementation = ProductResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Produto não encontrado"
            )
    })
    public ProductResponse get(
            @Parameter(description = "ID do produto", required = true)
            @PathVariable Long productId) {
        return productService.get(productId);
    }

    @GetMapping("/me")
    @Operation(
            summary = "Listar meus produtos",
            description = "Retorna todos os produtos do usuário autenticado. Requer autenticação via header X-user-id"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de produtos retornada com sucesso",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProductResponse.class)))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Usuário não autenticado"
            )
    })
    public List<ProductResponse> me(@Parameter(hidden = true) HttpServletRequest request) {
        Long authUserId = (Long) request.getAttribute(AuthUserInterceptor.REQ_ATTR_AUTH_USER_ID);
        Set<RoleName> roles = extractRoles(request);
        return productService.listMyProducts(authUserId, roles);
    }

    @GetMapping("/me/list")
    @Operation(
            summary = "Listar meus produtos (paginado)",
            description = "Retorna os produtos do usuário autenticado com paginação. Requer autenticação via header X-user-id"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Página de produtos retornada com sucesso",
                    content = @Content(schema = @Schema(implementation = ProductPageResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Usuário não autenticado"
            )
    })
    public ProductPageResponse mePaged(
            @Parameter(hidden = true) HttpServletRequest request,
            @Parameter(description = "Parâmetros de paginação (page, size, sort)")
            @ModelAttribute PaginationParams pagination) {
        Long authUserId = (Long) request.getAttribute(AuthUserInterceptor.REQ_ATTR_AUTH_USER_ID);
        Set<RoleName> roles = extractRoles(request);
        return productService.listMyProductsPaged(authUserId, roles, pagination);
    }

    private Set<RoleName> extractRoles(HttpServletRequest request) {
        Object raw = request.getAttribute(AuthUserInterceptor.REQ_ATTR_AUTH_ROLES);
        if (!(raw instanceof Set<?> rawSet)) {
            return Set.of();
        }
        return rawSet.stream()
                .map(RoleName.class::cast)
                .collect(Collectors.toSet());
    }
}
