package com.api.social.meli.config;

import com.api.social.meli.repository.mysql.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class AuthUserInterceptor implements HandlerInterceptor {

    public static final String HEADER_USER_ID = "X-User-Id";
    public static final String REQ_ATTR_AUTH_USER_ID = "authUserId";

    private final UserRepository userRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        String ctx = request.getContextPath();

        String path = uri;
        if (ctx != null && !ctx.isBlank() && uri != null && uri.startsWith(ctx)) {
            path = uri.substring(ctx.length());
        }

        if (path != null && (path.equals("/auth") || path.startsWith("/auth/"))) {
            return true;
        }
        String userIdHeader = request.getHeader(HEADER_USER_ID);

        if (userIdHeader == null || userIdHeader.isBlank()) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }

        long userId;
        try {
            userId = Long.parseLong(userIdHeader);
        } catch (NumberFormatException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.getWriter().write("Invalid user id");
            return false;
        }

        if (userId <= 0) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.getWriter().write("Invalid user id");
            return false;
        }

        if (!userRepository.existsById(userId)) {
            response.getWriter().write("User not found");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }

        request.setAttribute(REQ_ATTR_AUTH_USER_ID, userId);
        return true;
    }
}