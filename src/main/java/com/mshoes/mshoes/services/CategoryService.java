package com.mshoes.mshoes.services;

import com.mshoes.mshoes.models.dtos.CategoryDTO;

import java.util.List;

public interface CategoryService {

	/**
	 * Method get all category is active in database <br>
	 * <u><i>Update: 06/03/2023</i></u>
	 *
	 * @return
	 */
	List<CategoryDTO> getAllCategories();

	/**
	 * Method get category by categoryId. <br>
	 * <u><i>Update: 06/03/2023</i></u>
	 *
	 * @param categoryId
	 * @return
	 */
	CategoryDTO getCategoryById(long categoryId);

	/**
	 * Method create new category <br>
	 * <u><i>Update: 06/03/2023</i></u>
	 *
	 * @param categoryDTO
	 * @return
	 */
	CategoryDTO createCategory(CategoryDTO categoryDTO);

	/**
	 * Method update category with new information category and categoryId. <br>
	 * <u><i>Update: 06/03/2023</i></u>
	 *
	 * @param categoryDTO
	 * @param categoryId
	 * @return
	 */
	CategoryDTO updateCategory(CategoryDTO categoryDTO, long categoryId);

	/**
	 * Method delete category with categoryId. <br>
	 * <u><i>Update: 06/03/2023</i></u>
	 *
	 * @param categoryId
	 */
	void deleteCategory(long categoryId);
}
