package com.mshoes.mshoes.mapper;

import com.mshoes.mshoes.models.request.CheckOutRequest;
import com.mshoes.mshoes.models.request.ProfileRequest;
import com.mshoes.mshoes.models.request.SignupRequest;
import com.mshoes.mshoes.models.request.UserRequest;
import com.mshoes.mshoes.models.dtos.UserDTO;
import com.mshoes.mshoes.models.User;
import com.mshoes.mshoes.models.response.UserResponse;
import org.mapstruct.*;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
@Mapper(uses = ProductMapper.class)
public interface UserMapper {
	// mapper one model to dto
	UserDTO mapModelToDTO(User user);

	// mapper list model to dto
	List<UserDTO> mapModelToDTOs(List<User> users);

	// mapper one dto to model
	User mapDTOToModel(UserDTO userDTO);

	// mapper list dto to model
	List<User> mapDTOToModels(List<UserDTO> userDTOS);

	User mapRequestedToModel(UserRequest userRequest);

	//Map check out to Request
	@Mapping(target = "address", expression = "java(checkOutRequest.getAddress() + \"; \" +checkOutRequest.getWard() + \", \" + checkOutRequest.getDistrict() + \", \" + checkOutRequest.getCity())")
	UserRequest mapCheckOutToRequest(CheckOutRequest checkOutRequest);

	UserResponse mapModelToResponse(User user);

	User mapRequestedSignupToModel(SignupRequest signupRequest);
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	void updateModel(@MappingTarget User user, UserRequest userRequest);

}
