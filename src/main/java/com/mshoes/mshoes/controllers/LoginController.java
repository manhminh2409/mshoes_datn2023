package com.mshoes.mshoes.controllers;

import com.mshoes.mshoes.models.dtos.RoleDTO;
import com.mshoes.mshoes.models.request.LoginRequest;
import com.mshoes.mshoes.models.response.UserResponse;
import com.mshoes.mshoes.repositories.UserRepository;
import com.mshoes.mshoes.securities.JwtConfig;
import com.mshoes.mshoes.services.UserService;
import com.mshoes.mshoes.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping
public class LoginController {
    private final AuthenticationManager authenticationManager;

    private final UserService userService;

    private final UserRepository userRepository;

    private final JwtConfig jwtConfig;

    private final JwtUtils jwtUtils;

    @Autowired
    public LoginController(AuthenticationManager authenticationManager, UserService userService, UserRepository userRepository, JwtConfig jwtConfig, JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.userRepository = userRepository;
        this.jwtConfig = jwtConfig;
        this.jwtUtils = jwtUtils;
    }


    @GetMapping("/login")
    public String login(ModelMap model, HttpServletRequest request, @RequestParam(value = "message", defaultValue = "0") String message){
        model.addAttribute("message", message);

        //check lại token đăng nhập từ cookie
        String checkToken = jwtUtils.getTokenLoginFromCookie(request);

        //Nếu token đăng nhập còn thời hạn, lấy thông tin ngươ dùng và điều hướng
        if(checkToken != null){
            String username = jwtConfig.getUsernameFromJWT(checkToken);

            //Lấy thông tin người dùng
            // kiểm tra lại role của người dùng đăng nhập và điều hướng
            List<RoleDTO> roleDTOS = userService.getRoleByUsername(username);
            RoleDTO roleDTO = null;

            if(roleDTOS.size() == 1){
                roleDTO = roleDTOS.get(0);
            }
            assert roleDTO != null;
            //Lấy thông tin của người sử dụng
            model.addAttribute("userLogined",userService.getUserByUsername(username));

            if(Objects.equals(roleDTO.getName(), "ROLE_ADMIN")){
                return "admin/index";
            }else {
                return "web/index";
            }
        }else {
            return "web/login";
        }

    }

    @PostMapping("/login")
    public String authenticateUser(ModelMap model, @Validated @ModelAttribute("loginRequest") LoginRequest loginRequest, HttpServletRequest request, HttpServletResponse response){
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(), loginRequest.getPassword()));

            UserResponse userResponse = userService.getUserByUsername(loginRequest.getUsername());

            if (userResponse.getStatus() == 0) {
                return "redirect:/login?message=locked";
            } else {
                SecurityContextHolder.getContext().setAuthentication(authentication);

                //get token form tokenProvider
                String token = jwtConfig.generateToken(authentication);

                //set JWT in cookie
                addAuthentication(response, token);

                // kiểm tra lại role của người dùng đăng nhập và điều hướng
                List<RoleDTO> roleDTOS = userService.getRoleByUsername(loginRequest.getUsername());
                RoleDTO roleDTO = null;
                //Lấy thông tin của người sử dụng
                model.addAttribute("userLogined", userResponse);

                if (roleDTOS.size() == 1) {
                    roleDTO = roleDTOS.get(0);
                }
                assert roleDTO != null;

                // Lấy thông tin trang mà người dùng đã truy cập trước đó
                SavedRequest savedRequest = new HttpSessionRequestCache().getRequest(request, response);
                if (savedRequest != null) {
                    if (Objects.equals(roleDTO.getName(), "ROLE_ADMIN")) {
                        return "admin/index";
                    } else {
                        String targetUrl = savedRequest.getRedirectUrl();
                        if (targetUrl != null) {
                            // Chuyển hướng người dùng trở lại trang đã truy cập trước đó
                            return "redirect:" + targetUrl;
                        }
                    }

                } else {
                    if (Objects.equals(roleDTO.getName(), "ROLE_ADMIN")) {
                        return "redirect:/admin";
                    } else {
                        return "redirect:/";
                    }
                }
                return "redirect:/";
            }
        }catch (Exception e){
            return "redirect:/login?message=false";
        }
    }
    private void addAuthentication(HttpServletResponse response, String jwtToken) {
        // Set cookie value
        Cookie cookie = new Cookie("jwtToken", jwtToken);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(3600);
        response.addCookie(cookie);
    }
}
