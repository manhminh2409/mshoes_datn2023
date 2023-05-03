package com.mshoes.mshoes.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class LogoutController {

//    @GetMapping("/logout")
//    public String logout(HttpServletRequest request, HttpServletResponse response) {
//        Cookie[] cookies = request.getCookies(); // Lấy danh sách cookie từ request
//        if (cookies != null) {
//            for (Cookie cookie : cookies) {
//                if (cookie.getName().equals("jwtToken")) { // Tìm kiếm cookie có tên là "jwtToken"
//                    cookie.setValue(null); // Xóa giá trị của cookie
//                    cookie.setMaxAge(0); // Đặt thời gian sống của cookie về 0
//                    cookie.setPath("/"); // Đặt path của cookie về "/"
//                    response.addCookie(cookie); // Thêm cookie đã chỉnh sửa vào response
//                    break;
//                }
//            }
//        }
//        return "index";
//    }
}
