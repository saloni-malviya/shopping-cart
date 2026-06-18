package com.ecom.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.ecom.model.Cart;
import com.ecom.model.Category;
import com.ecom.model.OrderRequest;
import com.ecom.model.Product;
import com.ecom.model.ProductOrder;
import com.ecom.model.Review;
import com.ecom.model.UserDtls;
import com.ecom.service.CartService;
import com.ecom.service.CategoryService;
import com.ecom.service.OrderService;
import com.ecom.service.ProductService;
import com.ecom.service.ReviewService;
import com.ecom.service.UserService;
import com.ecom.util.CommonUtil;
import com.ecom.util.OrderStatus;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	private CategoryService categoryService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private CartService cartService;
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private CommonUtil commonUtil;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private ReviewService reviewService;
	
	@GetMapping("/")
	public String home() {
		return "user/home";
	}

	@ModelAttribute
	public void getUserDetails(Principal p, Model m) {
		if(p!=null) {
			String email = p.getName();
			UserDtls userDtls = userService.getUserByEmail(email);
			m.addAttribute("user", userDtls);
			Integer countCart = cartService.getCountCart(userDtls.getId());
			m.addAttribute("countCart", countCart);
		}
		List<Category> allActiveCategory = categoryService.getAllActiveCategory();
		m.addAttribute("categorys", allActiveCategory);
		
	}
	
	@GetMapping("/addCart")
	public String addToCart(@RequestParam Integer pid, @RequestParam Integer uid, HttpSession session) {
		Cart saveCart = cartService.saveCart(pid, uid);
		
		if(ObjectUtils.isEmpty(saveCart)) {
			session.setAttribute("errorMsg", "Product add to cart failed");
		}
		else {
			session.setAttribute("succMsg", "Product added to cart");
		}
		return "redirect:/product/" + pid;
	}
	
	@GetMapping("/cart")
	public String loadCartPage(Principal p, Model m) {
		
		UserDtls user = getLoggedInUserDetails(p);
		List<Cart> carts = cartService.getCartsByUser(user.getId());
		m.addAttribute("carts",carts);
		if(carts.size()>0) {
		//Double totalOrderPrice = carts.get(carts.size()-1).getTotalOrderPrice();
			Double totalOrderPrice = carts.stream()
			        .mapToDouble(Cart::getTotalPrice)
			        .sum();
		m.addAttribute("totalOrderPrice",totalOrderPrice);
		}
		return "user/cart";
		
	}
	
	@GetMapping("/cartQuantityUpdate")
	public String updateCartQuantity(@RequestParam String sy, @RequestParam Integer cid) {
		cartService.updateQuantity(sy, cid);
		return "redirect:/user/cart";
	}
	
	private UserDtls getLoggedInUserDetails(Principal p) {
		String email = p.getName();
		UserDtls userDtls = userService.getUserByEmail(email);
		return userDtls;
	}
	
	@GetMapping("/orders")
	public String orderPage(Principal p, Model m) {
		UserDtls user = getLoggedInUserDetails(p);
		List<Cart> carts = cartService.getCartsByUser(user.getId());
		m.addAttribute("carts",carts);
		if(carts.size()>0) {
		//Double orderPrice = carts.get(carts.size()-1).getTotalOrderPrice();
		//Double totalOrderPrice = carts.get(carts.size()-1).getTotalOrderPrice() + 250 + 100;
			Double orderPrice = carts.stream()
			        .mapToDouble(Cart::getTotalPrice)
			        .sum();

			Double totalOrderPrice = orderPrice + 250 + 100;	
		m.addAttribute("orderPrice",orderPrice);
		m.addAttribute("totalOrderPrice",totalOrderPrice); 
			
			
		}
		return "user/order";
	}
	
	
	
