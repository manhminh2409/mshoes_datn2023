package com.mshoes.mshoes.controllers;

import com.mshoes.mshoes.mapper.UserMapper;
import com.mshoes.mshoes.models.dtos.UserDTO;
import com.mshoes.mshoes.models.request.CheckOutRequest;
import com.mshoes.mshoes.models.request.OrderDetailRequest;
import com.mshoes.mshoes.models.request.OrderItemRequest;
import com.mshoes.mshoes.models.response.OrderDetailResponse;
import com.mshoes.mshoes.models.response.OrderItemResponse;
import com.mshoes.mshoes.models.response.UserResponse;
import com.mshoes.mshoes.services.OrderDetailService;
import com.mshoes.mshoes.services.UserService;
import com.mshoes.mshoes.utils.GetUserFromToken;
import com.mshoes.mshoes.utils.JwtUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.text.ParseException;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/home/cart")
public class CartController {
    private final OrderDetailService orderDetailService;

    private final GetUserFromToken getUserFromToken;
    private final UserService userService;
    private final UserMapper userMapper;
    private final JwtUtils jwtUtils;
    public CartController(OrderDetailService orderDetailService, GetUserFromToken getUserFromToken, UserService userService, UserMapper userMapper, JwtUtils jwtUtils) {
        this.orderDetailService = orderDetailService;
        this.getUserFromToken = getUserFromToken;
        this.userService = userService;
        this.userMapper = userMapper;
        this.jwtUtils = jwtUtils;
    }
    @ModelAttribute("userLogined")
    public UserResponse getUserLogined(ModelMap model, HttpServletRequest request) {
        return getUserFromToken.getUserFromToken(request);
    }
    @ModelAttribute("cartId")
    public Long getOrderDetailId(HttpServletRequest request){
        UserResponse userResponse = getUserFromToken.getUserFromToken(request);
        OrderDetailResponse orderDetailResponse = orderDetailService.getCartByUserAndType(userResponse.getId(), 0);
        if (orderDetailResponse == null){
            return null;
        }else {
            return orderDetailService.getCartByUserAndType(userResponse.getId(), 0).getId();
        }
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
    @ModelAttribute("carts")
    public List<OrderItemResponse> getAllItemFromCart(HttpServletRequest request){
        // Lấy id người dùng
        String token = jwtUtils.getTokenLoginFromCookie(request);
        //Lấy id người dùng
        Long userId = jwtUtils.getUserIdFromToken(token);
        return orderDetailService.getItemFromCart(userId);
    }
    @GetMapping
    public String viewCart(HttpServletRequest request, ModelMap model){
        return "web/cart";
    }

    @PostMapping("/addtocart")
    public String addToCart(ModelMap model, HttpServletRequest request,
                            @ModelAttribute("OrderItemRequest") OrderItemRequest orderItemRequest){
        //Lấy thông tin user
        String token = jwtUtils.getTokenLoginFromCookie(request);
        Long userId = jwtUtils.getUserIdFromToken(token);
        orderDetailService.addToOrder(userId,orderItemRequest);
        return "redirect:/home/cart";
    }
    //Xử lý tăng, giảm số lượng sản phẩm
    @PutMapping("/{id}")
    public String operateItem(ModelMap model,@PathVariable("id") Long cartItemId ,
                              @RequestParam("o") String operation){
        orderDetailService.plusOrMinusQuantity(cartItemId,operation);
        return "redirect:/home/cart";
    }
    @PersistenceContext
    EntityManager entityManager;
    @DeleteMapping("/delete/{id}")
    @Transactional
    public String removeFromCart(@PathVariable("id") Long itemId, ModelMap model) {
        Query query = entityManager.createQuery("DELETE FROM OrderItem oi WHERE oi.id = :id");
        query.setParameter("id", itemId);
        query.executeUpdate();
        return "redirect:/home/cart";
    }

    @GetMapping("/checkout")
    public String getCheckOut(ModelMap model, HttpServletRequest request,
                           @RequestParam("id") long orderDetailId,
                           @RequestParam("totalQuantity") String totalQuantity,
                           @RequestParam("totalAmount") String totalAmount) throws ParseException {
        //Lấy thông tin người dùng đang đăng nhập
        String token = jwtUtils.getTokenLoginFromCookie(request);
        Long userId = jwtUtils.getUserIdFromToken(token);
        UserDTO user = userService.getUserById(userId);
        model.addAttribute("userCheckOut",user);

        OrderDetailRequest orderDetailRequest = new OrderDetailRequest();
        orderDetailRequest.setId(orderDetailId);
        orderDetailRequest.setTotalQuantity(Integer.parseInt(totalQuantity));
        orderDetailRequest.setTotalAmount(Integer.parseInt(totalAmount.replace(".", "").replace("đ", "")));
        model.addAttribute("orderDetailRequest", orderDetailRequest);

        return "web/checkout";
    }

    @PutMapping("/checkout")
    public String checkOut(ModelMap model, HttpServletRequest request,
                           @ModelAttribute("checkOutRequest")CheckOutRequest checkOutRequest){
        try{
            String token = jwtUtils.getTokenLoginFromCookie(request);
            Long userId = jwtUtils.getUserIdFromToken(token);

            OrderDetailRequest orderDetailRequest = new OrderDetailRequest();
            orderDetailRequest.setId(checkOutRequest.getId());
            orderDetailRequest.setTotalQuantity(checkOutRequest.getTotalQuantity());
            orderDetailRequest.setTotalAmount(checkOutRequest.getTotalAmount());
            orderDetailRequest.setPhone(checkOutRequest.getPhone());
            orderDetailRequest.setNotes(checkOutRequest.getNotes());
            orderDetailRequest.setAddress(checkOutRequest.getAddress() +"; "+ checkOutRequest.getWard() + ", " + checkOutRequest.getDistrict()+", "+ checkOutRequest.getCity());
            if(Objects.equals(checkOutRequest.getPaymentMethod(), "cod")){
                orderDetailRequest.setPaymentId((long)1);
            }else if (Objects.equals(checkOutRequest.getPaymentMethod(), "bankTransfer")){
                orderDetailRequest.setPaymentId((long)2);
            }
            OrderDetailResponse orderDetailResponse = orderDetailService.checkOut(userId,orderDetailRequest);
            return "redirect:/home/user";
        }catch (Exception e){
            return null;
        }

    }

}

