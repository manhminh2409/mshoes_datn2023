package com.mshoes.mshoes.models.request;

import lombok.Data;

@Data
public class UserRequest {
	private String username;

	private String password;

	private String fullname;

	private String email;

	private String address;

	private String phone;
}
