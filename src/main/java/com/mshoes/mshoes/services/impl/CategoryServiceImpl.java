package com.mshoes.mshoes.services.impl;

import com.mshoes.mshoes.exception.ResourceNotFoundException;
import com.mshoes.mshoes.libraries.Utilities;
import com.mshoes.mshoes.mapper.CategoryMapper;
import com.mshoes.mshoes.models.Category;
import com.mshoes.mshoes.models.dtos.CategoryDTO;
import com.mshoes.mshoes.models.request.CategoryRequest;
import com.mshoes.mshoes.repositories.CategoryRepository;
import com.mshoes.mshoes.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

	private final CategoryRepository categoryRepository;

	private final CategoryMapper categoryMapper;

	private final Utilities utilities;

	@Autowired
	public CategoryServiceImpl(CategoryRepository categoryRepository, CategoryMapper categoryMapper,
							   Utilities utilities) {
		super();
		this.categoryRepository = categoryRepository;
		this.categoryMapper = categoryMapper;
		this.utilities = utilities;
	}

	@Override
	public Page<CategoryDTO> getCategories(int pageNumber, int pageSize, String sortBy) {
		Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy).descending());
		Page<Category> categories = categoryRepository.findAll(pageable);
		return categories.map(categoryMapper::mapModelToDTO);
	}

	@Override
	public List<CategoryDTO> getAllCategories() {
		// TODO Auto-generated method stub
		List<Category> categories = categoryRepository.findAll();
		return categoryMapper.mapModelToDTOs(categories);
	}

	@Override
	public CategoryDTO getCategoryById(long categoryId) {
		// TODO Auto-generated method stub
		Category category = categoryRepository.findById(categoryId)
				.orElseThrow(() -> new ResourceNotFoundException("Category", "Id", categoryId));

		return categoryMapper.mapModelToDTO(category);
	}

	@Override
	public CategoryDTO createCategory(CategoryRequest categoryRequest) {
		// TODO Auto-generated method stub

		// Get current date and set categoryCreatedDate, categoryLastModified
		Category category = categoryMapper.mapRequestToCategory(categoryRequest);
		category.setCreatedDate(utilities.getCurrentDate());
		category.setModifiedDate(utilities.getCurrentDate());
		// Set default status
		category.setStatus(1);
		return categoryMapper.mapModelToDTO(categoryRepository.save(category));
	}

	@Override
	public CategoryDTO updateCategory(CategoryRequest categoryRequest, long categoryId) {
		// TODO Auto-generated method stub
		Category category = categoryRepository.findById(categoryId)
				.orElseThrow(() -> new ResourceNotFoundException("Category", "Id", categoryId));
		categoryMapper.updateModel(category, categoryRequest);
		category.setModifiedDate(utilities.getCurrentDate());

		// Save
		return categoryMapper.mapModelToDTO(categoryRepository.save(category));
	}

	@Override
	public void deleteCategory(long categoryId) {
		// TODO Auto-generated method stub
		Category category = categoryRepository.findById(categoryId)
				.orElseThrow(() -> new ResourceNotFoundException("Category", "Id", categoryId));
		try {
			categoryRepository.delete(category);
		} catch (Exception ex) {
			System.out.print("Ex: " + ex);
		}
	}

	@Override
	public void actionCategory(long categoryId,int action) {
		Category category = categoryRepository.findById(categoryId)
				.orElseThrow(() -> new ResourceNotFoundException("Category", "Id", categoryId));
		try {
			if(action == 0){
				category.setStatus(1);
			}else if (action == 1){
				category.setStatus(0);
			}
			categoryRepository.save(category);
		} catch (Exception ex) {
			System.out.print("Ex: " + ex);
		}
	}

	@Override
	public long countCategory() {
		return categoryRepository.count();
	}

}
