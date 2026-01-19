package com.api.social.meli.service;

import com.api.social.meli.dto.common.PaginationParams;
import com.api.social.meli.dto.product.ProductCreateRequest;
import com.api.social.meli.dto.product.ProductPageResponse;
import com.api.social.meli.dto.product.ProductResponse;
import com.api.social.meli.dto.product.ProductUpdateRequest;
import com.api.social.meli.exception.ForbiddenException;
import com.api.social.meli.exception.NotFoundException;
import com.api.social.meli.model.mysql.Category;
import com.api.social.meli.model.mysql.Product;
import com.api.social.meli.model.mysql.RoleName;
import com.api.social.meli.model.mysql.User;
import com.api.social.meli.repository.mysql.CategoryRepository;
import com.api.social.meli.repository.mysql.ProductRepository;
import com.api.social.meli.repository.mysql.UserRepository;
import com.api.social.meli.repository.mysql.UserRoleRepository;
import com.api.social.meli.util.PaginationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;

    @Transactional
    public ProductResponse create(Long authUserId, Set<RoleName> authRoles, ProductCreateRequest req) {
        requireSeller(authRoles);

        User seller = userRepository.findById(authUserId)
                .orElseThrow(() -> new NotFoundException("Authenticated user not found"));

        Category category = categoryRepository.findById(req.getCategoryId())
                .orElseThrow(() -> new NotFoundException("Category not found"));

        Product product = Product.builder()
                .name(req.getName())
                .type(req.getType())
                .brand(req.getBrand())
                .color(req.getColor())
                .category(category)
                .price(req.getPrice())
                .active(true)
                .seller(seller)
                .build();

        product = productRepository.save(product);

        return toResponse(product);
    }

    @Transactional
    public ProductResponse update(Long authUserId, Set<RoleName> authRoles, Long productId, ProductUpdateRequest req) {
        requireSeller(authRoles);

        Product product = productRepository.findByIdAndActiveTrue(productId)
                .orElseThrow(() -> new NotFoundException("Product not found"));

        if (!product.getSeller().getId().equals(authUserId)) {
            throw new ForbiddenException("You can only update your own products");
        }

        Category category = categoryRepository.findById(req.getCategoryId())
                .orElseThrow(() -> new NotFoundException("Category not found"));

        product.setName(req.getName());
        product.setType(req.getType());
        product.setBrand(req.getBrand());
        product.setColor(req.getColor());
        product.setCategory(category);
        product.setPrice(req.getPrice());

        product = productRepository.save(product);
        return toResponse(product);
    }

    @Transactional
    public void delete(Long authUserId, Set<RoleName> authRoles, Long productId) {
        requireSeller(authRoles);

        Product product = productRepository.findByIdAndActiveTrue(productId)
                .orElseThrow(() -> new NotFoundException("Product not found"));

        if (!product.getSeller().getId().equals(authUserId)) {
            throw new ForbiddenException("You can only delete your own products");
        }

        product.setActive(false);
        productRepository.save(product);
    }

    @Transactional(readOnly = true)
    public ProductResponse get(Long productId) {
        Product product = productRepository.findByIdAndActiveTrue(productId)
                .orElseThrow(() -> new NotFoundException("Product not found"));
        return toResponse(product);
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> listMyProducts(Long authUserId, Set<RoleName> authRoles) {
        requireSeller(authRoles);
        return productRepository.findBySellerIdAndActiveTrue(authUserId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductPageResponse listMyProductsPaged(Long authUserId, Set<RoleName> authRoles, PaginationParams pagination) {
        requireSeller(authRoles);

        Integer page = (pagination != null ? pagination.getPage() : null);
        Integer size = (pagination != null ? pagination.getSize() : null);
        Integer offset = (pagination != null ? pagination.getOffset() : null);
        Integer limit = (pagination != null ? pagination.getLimit() : null);

        Pageable pageable = PaginationUtils.resolvePageable(page, size, offset, limit);
        Page<Product> productsPage = productRepository.findBySellerIdAndActiveTrue(authUserId, pageable);

        return ProductPageResponse.builder()
                .total(productsPage.getTotalElements())
                .page(productsPage.getNumber())
                .size(productsPage.getSize())
                .products(productsPage.getContent().stream().map(this::toResponse).toList())
                .build();
    }

    @Transactional(readOnly = true)
    public ProductPageResponse listMyProductsPaged(Long authUserId, Set<RoleName> authRoles, Integer page, Integer size, Integer offset, Integer limit) {
        return listMyProductsPaged(authUserId, authRoles, PaginationParams.builder()
                .page(page)
                .size(size)
                .offset(offset)
                .limit(limit)
                .build());
    }

    private void requireSeller(Set<RoleName> authRoles) {
        if (authRoles == null || !authRoles.contains(RoleName.SELLER)) {
            throw new ForbiddenException("Only SELLER can manage products");
        }
    }

    private ProductResponse toResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .type(product.getType())
                .brand(product.getBrand())
                .color(product.getColor())
                .categoryId(product.getCategory().getId())
                .categoryName(product.getCategory().getName())
                .price(product.getPrice())
                .active(product.getActive())
                .sellerId(product.getSeller().getId())
                .build();
    }
}
