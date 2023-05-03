package com.mshoes.mshoes.controllers.admin;

import com.mshoes.mshoes.utils.GetUserFromToken;
import com.mshoes.mshoes.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AOrderController {
    private final GetUserFromToken getUserFromToken;

    private final JwtUtils jwtUtils;

    @Autowired
    public AOrderController(GetUserFromToken getUserFromToken, JwtUtils jwtUtils) {
        this.getUserFromToken = getUserFromToken;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping("/order")
    public String getAllOrders(){
        return "admin/orders";
    }
}
