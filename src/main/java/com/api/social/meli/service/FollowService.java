package com.api.social.meli.service;

import com.api.social.meli.dto.user.*;
import com.api.social.meli.model.mysql.Follow;
import com.api.social.meli.model.mysql.RoleName;
import com.api.social.meli.model.mysql.User;
import com.api.social.meli.repository.mysql.FollowRepository;
import com.api.social.meli.repository.mysql.UserRepository;
import com.api.social.meli.repository.mysql.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
    public FollowerListResponse getFollowersList(Long userId, String order) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<Follow> follows = followRepository.findBySellerId(userId);

        List<UserBasicDto> followers = follows.stream()
                .map(follow -> UserBasicDto.builder()
                        .userId(follow.getUser().getId())
                        .userName(follow.getUser().getNickname())
                        .build())
                .collect(Collectors.toList());

        followers = applySorting(followers, order);

        return FollowerListResponse.builder()
                .userId(user.getId())
                .userName(user.getNickname())
                .followers(followers)
                .build();
    }

    @Transactional(readOnly = true)
    public FollowedListResponse getFollowedList(Long userId, String order) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<Follow> follows = followRepository.findByUserId(userId);

        List<UserBasicDto> followed = follows.stream()
                .map(follow -> UserBasicDto.builder()
                        .userId(follow.getSeller().getId())
                        .userName(follow.getSeller().getNickname())
                        .build())
                .collect(Collectors.toList());

        followed = applySorting(followed, order);

        return FollowedListResponse.builder()
                .userId(user.getId())
                .userName(user.getNickname())
                .followed(followed)
                .build();
    }

    private List<UserBasicDto> applySorting(List<UserBasicDto> users, String order) {
        if (order == null || order.isEmpty()) {
            return users;
        }

        return switch (order.toLowerCase()) {
            case "name_asc" -> users.stream()
                    .sorted(Comparator.comparing(UserBasicDto::getUserName))
                    .collect(Collectors.toList());
            case "name_desc" -> users.stream()
                    .sorted(Comparator.comparing(UserBasicDto::getUserName).reversed())
                    .collect(Collectors.toList());
            default -> throw new IllegalArgumentException("Invalid order parameter. Use 'name_asc' or 'name_desc'");
        };
    }
}
