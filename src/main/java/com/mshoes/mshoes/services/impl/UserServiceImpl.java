package com.mshoes.mshoes.services.impl;

import com.mshoes.mshoes.exception.ResourceNotFoundException;
import com.mshoes.mshoes.libraries.Utilities;
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
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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

	private final Utilities utilities;
	@Autowired
	public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, UserMapper userMapper,
						   RoleMapper roleMapper, Utilities utilities) {
		// TODO Auto-generated constructor stub
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.userMapper = userMapper;
		this.roleMapper = roleMapper;
		this.utilities = utilities;
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
	public UserDTO createUser(UserRequest userRequest) {
		// TODO Auto-generated method stub
		User user = userMapper.mapRequestedToModel(userRequest);

		// Get current date and set userCreatedDate and userLastModified
		user.setCreatedDate(utilities.getCurrentDate());
		user.setModifiedDate(utilities.getCurrentDate());

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
		user.setCreatedDate(utilities.getCurrentDate());
		user.setModifiedDate(utilities.getCurrentDate());

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

		// Lấy tên file và extension.
		String filename = StringUtils.cleanPath(Objects.requireNonNull(profileRequest.getImage().getOriginalFilename()));
		String extension = FilenameUtils.getExtension(filename);

		// Tạo đường dẫn tới file ảnh.
		String imagePath = "/assets/images/uploads/products/" + filename;

		// Tạo file mới với đường dẫn được chỉ định.

		File savedFile = new File("D:/DATN2023/src/main/resources/static/assets/images/user/"+ filename);

		// Lưu file vào đường dẫn.
		try (OutputStream outputStream = new FileOutputStream(savedFile)) {
			outputStream.write(profileRequest.getImage().getBytes());
		}
		user.setImage(imagePath);
		user.setFullname(profileRequest.getFullname());
		user.setPhone(profileRequest.getPhone());
		user.setAddress(profileRequest.getAddress());

		// Save data
		User responseUser = userRepository.save(user);

		return userMapper.mapModelToDTO(responseUser);

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
}
