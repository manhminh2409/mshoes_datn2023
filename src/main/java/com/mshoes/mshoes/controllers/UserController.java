package com.mshoes.mshoes.controllers;

import com.mshoes.mshoes.models.dtos.CategoryDTO;
import com.mshoes.mshoes.models.dtos.UserDTO;
import com.mshoes.mshoes.models.request.ProfileRequest;
import com.mshoes.mshoes.models.response.OrderDetailResponse;
import com.mshoes.mshoes.models.response.UserResponse;
import com.mshoes.mshoes.services.CategoryService;
import com.mshoes.mshoes.services.OrderDetailService;
import com.mshoes.mshoes.services.UserService;
import com.mshoes.mshoes.utils.GetUserFromToken;
import com.mshoes.mshoes.utils.JwtUtils;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/home/user")
public class UserController {
    private static final int  TYPE_ORDER = 1;
    private static final int  STATUS_PENDING_APPROVAL = 0;
    private static final int  STATUS_APPROVED = 1;
    private static final int  STATUS_DELIVERING = 2;
    private static final int  STATUS_DELIVERED = 3;
    private static final int  STATUS_CANCELED = 4;
    private final UserService userService;
    private final OrderDetailService orderDetailService;
    private final GetUserFromToken getUserFromToken;
    private final JwtUtils jwtUtils;
    private final CategoryService categoryService;

    public UserController(UserService userService, OrderDetailService orderDetailService, GetUserFromToken getUserFromToken, JwtUtils jwtUtils, CategoryService categoryService) {
        this.userService = userService;
        this.orderDetailService = orderDetailService;
        this.getUserFromToken = getUserFromToken;
        this.jwtUtils = jwtUtils;
        this.categoryService = categoryService;
    }
    @ModelAttribute("userLogined")
    public UserResponse getUserLogined(ModelMap model, HttpServletRequest request) {
        return getUserFromToken.getUserFromToken(request);
    }
    @ModelAttribute("categories")
    public List<CategoryDTO> getCategories(){
        return categoryService.getAllCategories();
    }
    @ModelAttribute("cartItem")
    public int countCartItem(HttpServletRequest request){
        UserResponse user = getUserFromToken.getUserFromToken(request);
        if (user != null){
            return orderDetailService.countItemFromCart(user.getId());
        }else {
            return 0;
        }
    }
    @ModelAttribute("orderDetails")
    public List<OrderDetailResponse> getAllOrderDetail(HttpServletRequest request){
        String token = jwtUtils.getTokenLoginFromCookie(request);
        Long userId = jwtUtils.getUserIdFromToken(token);
        //Lấy danh dách đơn hàng
        return orderDetailService.getAllOrdersWithType1(userId,TYPE_ORDER);
    }
    @ModelAttribute("orderDetailStatus0")
    public List<OrderDetailResponse> getAllOrderDetailStatus0(HttpServletRequest request){
        String token = jwtUtils.getTokenLoginFromCookie(request);
        Long userId = jwtUtils.getUserIdFromToken(token);
        //Lấy danh dách đơn hàng
        return orderDetailService.getAllOrdersWithType1ByStatus(userId,TYPE_ORDER, STATUS_PENDING_APPROVAL);
    }
    @ModelAttribute("orderDetailStatus1")
    public List<OrderDetailResponse> getAllOrderDetailStatus1(HttpServletRequest request){
        String token = jwtUtils.getTokenLoginFromCookie(request);
        Long userId = jwtUtils.getUserIdFromToken(token);
        //Lấy danh dách đơn hàng
        return orderDetailService.getAllOrdersWithType1ByStatus(userId,TYPE_ORDER, STATUS_APPROVED);
    }
    @ModelAttribute("orderDetailStatus2")
    public List<OrderDetailResponse> getAllOrderDetailStatus2(HttpServletRequest request){
        String token = jwtUtils.getTokenLoginFromCookie(request);
        Long userId = jwtUtils.getUserIdFromToken(token);
        //Lấy danh dách đơn hàng
        return orderDetailService.getAllOrdersWithType1ByStatus(userId,TYPE_ORDER, STATUS_DELIVERING);
    }
    @ModelAttribute("orderDetailStatus3")
    public List<OrderDetailResponse> getAllOrderDetailStatus3(HttpServletRequest request){
        String token = jwtUtils.getTokenLoginFromCookie(request);
        Long userId = jwtUtils.getUserIdFromToken(token);
        //Lấy danh dách đơn hàng
        return orderDetailService.getAllOrdersWithType1ByStatus(userId,TYPE_ORDER, STATUS_DELIVERED);
    }
    @ModelAttribute("orderDetailStatus4")
    public List<OrderDetailResponse> getAllOrderDetailStatus4(HttpServletRequest request){
        String token = jwtUtils.getTokenLoginFromCookie(request);
        Long userId = jwtUtils.getUserIdFromToken(token);
        //Lấy danh dách đơn hàng
        return orderDetailService.getAllOrdersWithType1ByStatus(userId,TYPE_ORDER, STATUS_CANCELED);
    }

    @GetMapping("/order")
    public String getOrderInformation(ModelMap model, HttpServletRequest request, HttpServletResponse response){
        // Lưu request của trang hiện tại vào session
        HttpSessionRequestCache requestCache = new HttpSessionRequestCache();
        requestCache.saveRequest(request, response);
        return "web/order";
    }

    //Huỷ đơn hàng
    @PutMapping("/cancelOrder/{id}")
    public String cancelOrder(@PathVariable("id") Long orderDetailId, HttpServletRequest request){
        String token = jwtUtils.getTokenLoginFromCookie(request);
        Long userId = jwtUtils.getUserIdFromToken(token);

        OrderDetailResponse orderDetailResponse = orderDetailService.cancelOrder(userId, orderDetailId);
        if(orderDetailResponse != null){
            return "redirect:/home/user";
        }else {
            return "redirect:/home/user?err=false";
        }
    }
    @ModelAttribute("oldUser")
    public UserResponse oldUser( HttpServletRequest request){
        //Lấy thông tin user
        return getUserFromToken.getUserFromToken(request);
    }
    @GetMapping("/profile")
    public String getProfile(ModelMap model, HttpServletRequest request){
        return "web/profile";
    }

    @PutMapping("/profile/change")
    public String editProfile(ModelMap model,
                              HttpServletRequest request,
                              HttpServletResponse response,
                              @ModelAttribute("profileRequest")ProfileRequest profileRequest,
                              RedirectAttributes redirectAttributes) throws IOException {

        //Lấy thông tin user
        UserResponse userResponse = getUserFromToken.getUserFromToken(request);
        UserDTO userDTO = userService.updateUser(profileRequest, userResponse.getId());
        return "redirect:/home/user/profile";
    }

}
