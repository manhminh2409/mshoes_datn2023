package com.mshoes.mshoes.models;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.mshoes.mshoes.config.LongDeserializer;

import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor


@Entity
@Table(name = "USER", uniqueConstraints = { @UniqueConstraint(columnNames = { "username" }),
		@UniqueConstraint(columnNames = { "email" }) })
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonDeserialize(using = LongDeserializer.class)
	private Long id;

	@Column(nullable = false)
	private String username;

	@Column(nullable = false)
	private String password;

	@Column
	private String fullname;

	@Column
	private String email;

	@Column
	private String image;

	@Column(columnDefinition = "LONGTEXT")
	private String address;

	@Column
	private String phone;

	@Column(nullable = false)
	private String createdDate;

	@Column
	private String modifiedDate;

	@Column
	private int status;

	@ManyToMany(targetEntity = Role.class,fetch = FetchType.EAGER)
	@JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
	private Set<Role> roles = new HashSet<>();

	@OneToMany(mappedBy = "user")
	private Set<Product> products = new HashSet<>();

	@OneToMany(mappedBy = "user")
	private Set<OrderDetail> orderDetails = new HashSet<>();

}
