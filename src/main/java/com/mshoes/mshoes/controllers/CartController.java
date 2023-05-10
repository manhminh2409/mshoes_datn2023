package com.mshoes.mshoes.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mshoes.mshoes.config.Config;
import com.mshoes.mshoes.models.dtos.CategoryDTO;
import com.mshoes.mshoes.models.dtos.UserDTO;
import com.mshoes.mshoes.models.request.CheckOutRequest;
import com.mshoes.mshoes.models.request.OrderDetailRequest;
import com.mshoes.mshoes.models.request.OrderItemRequest;
import com.mshoes.mshoes.models.response.OrderDetailResponse;
import com.mshoes.mshoes.models.response.OrderItemResponse;
import com.mshoes.mshoes.models.response.UserResponse;
import com.mshoes.mshoes.services.CategoryService;
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
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("/home/cart")
public class CartController {
    private final OrderDetailService orderDetailService;
    private final GetUserFromToken getUserFromToken;
    private final UserService userService;
    private final CategoryService categoryService;
    private final JwtUtils jwtUtils;
    public CartController(OrderDetailService orderDetailService, GetUserFromToken getUserFromToken, UserService userService, CategoryService categoryService, JwtUtils jwtUtils) {
        this.orderDetailService = orderDetailService;
        this.getUserFromToken = getUserFromToken;
        this.userService = userService;
        this.categoryService = categoryService;
        this.jwtUtils = jwtUtils;
    }
    @ModelAttribute("categories")
    public List<CategoryDTO> getCategories(){
        return categoryService.getAllCategories();
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
                              @RequestParam("quantity") String quantity,
                              @RequestParam("amount") String amount) throws ParseException {
        try{
            //Lấy thông tin người dùng đang đăng nhập
            String token = jwtUtils.getTokenLoginFromCookie(request);
            Long userId = jwtUtils.getUserIdFromToken(token);
            UserDTO user = userService.getUserById(userId);
            model.addAttribute("userCheckOut",user);

            OrderDetailRequest orderDetailRequest = new OrderDetailRequest();
            orderDetailRequest.setId(orderDetailId);
            orderDetailRequest.setTotalQuantity(Integer.parseInt(quantity));
            orderDetailRequest.setTotalAmount(Integer.parseInt(amount));
            model.addAttribute("orderDetailRequest", orderDetailRequest);
            return "web/checkout";
        }catch (Exception e){
            return null;
        }

    }
    @GetMapping("/checkout/success")
    public String checkOutSuccess(ModelMap model,
                                  @RequestParam("id") Long id,
                                  @RequestParam("quantity") int quantity,
                                  @RequestParam("phone")String phone,
                                  @RequestParam("address") String address,
                                  @RequestParam("notes")String notes,
                                  @RequestParam("vnp_Amount") int amount,
                                  @RequestParam("vnp_ResponseCode") String vnp_ResponseCode,
                                  @RequestParam("vnp_TxnRef") String vnp_TxnRef,
                                  @RequestParam("vnp_PayDate")String vnp_PayDate) throws ParseException {
        CheckOutRequest checkOutRequest = new CheckOutRequest();
        checkOutRequest.setId(id);
        checkOutRequest.setTotalQuantity(quantity);
        checkOutRequest.setTotalAmount(amount/100);
        checkOutRequest.setAddress( address);
        checkOutRequest.setNotes(notes);
        checkOutRequest.setPhone(phone);

        model.addAttribute("checkOutRequest", checkOutRequest);
        model.addAttribute("vnp_ResponseCode", vnp_ResponseCode);
        model.addAttribute("vnp_TxnRef", vnp_TxnRef);

        DateFormat inputFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = inputFormat.parse(vnp_PayDate);

        DateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        model.addAttribute("vnp_PayDate", outputFormat.format(date));
        return "web/payment";
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

            orderDetailRequest.setPaymentType(0);

            OrderDetailResponse orderDetailResponse = orderDetailService.checkOut(userId,orderDetailRequest);
            return "redirect:/home/user";
        }catch (Exception e){
            return null;
        }
    }
    @PutMapping("/checkout_bank_success")
    public String checkOutBank(ModelMap model,
                               @RequestParam("id") Long id,
                               @RequestParam("quantity") String quantity,
                               @RequestParam("phone")String phone,
                               @RequestParam("address") String address,
                               @RequestParam("notes")String notes,
                               @RequestParam("amount") String amount, HttpServletRequest request){
        try{
            String token = jwtUtils.getTokenLoginFromCookie(request);
            Long userId = jwtUtils.getUserIdFromToken(token);

            OrderDetailRequest orderDetailRequest = new OrderDetailRequest();
            orderDetailRequest.setId(id);
            orderDetailRequest.setTotalQuantity(Integer.parseInt(quantity));
            orderDetailRequest.setTotalAmount(Integer.parseInt(amount));
            orderDetailRequest.setPhone(phone);
            orderDetailRequest.setNotes(notes);
            orderDetailRequest.setAddress(address);

            orderDetailRequest.setPaymentType(1);
            orderDetailService.checkOut(userId,orderDetailRequest);
            return "redirect:/home/user";
        }catch (Exception e){
            return null;
        }
    }

    @PostMapping("/checkout_bank")
    public String checkOutBank(HttpServletRequest req, HttpServletResponse resp,
                                    @RequestBody CheckOutRequest checkOutRequest) throws ServletException, IOException {
        try {
            String queryString = "?id="+checkOutRequest.getId()+"&quantity="+checkOutRequest.getTotalQuantity()+"&phone="+ URLEncoder.encode(checkOutRequest.getPhone(), StandardCharsets.UTF_8)+"&address=" +checkOutRequest.getAddress()+"&notes="+checkOutRequest.getNotes();
            queryString = queryString.replace(", ,", "");

            String vnp_Version = "2.1.0";
            String vnp_Command = "pay";
            String orderType = req.getParameter("ordertype");
            int amount = checkOutRequest.getTotalAmount() * 100;
            String bankCode = req.getParameter("bankCode");

            String vnp_TxnRef = Config.getRandomNumber(8);
            String vnp_IpAddr = Config.getIpAddress(req);
            String vnp_TmnCode = Config.vnp_TmnCode;

            Map<String, String> vnp_Params = new HashMap<>();
            vnp_Params.put("vnp_Version", vnp_Version);
            vnp_Params.put("vnp_Command", vnp_Command);
            vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
            vnp_Params.put("vnp_Amount", String.valueOf(amount));
            vnp_Params.put("vnp_CurrCode", "VND");

            if (bankCode != null && !bankCode.isEmpty()) {
                vnp_Params.put("vnp_BankCode", bankCode);
            }
            vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
            vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang:" + vnp_TxnRef);
            vnp_Params.put("vnp_OrderType", orderType);

            String locate = req.getParameter("language");
            if (locate != null && !locate.isEmpty()) {
                vnp_Params.put("vnp_Locale", locate);
            } else {
                vnp_Params.put("vnp_Locale", "vn");
            }
            vnp_Params.put("vnp_ReturnUrl", Config.vnp_ReturnUrl + queryString);
            vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

            Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            String vnp_CreateDate = formatter.format(cld.getTime());
            vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

            cld.add(Calendar.MINUTE, 15);
            String vnp_ExpireDate = formatter.format(cld.getTime());
            vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

            List fieldNames = new ArrayList(vnp_Params.keySet());
            Collections.sort(fieldNames);
            StringBuilder hashData = new StringBuilder();
            StringBuilder query = new StringBuilder();
            Iterator itr = fieldNames.iterator();
            while (itr.hasNext()) {
                String fieldName = (String) itr.next();
                String fieldValue = (String) vnp_Params.get(fieldName);
                if ((fieldValue != null) && (fieldValue.length() > 0)) {
                    //Build hash data
                    hashData.append(fieldName);
                    hashData.append('=');
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8));
                    //Build query
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.UTF_8));
                    query.append('=');
                    query.append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8));
                    if (itr.hasNext()) {
                        query.append('&');
                        hashData.append('&');
                    }
                }
            }
            String queryUrl = query.toString();
            String vnp_SecureHash = Config.hmacSHA512(Config.vnp_HashSecret, hashData.toString());
            queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;

            String paymentUrl = Config.vnp_Url + "?" + queryUrl;
            com.google.gson.JsonObject job = new JsonObject();
            job.addProperty("code", "00");
            job.addProperty("message", "success");
            job.addProperty("data", paymentUrl);
            Gson gson = new Gson();
            resp.getWriter().write(gson.toJson(job));
            return null;
        }catch (Exception e){
            return null;
        }
