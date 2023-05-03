package com.mshoes.mshoes.models;

import javax.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ORDER_DETAIL")
public class OrderDetail {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column
	private String orderCode;

	@Column
	private int totalAmount;

	@Column
	private int totalQuantity;

	@Column
	private String createdDate;

	@Column
	private int status;

	@Column
	private int type;

	@OneToMany(mappedBy = "orderDetail", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private List<OrderItem> orderItems = new ArrayList<>();

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id")
	private User user;

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "payment_id")
	private Payment payment;
}
