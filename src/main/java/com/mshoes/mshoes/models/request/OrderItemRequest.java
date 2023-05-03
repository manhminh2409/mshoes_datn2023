package com.mshoes.mshoes.models.request;

import lombok.Data;

@Data
public class OrderItemRequest {
    private Long sizeId;

    private int quantity;
}
