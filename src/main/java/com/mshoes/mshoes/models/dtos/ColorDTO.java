package com.mshoes.mshoes.models.dtos;

import com.mshoes.mshoes.models.Size;
import lombok.Data;

import java.util.List;

@Data
public class ColorDTO {
    private String value;

    private List<SizeDTO> sizes;
}
