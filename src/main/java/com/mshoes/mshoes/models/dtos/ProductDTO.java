package com.mshoes.mshoes.models.dtos;

import com.mshoes.mshoes.models.*;
import lombok.Data;

import java.util.List;

@Data
public class ProductDTO {

	private Long id;

	private String name;

	private String description;

	private int visited;

	private String sku;

	private int price;

	private int discountPrice;

	private String createdDate;

	private String modifiedDate;

	private int status;

	private String categoryTitle;

	private String authorFullname;

	private List<ImageDTO> images;

	private List<ColorDTO> colors;
}
