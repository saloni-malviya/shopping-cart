package com.ecom.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.ecom.model.Product;
import com.ecom.model.Review;
import com.ecom.model.UserDtls;

public interface ReviewService {
public Review saveReview(Review review);
    
    public List<Review> getReviewsByProduct(Product product);
    
    public Page<Review> getApprovedReviews(Pageable pageable);
    
    public List<Review> getReviewsByUser(UserDtls user);
    
    public Review getReviewById(Integer id);
    
    public void deleteReview(Integer id);
    
    public Review updateReviewStatus(Integer id, Boolean approved);
    
    public long getTotalReviewCount();
    
    public long getApprovedReviewCount();
    
    public long getPendingReviewCount();
    
    public List<Review> getAllReviews();
}
