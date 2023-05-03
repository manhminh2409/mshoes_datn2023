package com.mshoes.mshoes.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {

	private Long id;

	private String title;

	private String description;

	private String createdDate;

	private String modifiedDate;

	private int status;

}
