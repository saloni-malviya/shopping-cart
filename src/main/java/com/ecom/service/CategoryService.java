package com.ecom.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.ecom.model.Category;

public interface CategoryService {
	public Category saveCategory(Category category);
	public Boolean existCategory(String name);
	public List<Category> getAllCategory();
	public Boolean deleteCategory(int id);
	public Category getCategoryById(int id);
	public List<Category> getAllActiveCategory();
	public Page<Category> getAllCategoryPagination(Integer pageNo, Integer pageSize);

	public void deactivateProductsByCategory(Integer categoryId);
    public void updateProductCategoryName(Integer categoryId, String newCategoryName);
    public List<Category> getAllActiveCategoryForAdmin();
    
	public long getTotalCategoryCount();
	public long getActiveCategoryCount();
	public long getInactiveCategoryCount();
}
