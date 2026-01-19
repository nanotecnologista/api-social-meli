package com.api.social.meli.service;

import com.api.social.meli.dto.post.*;
import com.api.social.meli.exception.ForbiddenException;
import com.api.social.meli.exception.NotFoundException;
import com.api.social.meli.model.mongo.Post;
import com.api.social.meli.model.mongo.PostProduct;
import com.api.social.meli.model.mysql.Category;
import com.api.social.meli.model.mysql.Follow;
import com.api.social.meli.model.mysql.Product;
import com.api.social.meli.model.mysql.RoleName;
import com.api.social.meli.model.mysql.User;
import com.api.social.meli.repository.mongo.PostRepository;
import com.api.social.meli.repository.mysql.CategoryRepository;
import com.api.social.meli.repository.mysql.FollowRepository;
import com.api.social.meli.repository.mysql.ProductRepository;
import com.api.social.meli.repository.mysql.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final FollowRepository followRepository;

    @Transactional
    public void publish(Long authUserId, Set<RoleName> authRoles, PostPublishRequest req) {
        requireSeller(authUserId, authRoles, req.getUserId());
        Product product = validateProductOwnership(authUserId, req.getProductId());
        Post post = buildPost(authUserId, req.getDate(), product, req.getCategoryId(), false, null);
        postRepository.save(post);
    }

    @Transactional
    public void publishPromo(Long authUserId, Set<RoleName> authRoles, PostPromoPublishRequest req) {
        requireSeller(authUserId, authRoles, req.getUserId());
        Product product = validateProductOwnership(authUserId, req.getProductId());
        Post post = buildPost(authUserId, req.getDate(), product, req.getCategoryId(), true, req.getDiscount());
        postRepository.save(post);
    }

    @Transactional(readOnly = true)
    public PromoCountResponse getPromoCount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        long count = postRepository.countByUserIdAndHasPromoTrue(userId);

        return PromoCountResponse.builder()
                .userId(user.getId())
                .userName(user.getNickname())
                .promoProductsCount(count)
                .build();
    }

    @Transactional(readOnly = true)
    public PromoListResponse getPromoList(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        List<Post> promoPosts = postRepository.findByUserIdAndHasPromoTrue(userId);

        List<PostDto> postDtos = promoPosts.stream()
                .map(this::mapToPostDto)
                .toList();

        return PromoListResponse.builder()
                .userId(user.getId())
                .userName(user.getNickname())
                .posts(postDtos)
                .build();
    }

    @Transactional(readOnly = true)
    public TimelineResponse getFollowedTimeline(Long userId, String order) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found");
        }

        List<Follow> follows = followRepository.findByUserId(userId);
        List<Long> followedUserIds = follows.stream()
                .map(follow -> follow.getSeller().getId())
                .toList();

        if (followedUserIds.isEmpty()) {
            return TimelineResponse.builder()
                    .userId(userId)
                    .posts(List.of())
                    .build();
        }

        LocalDate twoWeeksAgo = LocalDate.now().minusDays(14);
        List<Post> posts = postRepository.findByUserIdInAndDateGreaterThanEqual(followedUserIds, twoWeeksAgo);

        List<PostDto> postDtos = posts.stream()
                .map(this::mapToPostDto)
                .sorted(resolveDateSort(order))
                .toList();

        return TimelineResponse.builder()
                .userId(userId)
                .posts(postDtos)
                .build();
    }

    private void requireSeller(Long authUserId, Set<RoleName> authRoles, Long reqUserId) {
        if (reqUserId == null || !reqUserId.equals(authUserId)) {
            throw new ForbiddenException("You can only publish as yourself");
        }
        if (authRoles == null || !authRoles.contains(RoleName.SELLER)) {
            throw new ForbiddenException("Only SELLER can publish products");
        }
    }

    private Product validateProductOwnership(Long authUserId, Long productId) {
        Product product = productRepository.findByIdAndActiveTrue(productId)
                .orElseThrow(() -> new NotFoundException("Product not found"));

        if (!product.getSeller().getId().equals(authUserId)) {
            throw new ForbiddenException("You can only publish your own products");
        }

        return product;
    }

    private Post buildPost(Long authUserId, java.time.LocalDate date, Product product, 
                          Long categoryId, boolean hasPromo, BigDecimal discount) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Category not found"));

        return Post.builder()
                .userId(authUserId)
                .date(date)
                .product(PostProduct.builder()
                        .productId(product.getId())
                        .productName(product.getName())
                        .type(product.getType())
                        .brand(product.getBrand())
                        .color(product.getColor())
                        .notes(null)
                        .build())
                .categoryId(category.getId().intValue())
                .categoryName(category.getName())
                .price(product.getPrice())
                .hasPromo(hasPromo)
                .discount(discount)
                .build();
    }

    private PostDto mapToPostDto(Post post) {
        return PostDto.builder()
                .userId(post.getUserId())
                .postId(post.getId())
                .date(post.getDate())
                .product(PostProductDto.builder()
                        .productId(post.getProduct().getProductId())
                        .productName(post.getProduct().getProductName())
                        .type(post.getProduct().getType())
                        .brand(post.getProduct().getBrand())
                        .color(post.getProduct().getColor())
                        .notes(post.getProduct().getNotes())
                        .build())
                .category(post.getCategoryId())
                .price(post.getPrice())
                .hasPromo(post.getHasPromo())
                .discount(post.getDiscount())
                .build();
    }

    private Comparator<PostDto> resolveDateSort(String order) {
        if (order == null || order.isBlank()) {
            return Comparator.comparing(PostDto::getDate).reversed();
        }

        return switch (order.toLowerCase()) {
            case "date_asc" -> Comparator.comparing(PostDto::getDate);
            case "date_desc" -> Comparator.comparing(PostDto::getDate).reversed();
            default -> throw new IllegalArgumentException("Invalid order parameter. Use 'date_asc' or 'date_desc'");
        };
    }
}