//        return "redirect:" + paymentUrl;
    }

    @PutMapping("/checkout_bank")
    public String checkOutBank(ModelMap model, HttpServletRequest request,
                               @RequestParam("id") long orderDetailId,
                               @RequestParam("quantity") String quantity,
                               @RequestParam("amount") String amount,
                               @RequestParam("notes") String notes,
                               @RequestParam("ward") String ward,
                               @RequestParam("district") String district,
                               @RequestParam("city") String city,
                               @RequestParam("paymentMethod") String paymentMethod,
                               @RequestParam(value = "phone",defaultValue = "")String phone,
                               @RequestParam(value = "address", defaultValue = "")String address,
                               @RequestParam(value = "vnp_ResponseCode",defaultValue = "")String vnp_ResponseCode){
        try{
            if(Objects.equals(vnp_ResponseCode, "00")){
                String token = jwtUtils.getTokenLoginFromCookie(request);
                Long userId = jwtUtils.getUserIdFromToken(token);

                OrderDetailRequest orderDetailRequest = new OrderDetailRequest();
                orderDetailRequest.setId(orderDetailId);
                orderDetailRequest.setTotalQuantity(Integer.parseInt(quantity));
                orderDetailRequest.setTotalAmount(Integer.parseInt(amount));
                orderDetailRequest.setPhone(phone);
                orderDetailRequest.setNotes(notes);
                orderDetailRequest.setAddress(address +"; "+ ward + ", " + district+ ", "+ city);
                orderDetailRequest.setPaymentType(1);
                OrderDetailResponse orderDetailResponse = orderDetailService.checkOut(userId,orderDetailRequest);
                return "redirect:/home/user";
            }else {
                model.addAttribute("vnp_ResponseCode",vnp_ResponseCode);
                return "redirect:/home/cart/checkout?id="+orderDetailId+"&amount="+amount+"&quantity="+quantity;
            }

        }catch (Exception e){
            return null;
        }
    }
}

