package com.ecom.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

import com.ecom.model.Category;
import com.ecom.model.Product;
import com.ecom.model.ProductOrder;
import com.ecom.model.Review;
import com.ecom.model.UserDtls;
import com.ecom.service.CartService;
import com.ecom.service.CategoryService;
import com.ecom.service.CloudinaryImageService;
import com.ecom.service.OrderService;
import com.ecom.service.ProductService;
import com.ecom.service.ReviewService;
import com.ecom.service.UserService;
import com.ecom.util.CommonUtil;
import com.ecom.util.FileUploadUtil;
import com.ecom.util.OrderStatus;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private CategoryService categoryService;
	@Autowired
	private ProductService productService;
	@Autowired
	private UserService userService;
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
	@Autowired
	private CloudinaryImageService cloudinaryImageService;

	@ModelAttribute
	public void getUserDetails(Principal p, Model m) {
		if (p != null) {
			String email = p.getName();
			UserDtls userDtls = userService.getUserByEmail(email);
			m.addAttribute("user", userDtls);
			Integer countCart = cartService.getCountCart(userDtls.getId());
			m.addAttribute("countCart", countCart);
		}
		List<Category> allActiveCategory = categoryService.getAllActiveCategory();
		m.addAttribute("categorys", allActiveCategory);

	}

	@GetMapping("/")
	public String index() {
		return "admin/index";
	}

	@GetMapping("/loadAddProduct")
	public String loadAddProduct(Model m) {
		//List<Category> categories = categoryService.getAllCategory();
		List<Category> categories = categoryService.getAllActiveCategoryForAdmin();
		m.addAttribute("categories", categories);
		return "admin/add_product";
	}

	// get category(list)
	@GetMapping("/category")
	public String category(Model m, @RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
			@RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
		// m.addAttribute("categorys", categoryService.getAllCategory());
		Page<Category> page = categoryService.getAllCategoryPagination(pageNo, pageSize);
		List<Category> categorys = page.getContent();
		m.addAttribute("categorys", categorys);
		// m.addAttribute("productsSize", products.size());

		m.addAttribute("pageNo", page.getNumber());
		m.addAttribute("pageSize", pageSize);
		m.addAttribute("totalElements", page.getTotalElements());
		m.addAttribute("totalPages", page.getTotalPages());
		m.addAttribute("isFirst", page.isFirst());
		m.addAttribute("isLast", page.isLast());
		
		// 🔥 YEH 3 LINES ADD KARO - Category counts
	    m.addAttribute("totalCategories", categoryService.getTotalCategoryCount());
	    m.addAttribute("activeCategories", categoryService.getActiveCategoryCount());
	    m.addAttribute("inactiveCategories", categoryService.getInactiveCategoryCount());
		return "admin/category";
	}

	/*@PostMapping("/saveCategory")
	public String saveCategory(@ModelAttribute Category category, @RequestParam("file") MultipartFile file,
			HttpSession session) throws IOException {
		String imageName = file != null ? file.getOriginalFilename() : "default.jpg";
		category.setImageName(imageName);

		Boolean existCategory = categoryService.existCategory(category.getName());
		if (existCategory) {
			session.setAttribute("errorMsg", "Category name already exists");
		} else {
			Category saveCategory = categoryService.saveCategory(category);
			if (ObjectUtils.isEmpty(saveCategory)) {
				session.setAttribute("errorMsg", "Not saved ! internal server error");
			} else {
				File saveFile = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "Category_img" + File.separator
						+ file.getOriginalFilename());
				System.out.println(path);
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				session.setAttribute("succMsg", "saved successfully");
			}
		}
		// categoryService.saveCategory(category);
		return "redirect:/admin/category";
	}*/
	
	@PostMapping("/saveCategory")
	public String saveCategory(@ModelAttribute Category category, @RequestParam("file") MultipartFile file,
			HttpSession session) throws Exception {
		//String imageName = file != null ? file.getOriginalFilename() : "default.jpg";
		// Pehle category save karo (image name temporary rakh do)
	    String imageName = file.isEmpty() ? "default.jpg" : file.getOriginalFilename();
		
		category.setImageName(imageName);

		Boolean existCategory = categoryService.existCategory(category.getName());
		if (existCategory) {
			session.setAttribute("errorMsg", "Category name already exists");
		} else {
			Category saveCategory = categoryService.saveCategory(category);
			
			// 🔥 AGAR CATEGORY INACTIVE SAVE HO RAHI HAI TO PRODUCTS BHI INACTIVE KARO
		    if(saveCategory != null && saveCategory.getIsActive() == false) {
		        categoryService.deactivateProductsByCategory(saveCategory.getId());
		    }
			
			
			// Replace the old file saving code with:
		/*	if (!ObjectUtils.isEmpty(saveCategory)) {
			    if (!file.isEmpty()) {
			        String imageName1 = FileUploadUtil.saveImage(file, "Category_img");
			        saveCategory.setImageName(imageName1);
			        categoryService.saveCategory(category); // update with new name
			    }
			    session.setAttribute("succMsg", "saved successfully");
			}*/
		    if (!ObjectUtils.isEmpty(saveCategory)) {
	            if (!file.isEmpty()) {
	                // 🔥 Cloudinary pe upload karo
	                String publicId = cloudinaryImageService.uploadImage(file, "Category_img");
	                if(publicId != null) {
	                    saveCategory.setImageName(publicId);
	                  //  categoryService.saveCategory(saveCategory);
	                    categoryService.saveCategory(category);
	                }
	            }
	            session.setAttribute("succMsg", "saved successfully");
	        }
		}
		// categoryService.saveCategory(category);
		return "redirect:/admin/category";
	}

	@GetMapping("/deleteCategory/{id}")
	public String deleteCategory(@PathVariable int id, HttpSession session) {
		Boolean deleteCategory = categoryService.deleteCategory(id);
		if (deleteCategory) {
			session.setAttribute("succMsg", "category delete success");
		} else {
			session.setAttribute("errorMsg", "something wrong on server");
		}
		return "redirect:/admin/category";
	}

	@GetMapping("/loadEditCategory/{id}")
	public String loadEditCategory(@PathVariable int id, Model m) {
		m.addAttribute("category", categoryService.getCategoryById(id));
		return "admin/edit_category";
	}

