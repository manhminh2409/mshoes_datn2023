package com.mshoes.mshoes.repositories;

import com.mshoes.mshoes.models.Size;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SizeRepository extends JpaRepository<Size, Long> {
    Size findByValue(String value);
}
