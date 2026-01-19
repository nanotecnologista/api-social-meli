package com.api.social.meli.controller;

import com.api.social.meli.config.AuthUserInterceptor;
import com.api.social.meli.dto.post.*;
import com.api.social.meli.model.mysql.RoleName;
import com.api.social.meli.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Tag(name = "Posts", description = "Gerenciamento de publicações de produtos")
public class PostController {

    private final PostService postService;

    @PostMapping("/publish")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Publicar produto",
            description = "Cria uma nova publicação de produto. Requer autenticação via header X-user-id"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Produto publicado com sucesso"
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
                    description = "Usuário não tem permissão para publicar"
            )
    })
    public void publish(
            @Valid @RequestBody PostPublishRequest req,
            @Parameter(hidden = true) HttpServletRequest request) {
        Long authUserId = (Long) request.getAttribute(AuthUserInterceptor.REQ_ATTR_AUTH_USER_ID);
        Set<RoleName> roles = extractRoles(request);
        postService.publish(authUserId, roles, req);
    }

    @PostMapping("/promo-pub")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Publicar produto promocional",
            description = "Cria uma nova publicação de produto com promoção. Requer autenticação via header X-user-id"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Produto promocional publicado com sucesso"
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
                    description = "Usuário não tem permissão para publicar promoções"
            )
    })
    public void promoPublish(
            @Valid @RequestBody PostPromoPublishRequest req,
            @Parameter(hidden = true) HttpServletRequest request) {
        Long authUserId = (Long) request.getAttribute(AuthUserInterceptor.REQ_ATTR_AUTH_USER_ID);
        Set<RoleName> roles = extractRoles(request);
        postService.publishPromo(authUserId, roles, req);
    }

    @GetMapping("/posts/count")
    @Operation(
            summary = "Contar publicações do vendedor",
            description = """
                    Retorna a quantidade total de publicações de um vendedor.
                    
                    **Exemplo de Response (200 OK):**
                    ```json
                    {
                      "user_id": 1,
                      "user_name": "joaosilva",
                      "posts_count": 25
                    }
                    ```
                    
                    **Observações:**
                    - Conta todas as publicações (normais e promocionais)
                    - Endpoint público (não requer autenticação)
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Contagem retornada com sucesso",
                    content = @Content(schema = @Schema(implementation = PostCountResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuário não encontrado"
            )
    })
    public PostCountResponse postCount(
            @Parameter(description = "ID do usuário vendedor", required = true)
            @RequestParam("user_id") Long userId) {
        return postService.getPostCount(userId);
    }

    @GetMapping("/promo-pub/count")
    @Operation(
            summary = "Contar produtos promocionais",
            description = "Retorna a quantidade de produtos promocionais de um vendedor"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Contagem retornada com sucesso",
                    content = @Content(schema = @Schema(implementation = PromoCountResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuário não encontrado"
            )
    })
    public PromoCountResponse promoCount(
            @Parameter(description = "ID do usuário vendedor", required = true)
            @RequestParam("user_id") Long userId) {
        return postService.getPromoCount(userId);
    }

    @GetMapping("/promo-pub/list")
    @Operation(
            summary = "Listar produtos promocionais",
            description = "Retorna a lista de produtos promocionais de um vendedor"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista retornada com sucesso",
                    content = @Content(schema = @Schema(implementation = PromoListResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuário não encontrado"
            )
    })
    public PromoListResponse promoList(
            @Parameter(description = "ID do usuário vendedor", required = true)
            @RequestParam("user_id") Long userId) {
        return postService.getPromoList(userId);
    }

    @GetMapping("/followed/{userId}/list")
    @Operation(
            summary = "Timeline de produtos seguidos",
            description = "Retorna a timeline de produtos dos vendedores seguidos por um usuário"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Timeline retornada com sucesso",
                    content = @Content(schema = @Schema(implementation = TimelineResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuário não encontrado"
            )
    })
    public TimelineResponse followedTimeline(
            @Parameter(description = "ID do usuário", required = true)
            @PathVariable Long userId,
            @Parameter(description = "Ordenação: 'date_asc' ou 'date_desc'")
            @RequestParam(value = "order", required = false) String order) {
        return postService.getFollowedTimeline(userId, order);
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
