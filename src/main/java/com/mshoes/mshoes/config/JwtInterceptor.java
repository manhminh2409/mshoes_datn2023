package com.mshoes.mshoes.config;

import com.mshoes.mshoes.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class JwtInterceptor implements HandlerInterceptor {


    private final JwtUtils jwtUtils;
    @Autowired
    public JwtInterceptor(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = jwtUtils.getTokenLoginFromCookie(request); // lấy JWT token login từ request

        if (token == null) { // kiểm tra token đã hết hạn chưa
            response.sendRedirect("/login"); // chuyển hướng đến trang đăng nhập
            return false;
        }

        return true;
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        // lấy JWT token từ header của request
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
