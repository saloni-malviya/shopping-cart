package com.ecom.service.impl;

import java.util.List;
import org.springframework.data.domain.Pageable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.ecom.model.Category;
import com.ecom.model.Product;
import com.ecom.repository.CategoryRepository;
import com.ecom.repository.ProductRepository;
import com.ecom.service.CategoryService;

@Service
public class CategoryServiceImpl implements CategoryService {
	@Autowired
	private CategoryRepository categoryRepository;
	
	@Autowired
	private ProductRepository productRepository;

	@Override
	public Category saveCategory(Category category) {
		return categoryRepository.save(category);
	}

	@Override
	public List<Category> getAllCategory() {
		// TODO Auto-generated method stub
		return categoryRepository.findAll();
	}

	@Override
	public Boolean existCategory(String name) {
		// TODO Auto-generated method stub
		return categoryRepository.existsByName(name);
	}

	@Override
	public Boolean deleteCategory(int id) {
		Category category = categoryRepository.findById(id).orElse(null);
		if(!ObjectUtils.isEmpty(category)) {
			categoryRepository.delete(category);
			return true;
		}
		return false;
	}

	@Override
	public Category getCategoryById(int id) {
		Category category = categoryRepository.findById(id).orElse(null);
		
		return category;
	}

	@Override
	public List<Category> getAllActiveCategory() {
		List<Category> categories = categoryRepository.findByIsActiveTrue();
		return categories;
	}

	@Override
	public Page<Category> getAllCategoryPagination(Integer pageNo, Integer pageSize) {
		Pageable pageable = PageRequest.of(pageNo, pageSize);
		return categoryRepository.findAll(pageable);
	}

	@Override
	public void deactivateProductsByCategory(Integer categoryId) {
		Category category = getCategoryById(categoryId);
	    if(category != null) {
	        List<Product> products = productRepository.findByCategory(category.getName());
	        for(Product product : products) {
	            product.setIsActive(false);
	            productRepository.save(product);
	        }
	    }
		
	}
	
    @Override
    public void updateProductCategoryName(Integer categoryId, String newCategoryName) {
        Category category = getCategoryById(categoryId);
        if(category != null) {
            List<Product> products = productRepository.findByCategory(category.getName());
            for(Product product : products) {
                product.setCategory(newCategoryName);
                productRepository.save(product);
            }
        }
    }
    
    @Override
    public List<Category> getAllActiveCategoryForAdmin() {
        // Admin ke liye sirf active categories (kyuki inactive mein product add nahi karna)
        return categoryRepository.findByIsActiveTrue();
    }

	@Override
	public long getTotalCategoryCount() {
		// TODO Auto-generated method stub
		return categoryRepository.count();
	}

	@Override
	public long getActiveCategoryCount() {
		// TODO Auto-generated method stub
		return categoryRepository.countByIsActiveTrue();
	}

	@Override
	public long getInactiveCategoryCount() {
		// TODO Auto-generated method stub
		return categoryRepository.countByIsActiveFalse();
	}

}
