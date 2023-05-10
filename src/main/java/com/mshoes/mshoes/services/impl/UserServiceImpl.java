package com.mshoes.mshoes.services.impl;

import com.mshoes.mshoes.exception.ResourceNotFoundException;
import com.mshoes.mshoes.utils.DateUtils;
import com.mshoes.mshoes.mapper.RoleMapper;
import com.mshoes.mshoes.mapper.UserMapper;
import com.mshoes.mshoes.models.dtos.RoleDTO;
import com.mshoes.mshoes.models.request.ProfileRequest;
import com.mshoes.mshoes.models.request.SignupRequest;
import com.mshoes.mshoes.models.request.UserRequest;
import com.mshoes.mshoes.models.dtos.UserDTO;
import com.mshoes.mshoes.models.Role;
import com.mshoes.mshoes.models.User;
import com.mshoes.mshoes.models.response.UserResponse;
import com.mshoes.mshoes.repositories.RoleRepository;
import com.mshoes.mshoes.repositories.UserRepository;
import com.mshoes.mshoes.services.UserService;
import com.mshoes.mshoes.utils.ImageUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.*;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;

	private final RoleRepository roleRepository;

	private final UserMapper userMapper;

	private  final RoleMapper roleMapper;

	private final DateUtils dateUtils;

	private final ImageUtils imageUtils;
	@Autowired
	public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, UserMapper userMapper,
						   RoleMapper roleMapper, DateUtils dateUtils, ImageUtils imageUtils) {
		// TODO Auto-generated constructor stub
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.userMapper = userMapper;
		this.roleMapper = roleMapper;
		this.dateUtils = dateUtils;
		this.imageUtils = imageUtils;
	}

	@Override
	public Page<UserResponse> getUsers(int pageNumber, int pageSize, String sortBy) {
		Pageable pageable = PageRequest.of(pageNumber,pageSize, Sort.by(sortBy).descending());
		Page<User> users = userRepository.findAll(pageable);
		return users.map(userMapper::mapModelToResponse);
	}

	@Override
	public UserResponse getUser(Long userId) {
		return userMapper.mapModelToResponse(userRepository.findById(userId).orElseThrow());
	}

	@Override
	public List<UserDTO> getAllUsers() {
		// TODO Auto-generated method stub
		List<User> users = userRepository.findAll();
		return userMapper.mapModelToDTOs(users);
	}

	@Override
	public UserDTO getUserById(long userId) {
		// TODO Auto-generated method stub
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));
		return userMapper.mapModelToDTO(user);
	}

	@Override
	public List<RoleDTO> getRoleByUsername(String username) {
		//Lấy user
		User user = userRepository.findByUsername(username);
		Set<Role> roles = user.getRoles();
		return roles.stream().map(roleMapper::mapModelToDTO).toList();
	}

	@Override
	public UserResponse getUserByUsername(String username) {
		User user = userRepository.findByUsername(username);
		return userMapper.mapModelToResponse(user);
	}


	BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Transactional
	@Override
	public UserDTO createUser(UserRequest userRequest) throws IOException {
		// TODO Auto-generated method stub
		User user = userMapper.mapRequestedToModel(userRequest);
		String imagePath = imageUtils.saveImage(userRequest.getImage());
		user.setImage(imagePath);
		// Get current date and set userCreatedDate and userLastModified
		user.setCreatedDate(dateUtils.getCurrentDate());
		user.setModifiedDate(dateUtils.getCurrentDate());
		user.setAddress(user.getAddress() + "; " +userRequest.getWard()+", "+userRequest.getDistrict()+", "+ userRequest.getCity() );
		// Encode password
		user.setPassword(passwordEncoder().encode(user.getPassword()));

		// Set default userStatus
		user.setStatus(1);

		// Set default role_id = 2 (ROLE_USER)
		long defaultRoleId = 2;
		Role role = roleRepository.findById(defaultRoleId)
				.orElseThrow(() -> new ResourceNotFoundException("Role", "ID", defaultRoleId));
		user.getRoles().add(role);

		// Save user into database
		User responseUser = userRepository.save(user);

		return userMapper.mapModelToDTO(responseUser);
	}

	@Transactional
	@Override
	public UserDTO signupUser(SignupRequest signupRequest) {
		User user = userMapper.mapRequestedSignupToModel(signupRequest);

		// Get current date and set userCreatedDate and userLastModified
		user.setCreatedDate(dateUtils.getCurrentDate());
		user.setModifiedDate(dateUtils.getCurrentDate());

		// Encode password

		user.setPassword(passwordEncoder().encode(user.getPassword()));

		// Set default userStatus
		user.setStatus(1);

		// Set default role_id = 2 (ROLE_USER)
		long defaultRoleId = 2;
		Role role = roleRepository.findById(defaultRoleId)
				.orElseThrow(() -> new ResourceNotFoundException("Role", "ID", defaultRoleId));
		user.getRoles().add(role);

		// Save user into database
		try {
			User responseUser = userRepository.save(user);
			return userMapper.mapModelToDTO(responseUser);
		} catch (Exception e){
			return null;
		}
	}



	@Override
	public UserDTO updateUser(ProfileRequest profileRequest, long userId) throws IOException {
		// TODO Auto-generated method stub

		// Get old User with userId from Database
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User", "ID", userId));

		if (profileRequest.getImage().getOriginalFilename() != ""){
			user.setImage(imageUtils.saveImage(profileRequest.getImage()));
			user.setFullname(profileRequest.getFullname());
			user.setPhone(profileRequest.getPhone());
			user.setAddress(profileRequest.getAddress());
			user.setModifiedDate(dateUtils.getCurrentDate());
			// Save data
			User responseUser = userRepository.save(user);

			return userMapper.mapModelToDTO(responseUser);
		}else {
			user.setFullname(profileRequest.getFullname());
			user.setPhone(profileRequest.getPhone());
			user.setAddress(profileRequest.getAddress());
			user.setModifiedDate(dateUtils.getCurrentDate());
			// Save data
			User responseUser = userRepository.save(user);

			return userMapper.mapModelToDTO(responseUser);
		}

	}

	@Override
	@Transactional
	public UserResponse updateUser1(Long userId, UserRequest userRequest) throws IOException {
		//Lấy thông tin cũ
		User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("USER", "ID", userId));
		if(userRequest.getEmail() == user.getEmail()){
			return null;
		}else {
			//Lấy lại mật khẩu cũ
			userRequest.setPassword(user.getPassword());
			if (userRequest.getImage().getOriginalFilename() != ""){

				userMapper.updateModel(user, userRequest);
				user.setImage(imageUtils.saveImage(userRequest.getImage()));

				return userMapper.mapModelToResponse(userRepository.save(user));
			}else{
				//Lấy lại đường dẫn ảnh cũ
				String image = user.getImage();
				userMapper.updateModel(user, userRequest);
				user.setImage(image);
				user.setModifiedDate(dateUtils.getCurrentDate());
				return userMapper.mapModelToResponse(userRepository.save(user));
			}
		}
	}
	@Override
	public void deleteUser(long userId) {
		// TODO Auto-generated method stub

		// Get old User with userId from Database
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User", "ID", userId));
		try {
			user.getRoles().clear();
			userRepository.delete(user);
		} catch (Exception ex) {
			System.out.print("Ex: " + ex);
		}
	}

	@Override
	public long countUser() {
		return userRepository.count();
	}

	@Override
	public void actionProduct(Long userId, int action) {
		//Lấy thông tin user
		User user = userRepository.findById(userId).orElseThrow();
		try{
			if(action == 0){
				user.setStatus(1);
			}else if (action == 1){
				user.setStatus(0);
			}
			userRepository.save(user);
		}catch (Exception ex){
			System.out.print("Ex: " + ex);
		}
	}
}
