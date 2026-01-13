package com.api.social.meli.controller;

import com.api.social.meli.config.AuthUserInterceptor;
import com.api.social.meli.dto.user.ActivateSellerResponse;
import com.api.social.meli.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/{id}/activate-seller")
    public ActivateSellerResponse activateSeller(@PathVariable("id") Long id, HttpServletRequest request) {
        Long authUserId = (Long) request.getAttribute(AuthUserInterceptor.REQ_ATTR_AUTH_USER_ID);
        return userService.activateSeller(authUserId, id);
    }
}