package com.mshoes.mshoes.models.request;

import lombok.Data;

@Data
public class UpdateOrderRequest {
    private String fullname;
    private String email;
    private String phone;
    private String address;
    private String notes;
    private int status;
}
