package com.api.social.meli.service;

import com.api.social.meli.dto.common.PaginationParams;
import com.api.social.meli.dto.user.*;
import com.api.social.meli.model.mysql.Follow;
import com.api.social.meli.model.mysql.RoleName;
import com.api.social.meli.model.mysql.User;
import com.api.social.meli.repository.mysql.FollowRepository;
import com.api.social.meli.repository.mysql.UserRepository;
import com.api.social.meli.repository.mysql.UserRoleRepository;
import com.api.social.meli.util.PaginationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;

    @Transactional
    public void followUser(Long userId, Long userIdToFollow) {
        if (userId.equals(userIdToFollow)) {
            throw new IllegalArgumentException("User cannot follow themselves");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        User seller = userRepository.findById(userIdToFollow)
                .orElseThrow(() -> new IllegalArgumentException("User to follow not found"));

        boolean followerIsSeller = userRoleRepository.findRolesByUserId(userId).contains(RoleName.SELLER);
        boolean targetIsSeller = userRoleRepository.findRolesByUserId(userIdToFollow).contains(RoleName.SELLER);
        if (followerIsSeller && !targetIsSeller) {
            throw new IllegalArgumentException("Seller cannot follow a customer-only user");
        }

        if (followRepository.existsByUserIdAndSellerId(userId, userIdToFollow)) {
            throw new IllegalArgumentException("User is already following this seller");
        }

        Follow follow = Follow.builder()
                .user(user)
                .seller(seller)
                .build();

        followRepository.save(follow);

        seller.addFollower();
        user.addFollowing();
        userRepository.save(seller);
        userRepository.save(user);
    }

    @Transactional
    public void unfollowUser(Long userId, Long userIdToUnfollow) {
        if (userId.equals(userIdToUnfollow)) {
            throw new IllegalArgumentException("User cannot unfollow themselves");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        User seller = userRepository.findById(userIdToUnfollow)
                .orElseThrow(() -> new IllegalArgumentException("User to unfollow not found"));

        Follow follow = followRepository.findByUserIdAndSellerId(userId, userIdToUnfollow)
                .orElseThrow(() -> new IllegalArgumentException("User is not following this seller"));

        followRepository.delete(follow);

        seller.removeFollower();
        user.removeFollowing();
        userRepository.save(seller);
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public FollowerCountResponse getFollowersCount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        long count = followRepository.countBySellerId(userId);

        return FollowerCountResponse.builder()
                .userId(user.getId())
                .userName(user.getNickname())
                .followersCount(count)
                .build();
    }

    @Transactional(readOnly = true)
    public FollowerListResponse getFollowersList(Long userId, PaginationParams pagination) {
        return getFollowersList(
                userId,
                pagination != null ? pagination.getOrder() : null,
                pagination != null ? pagination.getPage() : null,
                pagination != null ? pagination.getSize() : null,
                pagination != null ? pagination.getOffset() : null,
                pagination != null ? pagination.getLimit() : null
        );
    }

    @Transactional(readOnly = true)
    public FollowedListResponse getFollowedList(Long userId, PaginationParams pagination) {
        return getFollowedList(
                userId,
                pagination != null ? pagination.getOrder() : null,
                pagination != null ? pagination.getPage() : null,
                pagination != null ? pagination.getSize() : null,
                pagination != null ? pagination.getOffset() : null,
                pagination != null ? pagination.getLimit() : null
        );
    }

    @Transactional(readOnly = true)
    public FollowerListResponse getFollowersList(Long userId, String order, Integer page, Integer size, Integer offset, Integer limit) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Pageable pageable = PaginationUtils.resolvePageable(page, size, offset, limit, resolveSort(order, "user.nickname"));
        Page<Follow> followsPage = followRepository.findBySellerId(userId, pageable);

        List<UserBasicDto> followers = followsPage.getContent().stream()
                .map(follow -> UserBasicDto.builder()
                        .userId(follow.getUser().getId())
                        .userName(follow.getUser().getNickname())
                        .build())
                .toList();

        return FollowerListResponse.builder()
                .userId(user.getId())
                .userName(user.getNickname())
                .total(followsPage.getTotalElements())
                .page(followsPage.getNumber())
                .size(followsPage.getSize())
                .followers(followers)
                .build();
    }

    @Transactional(readOnly = true)
    public FollowedListResponse getFollowedList(Long userId, String order, Integer page, Integer size, Integer offset, Integer limit) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Pageable pageable = PaginationUtils.resolvePageable(page, size, offset, limit, resolveSort(order, "seller.nickname"));
        Page<Follow> followsPage = followRepository.findByUserId(userId, pageable);

        List<UserBasicDto> followed = followsPage.getContent().stream()
                .map(follow -> UserBasicDto.builder()
                        .userId(follow.getSeller().getId())
                        .userName(follow.getSeller().getNickname())
                        .build())
                .toList();

        return FollowedListResponse.builder()
                .userId(user.getId())
                .userName(user.getNickname())
                .total(followsPage.getTotalElements())
                .page(followsPage.getNumber())
                .size(followsPage.getSize())
                .followed(followed)
                .build();
    }

    private Sort resolveSort(String order, String defaultProperty) {
        if (order == null || order.isBlank()) {
            return Sort.by(defaultProperty).ascending();
        }

        return switch (order.toLowerCase()) {
            case "name_asc" -> Sort.by(defaultProperty).ascending();
            case "name_desc" -> Sort.by(defaultProperty).descending();
            default -> throw new IllegalArgumentException("Invalid order parameter. Use 'name_asc' or 'name_desc'");
        };
    }
}
