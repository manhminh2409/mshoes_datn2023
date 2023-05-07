package com.mshoes.mshoes.services;

import com.mshoes.mshoes.models.dtos.RoleDTO;
import com.mshoes.mshoes.models.request.ProfileRequest;
import com.mshoes.mshoes.models.request.SignupRequest;
import com.mshoes.mshoes.models.request.UserRequest;
import com.mshoes.mshoes.models.dtos.UserDTO;
import com.mshoes.mshoes.models.response.UserResponse;
import org.springframework.data.domain.Page;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public interface UserService {
	Page<UserResponse> getUsers(int pageNumber, int pageSize, String sortBy);

	UserResponse getUser(Long userId);
	/**
	 * Method get all user is active in database <br>
	 * <u><i>Update: 26/02/2023</i></u>
	 *
	 */
	List<UserDTO> getAllUsers();

	/**
	 * Method get user by userId. <br>
	 * <u><i>Update: 26/02/2023</i></u>
	 *
	 */
	UserDTO getUserById(long userId);

	List<RoleDTO> getRoleByUsername(String username);

	UserResponse getUserByUsername(String username);
	/**
	 * Method create new User <br>
	 * <u><i>Update: 26/02/2023</i></u>
	 *
	 */
	UserDTO createUser(UserRequest userRequest) throws IOException;

	/**
	 * Method create new User <br>
	 * <u><i>Update: 26/02/2023</i></u>
	 *
	 */
	UserDTO signupUser(SignupRequest signupRequest);

	/**
	 * Method update user with new information User and userId. <br>
	 * <u><i>Update: 26/02/2023</i></u>
	 *
	 */
	UserDTO updateUser(ProfileRequest profileRequest, long userId) throws IOException;

	UserResponse updateUser1(Long userId, UserRequest userRequest) throws IOException;
	/**
	 * Method delete user with userId. <br>
	 * <u><i>Update: 26/02/2023</i></u>
	 *
	 */
	void deleteUser(long userId);

	long countUser();
}
