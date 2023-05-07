package com.mshoes.mshoes.mapper;

import com.mshoes.mshoes.models.Role;
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
import java.util.Set;
import java.util.stream.Collectors;

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

	@Mapping(target = "image", ignore = true)
	User mapRequestedToModel(UserRequest userRequest);

	//Map check out to Request
	@Mapping(target = "address", expression = "java(checkOutRequest.getAddress() + \"; \" +checkOutRequest.getWard() + \", \" + checkOutRequest.getDistrict() + \", \" + checkOutRequest.getCity())")
	UserRequest mapCheckOutToRequest(CheckOutRequest checkOutRequest);

	@Mapping(target = "roles", source = "user.roles", qualifiedByName = "rolesToRoleNames")
	UserResponse mapModelToResponse(User user);

	@Named("rolesToRoleNames")
	default List<String> rolesToRoleNames(Set<Role> roles) {
		return roles.stream().map(role -> role.getName().substring(5)).collect(Collectors.toList());
	}

	User mapRequestedSignupToModel(SignupRequest signupRequest);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "image", ignore = true)
	void updateModel(@MappingTarget User user, UserRequest userRequest);

}
