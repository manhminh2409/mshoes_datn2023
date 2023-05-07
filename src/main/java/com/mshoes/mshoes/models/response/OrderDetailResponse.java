package com.mshoes.mshoes.models.response;

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

    private String paymentType;

    private String notes;

    private List<OrderItemResponse> orderItemResponses;

    private UserResponse userResponse;
}
