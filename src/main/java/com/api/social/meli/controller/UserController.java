package com.api.social.meli.controller;

import com.api.social.meli.config.AuthUserInterceptor;
import com.api.social.meli.dto.user.*;
import com.api.social.meli.service.FollowService;
import com.api.social.meli.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final FollowService followService;

    @PostMapping("/{id}/activate-seller")
    public ActivateSellerResponse activateSeller(@PathVariable("id") Long id, HttpServletRequest request) {
        Long authUserId = (Long) request.getAttribute(AuthUserInterceptor.REQ_ATTR_AUTH_USER_ID);
        return userService.activateSeller(authUserId, id);
    }

    @PostMapping("/{userId}/follow/{userIdToFollow}")
    @ResponseStatus(HttpStatus.OK)
    public void followUser(
            @PathVariable("userId") Long userId,
            @PathVariable("userIdToFollow") Long userIdToFollow,
            HttpServletRequest request) {
        Long authUserId = (Long) request.getAttribute(AuthUserInterceptor.REQ_ATTR_AUTH_USER_ID);
        if (!authUserId.equals(userId)) {
            throw new IllegalArgumentException("You can only follow users as yourself");
        }
        followService.followUser(userId, userIdToFollow);
    }

    @PostMapping("/{userId}/unfollow/{userIdToUnfollow}")
    @ResponseStatus(HttpStatus.OK)
    public void unfollowUser(
            @PathVariable("userId") Long userId,
            @PathVariable("userIdToUnfollow") Long userIdToUnfollow,
            HttpServletRequest request) {
        Long authUserId = (Long) request.getAttribute(AuthUserInterceptor.REQ_ATTR_AUTH_USER_ID);
        if (!authUserId.equals(userId)) {
            throw new IllegalArgumentException("You can only unfollow users as yourself");
        }
        followService.unfollowUser(userId, userIdToUnfollow);
    }

    @GetMapping("/{userId}/followers/count")
    public FollowerCountResponse getFollowersCount(@PathVariable("userId") Long userId) {
        return followService.getFollowersCount(userId);
    }

    @GetMapping("/{userId}/followers/list")
    public FollowerListResponse getFollowersList(
            @PathVariable("userId") Long userId,
            @RequestParam(value = "order", required = false) String order) {
        return followService.getFollowersList(userId, order);
    }

    @GetMapping("/{userId}/followed/list")
    public FollowedListResponse getFollowedList(
            @PathVariable("userId") Long userId,
            @RequestParam(value = "order", required = false) String order) {
        return followService.getFollowedList(userId, order);
    }
}