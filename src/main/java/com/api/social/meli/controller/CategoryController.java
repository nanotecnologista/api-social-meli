package com.api.social.meli.controller;

import com.api.social.meli.dto.category.CategoryCreateRequest;
import com.api.social.meli.dto.category.CategoryResponse;
import com.api.social.meli.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponse create(@Valid @RequestBody CategoryCreateRequest req) {
        return categoryService.create(req);
    }

    @GetMapping
    public List<CategoryResponse> list() {
        return categoryService.list();
    }

    @GetMapping("/{categoryId}")
    public CategoryResponse get(@PathVariable Long categoryId) {
        return categoryService.get(categoryId);
    }
}
