package com.mshoes.mshoes.models.request;

import lombok.Data;

@Data
public class OrderDetailRequest {
    private Long id;

    private int totalAmount;

    private int totalQuantity;

    private int paymentType;

    private String phone;

    private String address;

    private String notes;
}
