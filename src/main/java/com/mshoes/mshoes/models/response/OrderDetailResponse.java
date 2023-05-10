package com.mshoes.mshoes.models.response;

import com.mshoes.mshoes.models.Payment;
import lombok.Data;
import java.util.List;

@Data
public class OrderDetailResponse {
    private Long id;

    private String orderCode;

    private int totalAmount;

    private int totalQuantity;

    private String phone;

    private String address;

    private String createdDate;

    private int status;

    private int type;

    private String notes;

    private List<OrderItemResponse> orderItemResponses;

    private UserResponse userResponse;

    private PaymentResponse paymentResponse;
}
