package com.mshoes.mshoes.models.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UserRequest {
	private String username;
	private String password;
	private String cfPassword;
	private String fullname;
	private String email;
	private String address;
	private String phone;
	private String city;
	private String district;
	private String ward;
	private MultipartFile image;
}
