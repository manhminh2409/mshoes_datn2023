package com.mshoes.mshoes.models.response;

import lombok.Data;

@Data
public class SizeResponse {
    private Long id;

    private String value;

    private int total;

    private int sold;
}
