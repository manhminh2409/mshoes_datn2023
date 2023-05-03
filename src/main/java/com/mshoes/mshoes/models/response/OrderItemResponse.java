package com.mshoes.mshoes.models.response;

import lombok.Data;

@Data
public class OrderItemResponse {
	private Long id;

	private Long sizeId;

	private int quantity;

	private ProductItemResponse productItemResponse;

	private String size;

	private String color;
}
