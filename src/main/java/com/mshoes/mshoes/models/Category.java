package com.mshoes.mshoes.models;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name = "CATEGORY")
public class Category {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column
	private String title;

	@Column
	private String description;

	@Column
	private String createdDate;

	@Column
	private String modifiedDate;

	@Column
	private int status;

	@OneToMany(mappedBy = "category")
	private List<Product> products;
}
