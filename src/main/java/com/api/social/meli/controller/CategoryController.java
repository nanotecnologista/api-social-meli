package com.api.social.meli.controller;

import com.api.social.meli.dto.category.CategoryCreateRequest;
import com.api.social.meli.dto.category.CategoryResponse;
import com.api.social.meli.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Tag(name = "Categorias", description = "Gerenciamento de categorias de produtos")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Criar nova categoria",
            description = "Cria uma nova categoria de produtos no sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Categoria criada com sucesso",
                    content = @Content(schema = @Schema(implementation = CategoryResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos"
            )
    })
    public CategoryResponse create(@Valid @RequestBody CategoryCreateRequest req) {
        return categoryService.create(req);
    }

    @GetMapping
    @Operation(
            summary = "Listar todas as categorias",
            description = "Retorna a lista completa de categorias disponíveis"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de categorias retornada com sucesso",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = CategoryResponse.class)))
            )
    })
    public List<CategoryResponse> list() {
        return categoryService.list();
    }

    @GetMapping("/{categoryId}")
    @Operation(
            summary = "Buscar categoria por ID",
            description = "Retorna os detalhes de uma categoria específica"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Categoria encontrada com sucesso",
                    content = @Content(schema = @Schema(implementation = CategoryResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Categoria não encontrada"
            )
    })
    public CategoryResponse get(
            @Parameter(description = "ID da categoria", required = true)
            @PathVariable Long categoryId) {
        return categoryService.get(categoryId);
    }
}
