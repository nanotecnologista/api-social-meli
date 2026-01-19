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