package com.mshoes.mshoes.controllers.admin;

import com.mshoes.mshoes.models.request.UpdateOrderRequest;
import com.mshoes.mshoes.models.response.OrderDetailResponse;
import com.mshoes.mshoes.models.response.UserResponse;
import com.mshoes.mshoes.services.OrderDetailService;
import com.mshoes.mshoes.utils.GetUserFromToken;
import com.mshoes.mshoes.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AOrderController {
    private final GetUserFromToken getUserFromToken;

    private final JwtUtils jwtUtils;

    private final OrderDetailService orderDetailService;

    @Autowired
    public AOrderController(GetUserFromToken getUserFromToken, JwtUtils jwtUtils, OrderDetailService orderDetailService) {
        this.getUserFromToken = getUserFromToken;
        this.jwtUtils = jwtUtils;
        this.orderDetailService = orderDetailService;
    }

    //Lấy thông tin người dùng
    @ModelAttribute("userLogined")
    public UserResponse getUserLogined(ModelMap model, HttpServletRequest request) {
        return getUserFromToken.getUserFromToken(request);
    }
    @GetMapping("/order")
    public String getAllOrders(ModelMap model, HttpServletRequest request,
                               @RequestParam(defaultValue = "0") int pageNumber,
                               @RequestParam(defaultValue = "10") int pageSize,
                               @RequestParam(defaultValue = "id") String sortBy){
        model.addAttribute("countOrder",orderDetailService.countOrderByType(1));
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("pageSize", pageSize);

        String token = jwtUtils.getTokenLoginFromCookie(request);
        long userId = jwtUtils.getUserIdFromToken(token);

        List<OrderDetailResponse> orderDetailResponses = orderDetailService.getAllOrdersByType1(pageNumber,pageSize,sortBy).getContent();
        model.addAttribute("orders", orderDetailResponses);
        return "admin/orders";
    }

    @GetMapping("/order/detail/{id}")
    public String viewDetailOrder(ModelMap model, HttpServletRequest request, @PathVariable("id") long orderDetailId){
        OrderDetailResponse orderDetailResponse = orderDetailService.getOrderById(orderDetailId);
        model.addAttribute("order", orderDetailResponse);
        return "admin/orderDetail";
    }

    @PutMapping("/order/update/{id}")
    public String updateOrder(ModelMap model, HttpServletRequest request, @PathVariable("id") long orderDetailId,
                              @ModelAttribute("updateOrderRequest")UpdateOrderRequest updateOrderRequest){
        try{
            OrderDetailResponse orderDetailResponse = orderDetailService.updateOrder(orderDetailId,updateOrderRequest);
            return "redirect:/admin/order/detail/"+orderDetailId;
        }catch (Exception e){
            return "redirect:/admin/order/detail/"+orderDetailId+"?err=false";
        }
    }
}
