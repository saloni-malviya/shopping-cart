package com.ecom.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ecom.model.Product;
import com.ecom.model.Review;
import com.ecom.model.UserDtls;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
List<Review> findByProductOrderByReviewDateDesc(Product product);
    
    Page<Review> findByIsApprovedTrue(Pageable pageable);
    
    List<Review> findByUserOrderByReviewDateDesc(UserDtls user);
    
    long countByIsApprovedTrue();
    
    long countByIsApprovedFalse();
    
    List<Review> findAllByOrderByReviewDateDesc();
}
