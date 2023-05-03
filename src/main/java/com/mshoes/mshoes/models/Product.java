package com.mshoes.mshoes.models;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name = "PRODUCT")
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(columnDefinition = "LONGTEXT")
	private String description;

	@Column
	private int visited;

	@Column
	private String sku;

	@Column
	private int price;

	@Column
	private int discountPrice;

	@Column(nullable = false)
	private String createdDate;

	@Column
	private String modifiedDate;

	@Column
	private int status;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "product_category_id")
	private Category category;

	@OneToMany(mappedBy = "product")
	private List<Image> images = new ArrayList<>();

	@ManyToOne()
	@JoinColumn(name = "product_author_id")
	private User user;

	@OneToMany(mappedBy = "product")
	private List<Color> colors = new ArrayList<>();
}
