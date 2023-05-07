package com.mshoes.mshoes.models.request;

import lombok.Data;

@Data
public class CheckOutRequest {
    private String fullname;
    private String email;
    private String phone;
    private String address;
    private String city;
    private String district;
    private String ward;
    private String paymentMethod;
    private String notes;
    private int totalQuantity;
    private int totalAmount;
    private long id;
}
