package com.mshoes.mshoes.repositories;

import com.mshoes.mshoes.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
	User findByEmail(String email);

	Optional<User> findByUsernameOrEmail(String name, String email);

	User findByUsername(String name);

	Boolean existsByUsername(String name);

	Boolean existsByEmail(String email);
}
