package com.mshoes.mshoes.controllers.admin;

import com.mshoes.mshoes.models.response.UserResponse;
import com.mshoes.mshoes.utils.GetUserFromToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final GetUserFromToken getUserFromToken;

    public AdminController(GetUserFromToken getUserFromToken) {
        this.getUserFromToken = getUserFromToken;
    }

    //Lấy thông tin người dùng
    @ModelAttribute("userLogined")
    public UserResponse getUserLogined(ModelMap model, HttpServletRequest request) {
        return getUserFromToken.getUserFromToken(request);
    }
    @GetMapping
    public String home(){
        return "admin/index";
    }
}
