package com.mshoes.mshoes.repositories;

import com.mshoes.mshoes.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
	List<Category> findAll();
}
