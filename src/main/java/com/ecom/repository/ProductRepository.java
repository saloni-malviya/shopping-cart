package com.ecom.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ecom.model.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {
	List<Product> findByIsActiveTrue();

	Page<Product> findByIsActiveTrue(Pageable pageable);
	
	List<Product> findByCategory(String category);
	
	List<Product> findByCategoryAndIsActiveTrue(String category);
	
	List<Product> findByTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(String ch, String ch2);
	
	Page<Product> findByCategory(Pageable pageable, String category);
	
	Page<Product> findByTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(String ch, String ch2, Pageable pageable);
	
//	Page<Product> findByisActiveTrueAndTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(String ch, String ch2, Pageable pageable);
	Page<Product> findByIsActiveTrueAndTitleContainingIgnoreCaseOrIsActiveTrueAndCategoryContainingIgnoreCase(
		    String title,
		    String category,
		    Pageable pageable
		);
	
	Page<Product> findByCategoryAndIsActiveTrue(String category, Pageable pageable);
	
	// Total products count
	long count();

	// Active products count (isActive = true)
	long countByIsActiveTrue();

	// Inactive products count (isActive = false)
	long countByIsActiveFalse();
}
