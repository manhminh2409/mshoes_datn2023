package com.mshoes.mshoes.models.response;

import lombok.Data;

import java.util.List;

@Data
public class UserResponse {
    private  Long id;

    private String username;

    private String password;

    private String fullname;

    private String email;

    private String image;

    private String address;

    private String phone;

    private List<String> roles;

    private int status;
}
