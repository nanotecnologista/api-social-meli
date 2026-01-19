package com.api.social.meli.controller;

import com.api.social.meli.config.AuthUserInterceptor;
import com.api.social.meli.dto.common.PaginationParams;
import com.api.social.meli.dto.user.*;
import com.api.social.meli.service.FollowService;
import com.api.social.meli.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Usuários", description = "Gerenciamento de usuários e relacionamentos de seguir/seguidores")
public class UserController {

    private final UserService userService;
    private final FollowService followService;

    @GetMapping("/sellers/{id}")
    @Operation(
            summary = "Buscar perfil completo de um vendedor",
            description = """
                    Retorna o perfil completo de um vendedor incluindo seus posts ativos (últimos 14 dias) paginados.
                    
                    **Parâmetros:**
                    - `id`: ID do vendedor (path parameter)
                    - `page`: Número da página dos posts (começa em 0) - padrão: 0
                    - `size`: Tamanho da página dos posts - padrão: 10
                    
                    **Exemplo de Response (200 OK):**
                    ```json
                    {
                      "userId": 2,
                      "name": "João Silva",
                      "userName": "joaosilva",
                      "followersCount": 150,
                      "followingCount": 25,
                      "totalPosts": 50,
                      "activePostsCount": 15,
                      "page": 0,
                      "size": 10,
                      "posts": [
                        {
                          "user_id": 2,
                          "post_id": "abc123",
                          "date": "15-01-2026",
                          "product": {
                            "productId": 10,
                            "productName": "Cadeira Gamer",
                            "type": "Gamer",
                            "brand": "Razer",
                            "color": "Preto"
                          },
                          "category": 1,
                          "price": 1500.00,
                          "has_promo": true,
                          "discount": 10.0
                        }
                      ]
                    }
                    ```
                    
                    **Observações:**
                    - Endpoint público (não requer autenticação)
                    - Retorna apenas se o usuário for SELLER
                    - **totalPosts**: Total de todas as publicações do vendedor
                    - **activePostsCount**: Total de publicações dos últimos 14 dias
                    - **posts**: Lista apenas publicações ativas (últimos 14 dias)
                    - Inclui contadores de seguidores e seguindo
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Perfil do vendedor retornado com sucesso",
                    content = @Content(schema = @Schema(implementation = SellerProfileResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Vendedor não encontrado"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Usuário não é um vendedor"
            )
    })
    public SellerProfileResponse getSellerProfile(
            @Parameter(description = "ID do vendedor", required = true)
            @PathVariable Long id,
            @Parameter(description = "Número da página dos posts (começa em 0)")
            @RequestParam(value = "page", defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página dos posts")
            @RequestParam(value = "size", defaultValue = "10") int size) {
        return userService.getSellerProfile(id, page, size);
    }

    @GetMapping("/sellers")
    @Operation(
            summary = "Listar todos os vendedores (paginado)",
            description = """
                    Retorna a lista paginada de todos os usuários que possuem a role SELLER.
                    
                    **Parâmetros:**
                    - `page`: Número da página (começa em 0) - padrão: 0
                    - `size`: Tamanho da página - padrão: 10
                    - `exclude_followed_by`: ID do usuário para excluir vendedores que ele já segue (opcional)
                    
                    **Exemplo de Response (200 OK):**
                    ```json
                    {
                      "total": 10,
                      "page": 0,
                      "size": 10,
                      "sellers": [
                        {
                          "user_id": 2,
                          "user_name": "seller1",
                          "followers_count": 150
                        },
                        {
                          "user_id": 5,
                          "user_name": "joaosilva",
                          "followers_count": 75
                        }
                      ]
                    }
                    ```
                    
                    **Exemplos de Uso:**
                    - `GET /users/sellers` - Primeira página com 10 vendedores
                    - `GET /users/sellers?page=1&size=5` - Segunda página com 5 vendedores
                    - `GET /users/sellers?exclude_followed_by=1` - Vendedores que o usuário 1 NÃO segue
                    - `GET /users/sellers?exclude_followed_by=1&page=0&size=20` - Combinação de filtro e paginação
                    
                    **Observações:**
                    - Endpoint público (não requer autenticação)
                    - Retorna apenas usuários com role SELLER
                    - Inclui contagem de seguidores de cada vendedor
                    - Use o parâmetro exclude_followed_by para descobrir novos vendedores
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de vendedores retornada com sucesso",
                    content = @Content(schema = @Schema(implementation = SellerListResponse.class))
            )
    })
    public SellerListResponse getAllSellers(
            @Parameter(description = "Número da página (começa em 0)")
            @RequestParam(value = "page", defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página")
            @RequestParam(value = "size", defaultValue = "10") int size,
            @Parameter(description = "ID do usuário para excluir vendedores já seguidos (opcional)")
            @RequestParam(value = "exclude_followed_by", required = false) Long excludeFollowedBy) {
        return userService.getAllSellers(excludeFollowedBy, page, size);
    }

    @GetMapping("/me")
    @Operation(
            summary = "Buscar perfil do usuário autenticado",
            description = """
                    Retorna os dados completos do usuário autenticado, incluindo estatísticas de seguidores.
                    
                    **Exemplo de Response (200 OK):**
                    ```json
                    {
                      "userId": 1,
                      "name": "João Silva",
                      "nickname": "joaosilva",
                      "email": "joao.silva@email.com",
                      "roles": ["BUYER", "SELLER"],
                      "followersCount": 150,
                      "followingCount": 25
                    }
                    ```
                    
                    **Observações:**
                    - Requer header X-user-id
                    - Retorna dados do usuário autenticado (não inclui senha)
                    - Útil para carregar perfil do usuário após login
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Perfil do usuário retornado com sucesso",
                    content = @Content(schema = @Schema(implementation = UserProfileResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Usuário não autenticado"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuário não encontrado"
            )
    })
    public UserProfileResponse getMyProfile(@Parameter(hidden = true) HttpServletRequest request) {
        Long authUserId = (Long) request.getAttribute(AuthUserInterceptor.REQ_ATTR_AUTH_USER_ID);
        return userService.getUserProfile(authUserId);
    }

    @PostMapping("/{id}/activate-seller")
    @Operation(
            summary = "Ativar usuário como vendedor",
            description = "Transforma um usuário comum em vendedor. Requer autenticação via header X-user-id"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuário ativado como vendedor com sucesso",
                    content = @Content(schema = @Schema(implementation = ActivateSellerResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Usuário não autenticado"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Usuário não tem permissão para ativar vendedores"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuário não encontrado"
            )
    })
    public ActivateSellerResponse activateSeller(
            @Parameter(description = "ID do usuário a ser ativado como vendedor", required = true)
            @PathVariable() Long id,
            @Parameter(hidden = true) HttpServletRequest request) {
        Long authUserId = (Long) request.getAttribute(AuthUserInterceptor.REQ_ATTR_AUTH_USER_ID);
        return userService.activateSeller(authUserId, id);
    }

    @PostMapping("/{userId}/follow/{userIdToFollow}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Seguir usuário",
            description = "Permite que um usuário siga outro vendedor. Requer autenticação via header X-user-id"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuário seguido com sucesso"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Não é possível seguir a si mesmo ou usuário já está sendo seguido"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Usuário não autenticado"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Usuário não pode seguir em nome de outro usuário"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuário não encontrado"
            )
    })
    public void followUser(
            @Parameter(description = "ID do usuário que vai seguir", required = true)
            @PathVariable() Long userId,
            @Parameter(description = "ID do usuário a ser seguido", required = true)
            @PathVariable() Long userIdToFollow,
            @Parameter(hidden = true) HttpServletRequest request) {
        Long authUserId = (Long) request.getAttribute(AuthUserInterceptor.REQ_ATTR_AUTH_USER_ID);
        if (!authUserId.equals(userId)) {
            throw new IllegalArgumentException("You can only follow users as yourself");
        }
        followService.followUser(userId, userIdToFollow);
    }

    @PostMapping("/{userId}/unfollow/{userIdToUnfollow}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Deixar de seguir usuário",
            description = "Permite que um usuário deixe de seguir um vendedor. Requer autenticação via header X-user-id"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Deixou de seguir com sucesso"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Usuário não está seguindo o vendedor especificado"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Usuário não autenticado"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Usuário não pode deixar de seguir em nome de outro usuário"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuário não encontrado"
            )
    })
    public void unfollowUser(
            @Parameter(description = "ID do usuário que vai deixar de seguir", required = true)
            @PathVariable() Long userId,
            @Parameter(description = "ID do usuário a deixar de seguir", required = true)
            @PathVariable() Long userIdToUnfollow,
            @Parameter(hidden = true) HttpServletRequest request) {
        Long authUserId = (Long) request.getAttribute(AuthUserInterceptor.REQ_ATTR_AUTH_USER_ID);
        if (!authUserId.equals(userId)) {
            throw new IllegalArgumentException("You can only unfollow users as yourself");
        }
        followService.unfollowUser(userId, userIdToUnfollow);
    }

    @GetMapping("/{userId}/followers/count")
    @Operation(
            summary = "Contar seguidores",
            description = "Retorna a quantidade de seguidores de um usuário vendedor"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Contagem retornada com sucesso",
                    content = @Content(schema = @Schema(implementation = FollowerCountResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuário não encontrado"
            )
    })
    public FollowerCountResponse getFollowersCount(
            @Parameter(description = "ID do usuário vendedor", required = true)
            @PathVariable() Long userId) {
        return followService.getFollowersCount(userId);
    }

    @GetMapping("/{userId}/followers/list")
    @Operation(
            summary = "Listar seguidores",
            description = "Retorna a lista de seguidores de um usuário vendedor com paginação"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de seguidores retornada com sucesso",
                    content = @Content(schema = @Schema(implementation = FollowerListResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuário não encontrado"
            )
    })
    public FollowerListResponse getFollowersList(
            @Parameter(description = "ID do usuário vendedor", required = true)
            @PathVariable() Long userId,
            @Parameter(description = "Parâmetros de paginação (page, size, sort)")
            @ModelAttribute PaginationParams pagination) {
        return followService.getFollowersList(userId, pagination);
    }

    @GetMapping("/{userId}/followed/list")
    @Operation(
            summary = "Listar usuários seguidos",
            description = "Retorna a lista de vendedores que um usuário segue com paginação"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de seguidos retornada com sucesso",
                    content = @Content(schema = @Schema(implementation = FollowedListResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuário não encontrado"
            )
    })
    public FollowedListResponse getFollowedList(
            @Parameter(description = "ID do usuário", required = true)
            @PathVariable() Long userId,
            @Parameter(description = "Parâmetros de paginação (page, size, sort)")
            @ModelAttribute PaginationParams pagination) {
        return followService.getFollowedList(userId, pagination);
    }
}