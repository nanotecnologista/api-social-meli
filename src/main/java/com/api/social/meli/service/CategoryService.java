package com.api.social.meli.service;

import com.api.social.meli.dto.category.CategoryCreateRequest;
import com.api.social.meli.dto.category.CategoryResponse;
import com.api.social.meli.exception.NotFoundException;
import com.api.social.meli.model.mysql.Category;
import com.api.social.meli.repository.mysql.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional
    public CategoryResponse create(CategoryCreateRequest req) {
        String name = req.getName() != null ? req.getName().trim() : null;
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name is required");
        }

        if (categoryRepository.existsByNameIgnoreCase(name)) {
            throw new IllegalArgumentException("Category already exists");
        }

        Category category = Category.builder()
                .name(name)
                .build();

        category = categoryRepository.save(category);
        return toResponse(category);
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> list() {
        return categoryRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoryResponse get(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Category not found"));
        return toResponse(category);
    }

    private CategoryResponse toResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }
}
