package com.mshoes.mshoes.mapper;

import com.mshoes.mshoes.models.Color;
import com.mshoes.mshoes.models.dtos.ColorDTO;
import com.mshoes.mshoes.models.request.ColorRequest;
import com.mshoes.mshoes.models.response.ColorResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper(uses = ProductMapper.class)
public interface ColorMapper {
    // mapper one model to dto
    ColorDTO mapModelToDTO(Color Color);

    // mapper list model to dto
    List<ColorDTO> mapModelToDTOs(List<Color> Colors);

    // mapper one dto to model
    Color mapDTOToModel(ColorDTO ColorDTO);

    // mapper list dto to model
    List<Color> mapDTOToModels(List<ColorDTO> ColorDTOS);

    @Mapping(target = "sizeResponses", source = "sizes")
    ColorResponse mapModelToResponse(Color color);

    @Mapping(target = "product.id", source = "productId")
    Color mapRequestedToModel(ColorRequest colorRequest);

//    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
//    void updateModel(@MappingTarget Color Color, RequestedColor requestedColor);

}
