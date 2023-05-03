package com.mshoes.mshoes.repositories;

import com.mshoes.mshoes.models.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Long> {
	List<Image> findAll();
}
