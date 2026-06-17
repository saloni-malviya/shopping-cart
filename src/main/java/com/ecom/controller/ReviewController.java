/*package com.ecom.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ecom.model.Product;
import com.ecom.model.Review;
import com.ecom.model.UserDtls;
import com.ecom.service.ProductService;
import com.ecom.service.ReviewService;
import com.ecom.service.UserService;
import com.ecom.util.CommonUtil;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/review")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private CommonUtil commonUtil;
    
    @PostMapping("/save")
    public String saveReview(@RequestParam Integer productId, 
                             @RequestParam Integer rating,
                             @RequestParam String reviewText,
                             Principal p,
                             HttpSession session) {
        
        UserDtls user = commonUtil.getLoggedInUserDetails(p);
        
        if(user == null) {
            session.setAttribute("errorMsg", "Please login to give review");
            return "redirect:/product/" + productId;
        }
        
        Product product = productService.getProductById(productId);
        
        Review review = new Review();
        review.setUser(user);
        review.setProduct(product);
        review.setRating(rating);
        review.setReviewText(reviewText);
        
        reviewService.saveReview(review);
        
        session.setAttribute("succMsg", "Review added successfully!");
        return "redirect:/product/" + productId;
    }
    
    @GetMapping("/my-reviews")
    public String myReviews(Model m, Principal p) {
        UserDtls user = commonUtil.getLoggedInUserDetails(p);
        List<Review> reviews = reviewService.getReviewsByUser(user);
        m.addAttribute("reviews", reviews);
        return "user/my_reviews";
    }
    
    @GetMapping("/delete/{id}")
    public String deleteReview(@PathVariable Integer id, HttpSession session) {
        reviewService.deleteReview(id);
        session.setAttribute("succMsg", "Review deleted successfully");
        return "redirect:/user/profile";
    }
}
*/