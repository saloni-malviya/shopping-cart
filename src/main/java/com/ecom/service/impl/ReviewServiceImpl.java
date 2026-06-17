package com.ecom.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ecom.model.Product;
import com.ecom.model.Review;
import com.ecom.model.UserDtls;
import com.ecom.repository.ReviewRepository;
import com.ecom.service.ReviewService;

@Service
public class ReviewServiceImpl implements ReviewService {
	
	@Autowired
    private ReviewRepository reviewRepository;

	@Override
	public Review saveReview(Review review) {
		review.setReviewDate(LocalDateTime.now());
        review.setIsApproved(true);  // Auto approve, ya admin approval ke liye false karein
        return reviewRepository.save(review);
	}

	@Override
	public List<Review> getReviewsByProduct(Product product) {
		// TODO Auto-generated method stub
		return reviewRepository.findByProductOrderByReviewDateDesc(product);
	}

	@Override
	public Page<Review> getApprovedReviews(Pageable pageable) {
		// TODO Auto-generated method stub
		return reviewRepository.findByIsApprovedTrue(pageable);
	}

	@Override
	public List<Review> getReviewsByUser(UserDtls user) {
		// TODO Auto-generated method stub
		return reviewRepository.findByUserOrderByReviewDateDesc(user);
	}

	@Override
	public Review getReviewById(Integer id) {
		// TODO Auto-generated method stub
		return reviewRepository.findById(id).orElse(null);
	}

	@Override
	public void deleteReview(Integer id) {
		// TODO Auto-generated method stub
		reviewRepository.deleteById(id);
	}

	@Override
	public Review updateReviewStatus(Integer id, Boolean approved) {
		// TODO Auto-generated method stub
		Review review = getReviewById(id);
        if(review != null) {
            review.setIsApproved(approved);
            return reviewRepository.save(review);
        }
        return null;
	}

	@Override
	public long getTotalReviewCount() {
		// TODO Auto-generated method stub
		return reviewRepository.count();
	}

	@Override
	public long getApprovedReviewCount() {
		// TODO Auto-generated method stub
		return reviewRepository.countByIsApprovedTrue();
	}

	@Override
	public long getPendingReviewCount() {
		// TODO Auto-generated method stub
		return reviewRepository.countByIsApprovedFalse();
	}

	@Override
	public List<Review> getAllReviews() {
		// TODO Auto-generated method stub
		return reviewRepository.findAllByOrderByReviewDateDesc();
	}

}
