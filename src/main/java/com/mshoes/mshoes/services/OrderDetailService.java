package com.mshoes.mshoes.services;

import com.mshoes.mshoes.models.request.OrderDetailRequest;
import com.mshoes.mshoes.models.request.OrderItemRequest;
import com.mshoes.mshoes.models.request.UpdateOrderRequest;
import com.mshoes.mshoes.models.response.OrderDetailResponse;
import com.mshoes.mshoes.models.response.OrderItemResponse;
import org.springframework.data.domain.Page;

import java.awt.print.Pageable;
import java.util.List;

public interface OrderDetailService {
    //Lấy danh sách đơn hàng
    List<OrderDetailResponse> getAllOrdersWithType1(Long userId, int type);

    Page<OrderDetailResponse> getAllOrdersByType1(int pageNumber, int pageSize, String sortBy);

    Page<OrderDetailResponse> getAllOrdersByType1AndStatus(int status,int pageNumber, int pageSize, String sortBy);
    //Lấy danh sách đơn hàng theo status
    List<OrderDetailResponse> getAllOrdersWithType1ByStatus(Long userId, int type, int status);

    //lấy thông tin 1 đơn hàng
    OrderDetailResponse getOrderById(long orderDetailId);
    //Thêm mới item vào order khi người dùng đã đăng nhập
    void addToOrder(Long userId, OrderItemRequest orderItemRequest);

    //Lấy danh sách giỏ hàng (orderDetail có type = 0)
    List<OrderItemResponse> getItemFromCart(Long userId);

    //Lấy thông tin giỏ hàng hiện tại
    OrderDetailResponse getCartByUserAndType(Long userId, int type);

    //Đặt hàng
    OrderDetailResponse checkOut(Long userId, OrderDetailRequest orderDetailRequest);

    //Tăng giảm số lượng
    void plusOrMinusQuantity(Long orderItemId, String operation);

    //Lấy số sản phẩm có trong giỏ hàng
    int countItemFromCart(Long userId);

    long countOrderByType(int type);
    //Huỷ đơn hàng
    OrderDetailResponse cancelOrder(Long userId, Long orderDetailId);

    OrderDetailResponse updateOrder(Long orderDetailId, UpdateOrderRequest updateOrderRequest);

}