/*@PostMapping("/updateCategory")
	public String updateCategory(@ModelAttribute Category category, @RequestParam("file") MultipartFile file,
			HttpSession session) throws IOException {

		Category oldCategory = categoryService.getCategoryById(category.getId());
		String imageName = file.isEmpty() ? oldCategory.getImageName() : file.getOriginalFilename();

		if (!ObjectUtils.isEmpty(category)) {
			oldCategory.setName(category.getName());
			oldCategory.setIsActive(category.getIsActive());
			oldCategory.setImageName(imageName);
		}

		Category updateCategory = categoryService.saveCategory(oldCategory);

		if (!ObjectUtils.isEmpty(updateCategory)) {
			if (!file.isEmpty()) {
				File saveFile = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "Category_img" + File.separator
						+ file.getOriginalFilename());
				// System.out.println(path);
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				// session.setAttribute("succMsg", "saved successfully");
			}
			session.setAttribute("succMsg", "Category update success");
		} else {
			session.setAttribute("errorMsg", "something wrong on server");
		}
		return "redirect:/admin/loadEditCategory/" + category.getId();
	}*/
	
	@PostMapping("/updateCategory")
	public String updateCategory(@ModelAttribute Category category, @RequestParam("file") MultipartFile file,
	        HttpSession session) throws Exception {  // Exception throw karo

	    Category oldCategory = categoryService.getCategoryById(category.getId());
	    
	    if (!ObjectUtils.isEmpty(category)) {
	    	/*Category name update start */
	    	// 🔥 AGAR CATEGORY KA NAAM BADAL RAHA HAI
	        String oldCategoryName = oldCategory.getName();      // Purana naam yaad rakho
	        String newCategoryName = category.getName();         // Naya naam
	        
	        if(!oldCategoryName.equals(newCategoryName)) {
	            // Step 1: Sabse pehle products ka category naam update karo
	            categoryService.updateProductCategoryName(category.getId(), newCategoryName);
	        }
	        
	        // Ab category ka naam set karo
	        oldCategory.setName(newCategoryName);
	    	/*Category name update end */
	    	
	       // oldCategory.setName(category.getName());
	        
	        // 🔥 YEH CHECK ADD KARO - AGAR CATEGORY INACTIVE HO RAHI HAI TO PRODUCTS BHI INACTIVE KARO
	        if(oldCategory.getIsActive() == true && category.getIsActive() == false) {
	            // Category active se inactive ho rahi hai
	            categoryService.deactivateProductsByCategory(category.getId());
	        }
	        
	        oldCategory.setIsActive(category.getIsActive());
	        
	        // Agar nayi image upload ki hai to
	    /*    if (!file.isEmpty()) {
	            // ✅ External folder mein save karo
	            String imageName = FileUploadUtil.saveImage(file, "Category_img");
	            oldCategory.setImageName(imageName);
	        }*/
	        // Agar image nahi badli to purani image rahegi (change mat karo)
	        
	     // 🔥 Agar nayi image upload ki hai toh Cloudinary pe upload karo
	        if (!file.isEmpty()) {
	            String publicId = cloudinaryImageService.uploadImage(file, "Category_img");
	            if(publicId != null) {
	                oldCategory.setImageName(publicId);
	            }
	        }
	    }

	    Category updateCategory = categoryService.saveCategory(oldCategory);

	    if (!ObjectUtils.isEmpty(updateCategory)) {
	        session.setAttribute("succMsg", "Category update success");
	    } else {
	        session.setAttribute("errorMsg", "something wrong on server");
	    }
	    return "redirect:/admin/loadEditCategory/" + category.getId();
	}

