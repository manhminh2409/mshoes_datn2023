package com.mshoes.mshoes.repositories;

import com.mshoes.mshoes.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
	@Override
	Optional<Role> findById(Long aLong);

	Optional<Role> findByName(String name);
}
