package com.mshoes.mshoes.services;

import com.mshoes.mshoes.models.dtos.CategoryDTO;
import com.mshoes.mshoes.models.request.CategoryRequest;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CategoryService {


	Page<CategoryDTO> getCategories(int pageNumber, int pageSize, String sortBy);
	/**
	 * Method get all category is active in database <br>
	 * <u><i>Update: 06/03/2023</i></u>
	 *
	 */
	List<CategoryDTO> getAllCategories();

	/**
	 * Method get category by categoryId. <br>
	 * <u><i>Update: 06/03/2023</i></u>
	 *
	 */
	CategoryDTO getCategoryById(long categoryId);

	/**
	 * Method create new category <br>
	 * <u><i>Update: 06/03/2023</i></u>
	 *
	 */
	CategoryDTO createCategory(CategoryRequest categoryRequest);

	/**
	 * Method update category with new information category and categoryId. <br>
	 * <u><i>Update: 06/03/2023</i></u>
	 *
	 */
	CategoryDTO updateCategory(CategoryRequest categoryRequest, long categoryId);

	/**
	 * Method delete category with categoryId. <br>
	 */
	void deleteCategory(long categoryId);

	void actionCategory(long categoryId,int action);
	long countCategory();
}
