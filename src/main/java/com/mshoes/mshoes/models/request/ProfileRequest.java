package com.mshoes.mshoes.models.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ProfileRequest {
    private String fullname;
    private String phone;
    private String address;
    private MultipartFile image;
}
