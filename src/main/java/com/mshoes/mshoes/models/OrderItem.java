package com.mshoes.mshoes.models;

import javax.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "ORDER_ITEM")
public class OrderItem {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column
	private int quantity;

	@ManyToOne()
	@JoinColumn(name = "order_id")
	private OrderDetail orderDetail;

	@OneToOne()
	@JoinColumn(name = "size_id")
	private  Size size;

}
