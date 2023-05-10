package com.mshoes.mshoes.models;

import javax.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "PAYMENT")
public class Payment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column
	private String type;

	@Column
	private int status;

	@OneToOne()
	@JoinColumn(name = "order_detail_id")
	private OrderDetail orderDetail;
}
