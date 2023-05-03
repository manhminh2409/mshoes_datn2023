package com.mshoes.mshoes.utils;

import com.mshoes.mshoes.models.response.UserResponse;
import com.mshoes.mshoes.securities.JwtConfig;
import com.mshoes.mshoes.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class GetUserFromToken {
    private final JwtConfig jwtConfig;

    private final JwtUtils jwtUtils;

    private final UserService userService;

    @Autowired
    public GetUserFromToken(JwtConfig jwtConfig, JwtUtils jwtUtils, UserService userService) {
        this.jwtConfig = jwtConfig;
        this.jwtUtils = jwtUtils;
        this.userService = userService;
    }

    public UserResponse getUserFromToken(HttpServletRequest request){
        //check lại token đăng nhập từ cookie
        String checkToken = jwtUtils.getTokenLoginFromCookie(request);
        //Lấy thông tin người dùng
        if (checkToken != null){
            String username = jwtConfig.getUsernameFromJWT(checkToken);
            return userService.getUserByUsername(username);
        } else {
            return null;
        }
    }

    public boolean checkLogin(HttpServletRequest request){
        //Lấy token đăng nhập từ cookie
        String token = jwtUtils.getTokenLoginFromCookie(request);
        return token != null;
    }

}