/*	@PostMapping("/save-order")
	public String saveOrder(@ModelAttribute OrderRequest request, Principal p, HttpSession session) throws Exception {
		//System.out.println(request);
		UserDtls user = getLoggedInUserDetails(p);
		
		if("ONLINE".equals(request.getPaymentType())) {
	        // For online payment, store order details in session and redirect to Razorpay
	        session.setAttribute("pendingOrder", request);
	        List<Cart> carts = cartService.getCartsByUser(user.getId());
	        Double totalOrderPrice = carts.get(carts.size()-1).getTotalOrderPrice();
	        Double finalAmount = totalOrderPrice + 250 + 100;
	        session.setAttribute("razorpayAmount", finalAmount);
	        return "redirect:/user/razorpay-payment";
	    } else {
         //for COD, save order directly
		orderService.saveOrder(user.getId(), request);
		return "redirect:/user/success";
	}}*/
	
	
	
	
	@PostMapping("/save-order")
	public String saveOrder(@ModelAttribute OrderRequest request, Principal p, HttpSession session) throws Exception {
	    UserDtls user = getLoggedInUserDetails(p);
	    
	    if(user == null) {
	        session.setAttribute("errorMsg", "User not found. Please login again.");
	        return "redirect:/signin";
	    }
	    
	    System.out.println("Save order called. Payment type: " + request.getPaymentType());
	    
	    if("ONLINE".equals(request.getPaymentType())) {
	        // For online payment, store order details in session and redirect to Razorpay
	        session.setAttribute("pendingOrder", request);
	        List<Cart> carts = cartService.getCartsByUser(user.getId());
	        if(carts != null && !carts.isEmpty()) {
	            Double totalOrderPrice = carts.stream()
	                    .mapToDouble(Cart::getTotalPrice)
	                    .sum();
	            
	            Double finalAmount = totalOrderPrice + 250 + 100;
	            session.setAttribute("razorpayAmount", finalAmount);
	            System.out.println("Redirecting to Razorpay payment page. Amount: " + finalAmount);
	            return "redirect:/user/razorpay-payment";
	            
	        } else {
	            session.setAttribute("errorMsg", "Cart is empty");
	            return "redirect:/user/cart";
	            
	        }
	    } else {
	        // for COD, save order directly
	        orderService.saveOrder(user.getId(), request);
	        session.setAttribute("succMsg", "Order placed successfully!");
	        return "redirect:/user/success";
	    }
	}
	
		
	@GetMapping("/razorpay-payment")
	public String razorpayPayment(Model m, Principal p, HttpSession session) {
	    UserDtls user = getLoggedInUserDetails(p);
	    Double amount = (Double) session.getAttribute("razorpayAmount");
	    m.addAttribute("amount", amount);
	    m.addAttribute("user", user);
	    return "user/razorpay";
	}
	
	@GetMapping("/success")
	public String loadSuccess() {
		return "user/success";
	}
	
	@GetMapping("/user-orders")
	public String myOrder(Model m, Principal p) {
		UserDtls loginUser = getLoggedInUserDetails(p);
		List<ProductOrder> orders = orderService.getOrdersByUser(loginUser.getId());
		m.addAttribute("orders",orders);
		return "user/my_orders";
	}
	
	@GetMapping("/update-status")
	public String updateOrderStatus(@RequestParam Integer id, @RequestParam Integer st, HttpSession session) {
		
		OrderStatus[] values = OrderStatus.values();
		String status = null;
		
		for(OrderStatus orderSt : values) {
			if(orderSt.getId().equals(st)) {
				status = orderSt.getName();
			}
		}
		ProductOrder updateOrder = orderService.updateOrderStatus(id, status);
		if(!ObjectUtils.isEmpty(updateOrder)) {
			session.setAttribute("succMsg", "Status Updated");
		}
		else {
			session.setAttribute("errorMsg", "Status not updated");
		}
		//System.out.println("values:" +values);
		return "redirect:/user/user-orders";
	}
	
	@GetMapping("/profile")
	public String profile() {
		return "user/profile";
	}
	
	@PostMapping("/update-profile")
	public String updateProfile(@ModelAttribute UserDtls user, @RequestParam MultipartFile img, HttpSession session) {
		UserDtls updateUserProfile = userService.updateUserProfile(user, img);
		if(ObjectUtils.isEmpty(updateUserProfile)) {
			session.setAttribute("errorMsg", "Profile not Updated");
		}
		else {
			session.setAttribute("succMsg", "Profile updated");
		}
		return "redirect:/user/profile";
	}
	
	@PostMapping("/change-password")
	public String changePassword(@RequestParam String newPassword, @RequestParam String currentPassword, Principal p, HttpSession session) {
		UserDtls loggedInUserDetails = getLoggedInUserDetails(p);
		boolean matches = passwordEncoder.matches(currentPassword, loggedInUserDetails.getPassword());
		if(matches) {
			String encodePassword = passwordEncoder.encode(newPassword);
			loggedInUserDetails.setPassword(encodePassword);
			UserDtls updateUser = userService.updateUser(loggedInUserDetails);
			if(ObjectUtils.isEmpty(updateUser)) {
				session.setAttribute("errorMsg", "Password not updated || Error in server");
			}
			else {
				session.setAttribute("succMsg", "Password updated successfully");
			}
		}
		else {
			session.setAttribute("errorMsg", "Current Password incorrect");
		}
		return "redirect:/user/profile";
	}
	
	
	@PostMapping("/save-review")
    public String saveReview(@RequestParam Integer productId, 
                             @RequestParam Integer rating,
                             @RequestParam String reviewText,
                             Principal p,
                             HttpSession session) {
        
        UserDtls user = getLoggedInUserDetails(p);
        
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
        UserDtls user = getLoggedInUserDetails(p);
        if(user != null) {
        List<Review> reviews = reviewService.getReviewsByUser(user);
        m.addAttribute("reviews", reviews);
        }
        return "user/my_reviews";
    }
    
    @GetMapping("/delete-review/{id}")
    public String deleteReview(@PathVariable Integer id, HttpSession session, Principal p) {
    	 UserDtls user = getLoggedInUserDetails(p);
    	    Review review = reviewService.getReviewById(id);
    	    
    	    // Check karo ki ye review current user ka hai ya nahi
    	    if(review != null && review.getUser().getId().equals(user.getId())) {
    	    	reviewService.deleteReview(id);
    	        session.setAttribute("succMsg", "Review deleted successfully");
    	    } else {
    	    	session.setAttribute("errorMsg", "You can only delete your own reviews");
    	    }
       
        return "redirect:/user/my-reviews";
    }
	
	
	
	
}
