package com.api.social.meli.controller;

import com.api.social.meli.config.AuthUserInterceptor;
import com.api.social.meli.dto.post.*;
import com.api.social.meli.model.mysql.RoleName;
import com.api.social.meli.service.PostService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping("/publish")
    @ResponseStatus(HttpStatus.OK)
    public void publish(@Valid @RequestBody PostPublishRequest req, HttpServletRequest request) {
        Long authUserId = (Long) request.getAttribute(AuthUserInterceptor.REQ_ATTR_AUTH_USER_ID);
        Set<RoleName> roles = extractRoles(request);
        postService.publish(authUserId, roles, req);
    }

    @PostMapping("/promo-pub")
    @ResponseStatus(HttpStatus.OK)
    public void promoPublish(@Valid @RequestBody PostPromoPublishRequest req, HttpServletRequest request) {
        Long authUserId = (Long) request.getAttribute(AuthUserInterceptor.REQ_ATTR_AUTH_USER_ID);
        Set<RoleName> roles = extractRoles(request);
        postService.publishPromo(authUserId, roles, req);
    }

    @GetMapping("/promo-pub/count")
    public PromoCountResponse promoCount(@RequestParam("user_id") Long userId) {
        return postService.getPromoCount(userId);
    }

    @GetMapping("/promo-pub/list")
    public PromoListResponse promoList(@RequestParam("user_id") Long userId) {
        return postService.getPromoList(userId);
    }

    @GetMapping("/followed/{userId}/list")
    public TimelineResponse followedTimeline(
            @PathVariable Long userId,
            @RequestParam(value = "order", required = false) String order) {
        return postService.getFollowedTimeline(userId, order);
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
