package com.mshoes.mshoes.models.response;

import lombok.Data;

import java.util.List;

@Data
public class ProductItemResponse {
    private Long id;

    private String name;

    private String description;

    private int visited;

    private String sku;

    private int price;

    private int discountPrice;

    private List<ImageResponse> imageResponses;
}
