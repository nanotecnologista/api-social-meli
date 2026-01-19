package com.api.social.meli.controller;

import com.api.social.meli.config.AuthUserInterceptor;
import com.api.social.meli.dto.common.PaginationParams;
import com.api.social.meli.dto.product.ProductCreateRequest;
import com.api.social.meli.dto.product.ProductPageResponse;
import com.api.social.meli.dto.product.ProductResponse;
import com.api.social.meli.dto.product.ProductUpdateRequest;
import com.api.social.meli.model.mysql.RoleName;
import com.api.social.meli.service.ProductService;
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
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse create(@Valid @RequestBody ProductCreateRequest req, HttpServletRequest request) {
        Long authUserId = (Long) request.getAttribute(AuthUserInterceptor.REQ_ATTR_AUTH_USER_ID);
        Set<RoleName> roles = extractRoles(request);
        return productService.create(authUserId, roles, req);
    }

    @PutMapping("/{productId}")
    public ProductResponse update(
            @PathVariable Long productId,
            @Valid @RequestBody ProductUpdateRequest req,
            HttpServletRequest request) {
        Long authUserId = (Long) request.getAttribute(AuthUserInterceptor.REQ_ATTR_AUTH_USER_ID);
        Set<RoleName> roles = extractRoles(request);
        return productService.update(authUserId, roles, productId, req);
    }

    @DeleteMapping("/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long productId, HttpServletRequest request) {
        Long authUserId = (Long) request.getAttribute(AuthUserInterceptor.REQ_ATTR_AUTH_USER_ID);
        Set<RoleName> roles = extractRoles(request);
        productService.delete(authUserId, roles, productId);
    }

    @GetMapping("/{productId}")
    public ProductResponse get(@PathVariable Long productId) {
        return productService.get(productId);
    }

    @GetMapping("/me")
    public List<ProductResponse> me(HttpServletRequest request) {
        Long authUserId = (Long) request.getAttribute(AuthUserInterceptor.REQ_ATTR_AUTH_USER_ID);
        Set<RoleName> roles = extractRoles(request);
        return productService.listMyProducts(authUserId, roles);
    }

    @GetMapping("/me/list")
    public ProductPageResponse mePaged(
            HttpServletRequest request,
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
