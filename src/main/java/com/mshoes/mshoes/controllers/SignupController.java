package com.mshoes.mshoes.controllers;

import com.mshoes.mshoes.models.dtos.UserDTO;
import com.mshoes.mshoes.models.request.SignupRequest;
import com.mshoes.mshoes.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping
public class SignupController {

    @Autowired
    private final UserService userService;

    public SignupController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/signup")
    public String signup(){
        return "web/signup";
    }

    @PostMapping("/signup")
    public String signupUser(@Validated @ModelAttribute("signupRequest") SignupRequest signupRequest){
        UserDTO newUser = userService.signupUser(signupRequest);
        if (newUser != null) {
            return "web/login";
        } else {
            return "web/signup";
        }
    }
}