/*	@PostMapping("/saveProduct")
	public String saveProduct(@ModelAttribute Product product, @RequestParam("file") MultipartFile image,
			HttpSession session) throws IOException {
		String imageName = image.isEmpty() ? "default.jpg" : image.getOriginalFilename();

		product.setImage(imageName);
		product.setDiscount(0);
		product.setDiscountPrice(product.getPrice());
		Product saveProduct = productService.saveProduct(product);

		if (!ObjectUtils.isEmpty(saveProduct)) {
			File saveFile = new ClassPathResource("static/img").getFile();
			Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "Product" + File.separator
					+ image.getOriginalFilename());
			// System.out.println(path);
			Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			session.setAttribute("succMsg", "Product saved successfully");

		} else {

			session.setAttribute("errorMsg", "Not saved ! internal server error");
		}

		return "redirect:/admin/loadAddProduct";
	}*/

	@PostMapping("/saveProduct")
	public String saveProduct(@ModelAttribute Product product, @RequestParam("file") MultipartFile image,
			HttpSession session) throws Exception {
		String imageName = image.isEmpty() ? "default.jpg" : image.getOriginalFilename();

		product.setImage(imageName);
		product.setDiscount(0);
		product.setDiscountPrice(product.getPrice());
		Product saveProduct = productService.saveProduct(product);

		if (!ObjectUtils.isEmpty(saveProduct)) {
		  /*  if (!image.isEmpty()) {
		        String imageName1 = FileUploadUtil.saveImage(image, "Product");
		        saveProduct.setImage(imageName1);
		        productService.saveProduct(saveProduct);
		    }*/
			if (!image.isEmpty()) {
	            // 🔥 Cloudinary pe upload karo
	            String publicId = cloudinaryImageService.uploadImage(image, "Product");
	            if(publicId != null) {
	                saveProduct.setImage(publicId);
	                productService.saveProduct(saveProduct);
	            }
	        }
		    session.setAttribute("succMsg", "Product saved successfully");
		}
		
	 else {

			session.setAttribute("errorMsg", "Not saved ! internal server error");
		}

		return "redirect:/admin/loadAddProduct";
	}
	
	@GetMapping("/products")
	public String loadViewProduct(Model m, @RequestParam(defaultValue = "") String ch,
			@RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
			@RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {

		/*
		 * List<Product> products = null; if (ch != null && ch.length() > 0) { products
		 * = productService.searchProduct(ch); } else { products =
		 * productService.getAllProducts(); } m.addAttribute("products", products);
		 */

		Page<Product> page = null;
		if (ch != null && ch.length() > 0) {
			page = productService.searchProductPagination(pageNo, pageSize, ch);
		} else {
			page = productService.getAllProductsPagination(pageNo, pageSize);
		}
		m.addAttribute("products", page.getContent());
		m.addAttribute("pageNo", page.getNumber());
		m.addAttribute("pageSize", pageSize);
		m.addAttribute("totalElements", page.getTotalElements());
		m.addAttribute("totalPages", page.getTotalPages());
		m.addAttribute("isFirst", page.isFirst());
		m.addAttribute("isLast", page.isLast());

		// 🔥 YEH 3 LINES ADD KARO - Product counts
	    m.addAttribute("totalProducts", productService.getTotalProductCount());
	    m.addAttribute("activeProducts", productService.getActiveProductCount());
	    m.addAttribute("inactiveProducts", productService.getInactiveProductCount());
		
		return "admin/products";
	}

	@GetMapping("/deleteProduct/{id}")
	public String deleteProduct(@PathVariable int id, HttpSession session) {
		Boolean deleteProduct = productService.deleteProduct(id);
		if (deleteProduct) {
			session.setAttribute("succMsg", "product delete success");
		} else {
			session.setAttribute("errorMsg", "something wrong on server");
		}
		return "redirect:/admin/products";
	}

	@GetMapping("/editProduct/{id}")
	public String editProduct(@PathVariable int id, Model m) {
		m.addAttribute("product", productService.getProductById(id));
		List<Category> activeCategories = categoryService.getAllActiveCategoryForAdmin();
		 m.addAttribute("categories", activeCategories);
	//	m.addAttribute("categories", categoryService.getAllCategory());
		return "admin/edit_product";
	} 
	
	
	

	@PostMapping("/updateProduct")
	public String updateProduct(@ModelAttribute Product product, @RequestParam("file") MultipartFile image,
			HttpSession session, Model m) {

		if (product.getDiscount() < 0 || product.getDiscount() > 100) {
			session.setAttribute("errorMsg", "Invalid Discount");
		} else {
			Product updateProduct = productService.updateProduct(product, image);
			if (!ObjectUtils.isEmpty(updateProduct)) {
				session.setAttribute("succMsg", "product update success");
			} else {
				session.setAttribute("errorMsg", "something wrong on server");
			}
		}
		return "redirect:/admin/editProduct/" + product.getId();

	}

	@GetMapping("/users")
	public String getAllUsers(Model m, @RequestParam Integer type) {
		List<UserDtls> users = null;
		String role = null;
		if(type==1) {
			users = userService.getUsers("ROLE_USER");
			role = "ROLE_USER";
		} else {
			users = userService.getUsers("ROLE_ADMIN");
			 role = "ROLE_ADMIN";
		}
		m.addAttribute("userType", type);
		m.addAttribute("users", users);
		
		// 🔥 YEH 3 LINES ADD KARO - User counts
	    m.addAttribute("totalUsers", userService.getTotalUserCount(role));
	    m.addAttribute("activeUsers", userService.getActiveUserCount(role));
	    m.addAttribute("inactiveUsers", userService.getInactiveUserCount(role));
		return "/admin/users";

	}

	@GetMapping("/updateSts")
	public String updateUserAccountStatus(@RequestParam Boolean status, @RequestParam Integer id, @RequestParam Integer type, HttpSession session) {
		Boolean f = userService.updateAccountStatus(id, status);
		if (f) {
			session.setAttribute("succMsg", "Account Status Updated");
		} else {
			session.setAttribute("errorMsg", "Something wrong on server");
		}

		return "redirect:/admin/users?type="+type;
	}

	@GetMapping("/orders")
	public String getAllOrders(Model m, @RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
			@RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
	//	List<ProductOrder> allOrders = orderService.getAllOrders();
	//	m.addAttribute("orders", allOrders);
	//	m.addAttribute("srch", false);
		
		Page<ProductOrder> page = orderService.getAllOrdersPagination(pageNo, pageSize);
		m.addAttribute("orders", page.getContent());
		m.addAttribute("srch", false);
		
		m.addAttribute("pageNo", page.getNumber());
		m.addAttribute("pageSize", pageSize);
		m.addAttribute("totalElements", page.getTotalElements());
		m.addAttribute("totalPages", page.getTotalPages());
		m.addAttribute("isFirst", page.isFirst());
		m.addAttribute("isLast", page.isLast());
		
		// 🔥 YEH 8 LINES ADD KARO - Order status-wise counts
	    m.addAttribute("totalOrders", orderService.getTotalOrderCount());
	    m.addAttribute("inProgressOrders", orderService.getOrderCountByStatus("In Progress"));
	    m.addAttribute("orderReceivedOrders", orderService.getOrderCountByStatus("Order Received"));
	    m.addAttribute("productPackedOrders", orderService.getOrderCountByStatus("Product Packed"));
	    m.addAttribute("outForDeliveryOrders", orderService.getOrderCountByStatus("Out for Delivery"));
	    m.addAttribute("deliveredOrders", orderService.getOrderCountByStatus("Delivered"));
	    m.addAttribute("cancelledOrders", orderService.getOrderCountByStatus("Cancelled"));
		
		return "/admin/orders";
	}

	@PostMapping("/update-order-status")
	public String updateOrderStatus(@RequestParam Integer id, @RequestParam Integer st, HttpSession session) {

		OrderStatus[] values = OrderStatus.values();
		String status = null;

		for (OrderStatus orderSt : values) {
			if (orderSt.getId().equals(st)) {
				status = orderSt.getName();
			}
		}
		ProductOrder updateOrder = orderService.updateOrderStatus(id, status);
		try {
			commonUtil.sendMailForProductOrder(updateOrder, status);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (!ObjectUtils.isEmpty(updateOrder)) {
			session.setAttribute("succMsg", "Status Updated");
		} else {
			session.setAttribute("errorMsg", "Status not updated");
		}
		// System.out.println("values:" +values);
		return "redirect:/admin/orders";
	}

	@GetMapping("/search-order")
	public String searchProduct(@RequestParam String orderId, Model m, HttpSession session, @RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
			@RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
		if (orderId != null && orderId.length() > 0) {

			ProductOrder order = orderService.getOrdersByOrderId(orderId.trim());
			if (ObjectUtils.isEmpty(order)) {
				session.setAttribute("errorMsg", "Incorrect orderId");
				m.addAttribute("orderDtls", null);
			} else {
				m.addAttribute("orderDtls", order);
			}
			m.addAttribute("srch", true);

		} else {
		//	List<ProductOrder> allOrders = orderService.getAllOrders();
		//	m.addAttribute("orders", allOrders);
		//	m.addAttribute("srch", false);
			
			Page<ProductOrder> page = orderService.getAllOrdersPagination(pageNo, pageSize);
				m.addAttribute("orders", page.getContent());
				m.addAttribute("srch", false);
				
				m.addAttribute("pageNo", page.getNumber());
				m.addAttribute("pageSize", pageSize);
				m.addAttribute("totalElements", page.getTotalElements());
				m.addAttribute("totalPages", page.getTotalPages());
				m.addAttribute("isFirst", page.isFirst());
				m.addAttribute("isLast", page.isLast());
		}
		return "/admin/orders";
	}
	
	@GetMapping("/add-admin")
	public String loadAdminAdd() {
		return "/admin/add_admin";
	}
	
	@PostMapping("/save-admin")
	public String saveAdmin(@ModelAttribute UserDtls user, @RequestParam("img") MultipartFile file, HttpSession session)
			throws Exception {
		Boolean existsEmail = userService.existsEmail(user.getEmail());

		if(existsEmail) {
		    session.setAttribute("errorMsg", "Email already exist");
		    return "redirect:/admin/add-admin";
		}
		
		String imageName = file.isEmpty() ? "default.jpg" : file.getOriginalFilename();
		user.setProfileImage(imageName);
		UserDtls saveUser = userService.saveAdmin(user);

		if (!ObjectUtils.isEmpty(saveUser)) {
		/*	if (!file.isEmpty()) {
				 String imageName1 = FileUploadUtil.saveImage(file, "profile_img");
				    saveUser.setProfileImage(imageName1);
				    // user already saved hai upar, but image name update karna hoga
				   // userService.saveAdmin(user);  
				    userService.updateUser(saveUser);// Update with new image name
			//	File saveFile = new ClassPathResource("static/img").getFile();
			//	Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "profile_img" + File.separator
			//			+ file.getOriginalFilename());
			//	System.out.println(path);
			//	Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			}*/
			if (!file.isEmpty()) {
	            // 🔥 Cloudinary pe upload karo
	            String publicId = cloudinaryImageService.uploadImage(file, "profile_img");
	            if(publicId != null) {
	                saveUser.setProfileImage(publicId);
	                userService.updateUser(saveUser);
	            }
	        }
			session.setAttribute("succMsg", "Register Successfully");
		} else {
			session.setAttribute("errorMsg", "Something wrong on server");
		}
		return "redirect:/admin/add-admin";
	}

	@GetMapping("/profile")
	public String profile() {
		return "/admin/profile";
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
		return "redirect:/admin/profile";
	}
	
	@PostMapping("/change-password")
	public String changePassword(@RequestParam String newPassword, @RequestParam String currentPassword, Principal p, HttpSession session) {
		UserDtls loggedInUserDetails = commonUtil.getLoggedInUserDetails(p);
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
		return "redirect:/admin/profile";
	}
	
	
	
	

	@GetMapping("/reviews")
	public String getAllReviews(Model m, 
	        @RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
	        @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
	    
	    Page<Review> page = reviewService.getApprovedReviews(PageRequest.of(pageNo, pageSize));
	    m.addAttribute("reviews", page.getContent());
	    m.addAttribute("pageNo", page.getNumber());
	    m.addAttribute("pageSize", pageSize);
	    m.addAttribute("totalElements", page.getTotalElements());
	    m.addAttribute("totalPages", page.getTotalPages());
	    m.addAttribute("isFirst", page.isFirst());
	    m.addAttribute("isLast", page.isLast());
	    
	    // Review counts
	    m.addAttribute("totalReviews", reviewService.getTotalReviewCount());
	    m.addAttribute("approvedReviews", reviewService.getApprovedReviewCount());
	    m.addAttribute("pendingReviews", reviewService.getPendingReviewCount());
	    
	    return "admin/reviews";
	}

	@GetMapping("/all-reviews")
	public String getAllReviewsList(Model m) {
	    List<Review> reviews = reviewService.getAllReviews();
	    m.addAttribute("reviews", reviews);
	    m.addAttribute("totalReviews", reviewService.getTotalReviewCount());
	    m.addAttribute("approvedReviews", reviewService.getApprovedReviewCount());
	    m.addAttribute("pendingReviews", reviewService.getPendingReviewCount());
	    return "admin/all_reviews";
	}

	@PostMapping("/update-review-status")
	public String updateReviewStatus(@RequestParam Integer id, @RequestParam Boolean status) {
	    reviewService.updateReviewStatus(id, status);
	    return "redirect:/admin/all-reviews";
	}

	@GetMapping("/delete-review/{id}")
	public String deleteReview(@PathVariable Integer id, HttpSession session) {
	    reviewService.deleteReview(id);
	    session.setAttribute("succMsg", "Review deleted successfully");
	    return "redirect:/admin/all-reviews";
	}


}
