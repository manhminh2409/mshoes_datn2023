package com.mshoes.mshoes.models.response;

import java.util.List;
import lombok.Data;

@Data
public class ProductResponse {
	private Long id;

	private String name;

	private String description;

	private int visited;

	private String sku;

	private int price;

	private int discountPrice;

	private Long categoryId;

	private List<ImageResponse> imageResponses;

	private List<ColorResponse> colorResponses;
}
