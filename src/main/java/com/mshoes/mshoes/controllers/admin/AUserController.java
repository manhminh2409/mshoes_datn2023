package com.mshoes.mshoes.controllers.admin;

import com.mshoes.mshoes.models.dtos.UserDTO;
import com.mshoes.mshoes.models.request.UserRequest;
import com.mshoes.mshoes.models.response.UserResponse;
import com.mshoes.mshoes.services.UserService;
import com.mshoes.mshoes.utils.GetUserFromToken;
import com.mshoes.mshoes.utils.JwtUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/admin/user")
public class AUserController {
    private final UserService userService;

    private final GetUserFromToken getUserFromToken;

    private final JwtUtils jwtUtils;

    public AUserController(UserService userService, GetUserFromToken getUserFromToken, JwtUtils jwtUtils) {
        this.userService = userService;
        this.getUserFromToken = getUserFromToken;
        this.jwtUtils = jwtUtils;
    }
    @ModelAttribute(value = "userLogined")
    public UserResponse getUserLogined(HttpServletRequest request){
        return getUserFromToken.getUserFromToken(request);
    }

    @GetMapping("")
    public String viewUsers(ModelMap model, HttpServletRequest request,
                            @RequestParam(value = "pageNumber",defaultValue = "0") int pageNumber,
                            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy){
        model.addAttribute("countUser",userService.countUser());
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("pageSize", pageSize);
        //Lấy danh sách người dùng
        Page<UserResponse> userResponses = userService.getUsers(pageNumber,pageSize,sortBy);
        List<UserResponse> userResponses1 = userResponses.getContent();
        model.addAttribute("users", userResponses1);
        return "admin/users";
    }
    @GetMapping("/create")
    public String viewCreateUser(){
        return "admin/newUser";
    }

    @PostMapping("/create")
    public String createUser(ModelMap model, HttpServletRequest request,
                             @ModelAttribute("userRequest")UserRequest userRequest) throws IOException {
        try {
            UserDTO userDTO = userService.createUser(userRequest);
            if (userDTO != null){
                return "redirect:/admin/user";
            }else {
                return "redirect:/admin/user/create?err=false";
            }
        }catch (Exception e){
            return "redirect:/admin/user/create?err=false";
        }

    }

    //Xem thông tin chi tiết 1 người sử dụng
    @GetMapping("/detail/{id}")
    public  String detailUser(ModelMap model,
                              @PathVariable("id") Long userId,
                              @RequestParam(value = "err",defaultValue = "0") String error){
        model.addAttribute("error", error);
        //Lấy thông tin người dùng không
        try {
            UserResponse userResponse = userService.getUser(userId);
            if(userResponse!= null){
                model.addAttribute("userDetail",userResponse);
                return "admin/userDetail";
            }else {
                return "redirect:/admin/user?err=false";
            }
        }catch (Exception e){
            return "redirect:/admin/user?err=false";
        }
    }

    @PutMapping("/update/{id}")
    public String updateUser(ModelMap model, @PathVariable("id") Long userId,
                             @ModelAttribute("userRequest") UserRequest userRequest){
        try {
            UserResponse userResponse = userService.updateUser1(userId, userRequest);
            return "redirect:/admin/user";
        }catch (Exception e){
            return "redirect:/admin/user/detail/"+userId+"?err=false";
        }
    }
}
