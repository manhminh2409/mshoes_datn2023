package com.mshoes.mshoes.models.response;

import lombok.Data;

@Data
public class PaymentResponse {
    private Long id;

    private String type;

    private int status;
}
