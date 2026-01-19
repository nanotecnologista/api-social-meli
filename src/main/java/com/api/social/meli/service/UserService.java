package com.api.social.meli.service;

import com.api.social.meli.dto.post.PostDto;
import com.api.social.meli.dto.post.PostProductDto;
import com.api.social.meli.dto.user.ActivateSellerResponse;
import com.api.social.meli.dto.user.SellerDto;
import com.api.social.meli.dto.user.SellerListResponse;
import com.api.social.meli.dto.user.SellerProfileResponse;
import com.api.social.meli.dto.user.UserProfileResponse;
import com.api.social.meli.exception.NotFoundException;
import com.api.social.meli.model.mysql.Role;
import com.api.social.meli.model.mysql.RoleName;
import com.api.social.meli.model.mysql.User;
import com.api.social.meli.model.mysql.UserRole;
import com.api.social.meli.model.mongo.Post;
import com.api.social.meli.repository.mongo.PostRepository;
import com.api.social.meli.repository.mysql.FollowRepository;
import com.api.social.meli.repository.mysql.RoleRepository;
import com.api.social.meli.repository.mysql.UserRepository;
import com.api.social.meli.repository.mysql.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final FollowRepository followRepository;
    private final PostRepository postRepository;

    public ActivateSellerResponse activateSeller(Long authUserId, Long userIdToActivate) {
        if (!authUserId.equals(userIdToActivate)) {
            throw new IllegalArgumentException("you can only activate seller for yourself");
        }

        if (userRoleRepository.findRolesByUserId(userIdToActivate).contains(RoleName.SELLER)) {
            throw new IllegalArgumentException("user is already a seller");
        }

        User user = userRepository.findById(userIdToActivate)
                .orElseThrow(() -> new IllegalArgumentException("user not found"));

        Role sellerRole = roleRepository.findByName(RoleName.SELLER)
                .orElseThrow(() -> new IllegalStateException("Role SELLER not found in database"));


        // adiciona SELLER
        userRoleRepository.save(UserRole.builder().user(user).role(sellerRole).build());

        Set<String> roles = userRoleRepository.findRolesByUserId(user.getId())
                .stream()
                .map(Enum::name)
                .collect(Collectors.toSet());

        return ActivateSellerResponse.builder()
                .userId(user.getId())
                .name(user.getName())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .roles(roles)
                .followersCount(user.getFollowersCount())
                .followingCount(user.getFollowingCount())
                .build();
    }

    public UserProfileResponse getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("user not found"));

        Set<String> roles = userRoleRepository.findRolesByUserId(userId)
                .stream()
                .map(Enum::name)
                .collect(Collectors.toSet());

        return UserProfileResponse.builder()
                .userId(user.getId())
                .name(user.getName())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .roles(roles)
                .followersCount(user.getFollowersCount())
                .followingCount(user.getFollowingCount())
                .build();
    }

    public SellerListResponse getAllSellers(Long excludeFollowedByUserId, int page, int size) {
        List<User> sellers = userRoleRepository.findUsersByRoleName(RoleName.SELLER);

        List<Long> followedSellerIds = List.of();
        if (excludeFollowedByUserId != null) {
            followedSellerIds = followRepository.findSellerIdsByUserId(excludeFollowedByUserId);
        }

        final List<Long> excludeIds = followedSellerIds;
        List<SellerDto> allSellerDtos = sellers.stream()
                .filter(user -> !excludeIds.contains(user.getId()))
                .map(user -> SellerDto.builder()
                        .userId(user.getId())
                        .userName(user.getNickname())
                        .followersCount(user.getFollowersCount())
                        .build())
                .collect(Collectors.toList());

        long total = allSellerDtos.size();
        int start = page * size;
        int end = Math.min(start + size, allSellerDtos.size());

        List<SellerDto> paginatedSellers = start < allSellerDtos.size() 
                ? allSellerDtos.subList(start, end) 
                : List.of();

        return SellerListResponse.builder()
                .total(total)
                .page(page)
                .size(size)
                .sellers(paginatedSellers)
                .build();
    }

    public SellerProfileResponse getSellerProfile(Long sellerId, int page, int size) {
        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new NotFoundException("Seller not found"));

        Set<RoleName> roles = userRoleRepository.findRolesByUserId(sellerId);
        if (!roles.contains(RoleName.SELLER)) {
            throw new IllegalArgumentException("User is not a seller");
        }

        long totalPosts = postRepository.countByUserId(sellerId);

        LocalDate fourteenDaysAgo = LocalDate.now().minusDays(14);
        long activePostsCount = postRepository.countByUserIdAndDateGreaterThanEqual(sellerId, fourteenDaysAgo);

        Pageable pageable = PageRequest.of(page, size);
        Page<Post> postsPage = postRepository.findByUserIdAndDateGreaterThanEqual(sellerId, fourteenDaysAgo, pageable);

        List<PostDto> postDtos = postsPage.getContent().stream()
                .map(this::mapToPostDto)
                .collect(Collectors.toList());

        return SellerProfileResponse.builder()
                .userId(seller.getId())
                .name(seller.getName())
                .userName(seller.getNickname())
                .followersCount(seller.getFollowersCount())
                .followingCount(seller.getFollowingCount())
                .totalPosts(totalPosts)
                .activePostsCount(activePostsCount)
                .page(page)
                .size(size)
                .posts(postDtos)
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
}