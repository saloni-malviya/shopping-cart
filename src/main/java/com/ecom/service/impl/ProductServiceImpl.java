package com.ecom.service.impl;

import java.io.File;
import org.springframework.data.domain.Pageable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.ecom.model.Category;
import com.ecom.model.Product;
import com.ecom.model.Review;
import com.ecom.repository.ProductRepository;
import com.ecom.service.CloudinaryImageService;
import com.ecom.service.ProductService;
import com.ecom.service.ReviewService;
import com.ecom.util.FileUploadUtil;

import jakarta.servlet.http.HttpSession;

@Service
public class ProductServiceImpl implements ProductService {
	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private ReviewService reviewService;
	@Autowired
	private CloudinaryImageService cloudinaryImageService;

	@Override
	public Product saveProduct(Product product) {

		return productRepository.save(product);
	}

	@Override
	public List<Product> getAllProducts() {

		return productRepository.findAll();
	}

	@Override
	public Boolean deleteProduct(int id) {
		// TODO Auto-generated method stub
		Product product = productRepository.findById(id).orElse(null);
		if (!ObjectUtils.isEmpty(product)) {
			productRepository.delete(product);
			return true;
		}
		return false;

	}
	
	

	@Override
	public Product getProductById(int id) {
		Product product = productRepository.findById(id).orElse(null);

		return product;

	}

	/*@Override
	public Product updateProduct(Product product, MultipartFile image) {
			Product oldProduct = getProductById(product.getId());
			String imageName = image.isEmpty() ? oldProduct.getImage() : image.getOriginalFilename();

			
				oldProduct.setTitle(product.getTitle());
				oldProduct.setDescription(product.getDescription());
				oldProduct.setCategory(product.getCategory());
				oldProduct.setPrice(product.getPrice());
				oldProduct.setStock(product.getStock());
			    oldProduct.setImage(imageName);
			    oldProduct.setIsActive(product.getIsActive());
			    oldProduct.setDiscount(product.getDiscount());
			    
			    // for discount
			    //5 = 100*(5/100); 100-5=95
			    Double discount = product.getPrice()*(product.getDiscount()/100.0);
			    Double discountPrice = product.getPrice()-discount;
			    oldProduct.setDiscountPrice(discountPrice);
			    

			Product updateProduct = productRepository.save(oldProduct);

			if (!ObjectUtils.isEmpty(updateProduct)) {
				if (!image.isEmpty()) {
					try {
					File saveFile = new ClassPathResource("static/img").getFile();
					Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "Product" + File.separator
							+ image.getOriginalFilename());
						
					//	String uploadDir = "C:/ecom/product_img/";

					//	Path path = Paths.get(uploadDir + image.getOriginalFilename());

					//	Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
						
					 System.out.println(path);
					Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
					// session.setAttribute("succMsg", "saved successfully");
				} catch(Exception e) {
					e.printStackTrace();
				}
				}
				return product;
			} 
			
		


		return null;
	}*/
	
	@Override
	public Product updateProduct(Product product, MultipartFile image) {
	    Product oldProduct = getProductById(product.getId());
	    
	    if (ObjectUtils.isEmpty(oldProduct)) {
	        return null;
	    }
	    
	    // Agar nayi image upload ki hai to external folder mein save karo
	   /* if (!image.isEmpty()) {
	        try {
	            String imageName = FileUploadUtil.saveImage(image, "Product");
	            oldProduct.setImage(imageName);
	        } catch (Exception e) {
	            e.printStackTrace();
	            return null;
	        }
	    }*/
	    if (!image.isEmpty()) {
	        try {
	            String publicId = cloudinaryImageService.uploadImage(image, "Product");
	            if(publicId != null) {
	                oldProduct.setImage(publicId);
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	            return null;
	        }
	    }
	    // Agar image nahi badli to purani image rahegi (change mat karo)
	    
	    // Baaki fields update karo
	    oldProduct.setTitle(product.getTitle());
	    oldProduct.setDescription(product.getDescription());
	    oldProduct.setCategory(product.getCategory());
	    oldProduct.setPrice(product.getPrice());
	    oldProduct.setStock(product.getStock());
	    oldProduct.setIsActive(product.getIsActive());
	    oldProduct.setDiscount(product.getDiscount());
	    
	    // Discount price calculate karo
	    Double discount = product.getPrice() * (product.getDiscount() / 100.0);
	    Double discountPrice = product.getPrice() - discount;
	    oldProduct.setDiscountPrice(discountPrice);
	    
	    return productRepository.save(oldProduct);
	}
	
	

	@Override
	public List<Product> getAllActiveProducts(String category) {
		List<Product> products = null;
		if(ObjectUtils.isEmpty(category)) {
			products = productRepository.findByIsActiveTrue();
		} else {
		//	products = productRepository.findByCategory(category);
			products = productRepository.findByCategoryAndIsActiveTrue(category);
			//products = productRepository.findByCategoryIgnoreCaseAndIsActiveTrue(category);
		}
		return products;
	}

	@Override
	public List<Product> searchProduct(String ch) {
		
		return productRepository.findByTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(ch, ch);
	}

	@Override
	public Page<Product> getAllActiveProductPagination(Integer pageNo, Integer pageSize, String category) {
		Pageable pageable = PageRequest.of(pageNo, pageSize);
		Page<Product> pageProduct = null;
		
		if(ObjectUtils.isEmpty(category)) {
			pageProduct = productRepository.findByIsActiveTrue(pageable);
		} 
		else {
			//pageProduct = productRepository.findByCategory(pageable,category);
			 pageProduct = productRepository.findByCategoryAndIsActiveTrue(category, pageable);  
		}
		
		return pageProduct;
	}

	@Override
	public Page<Product> searchProductPagination(Integer pageNo, Integer pageSize, String ch) {
		Pageable pageable = PageRequest.of(pageNo, pageSize);
		return productRepository.findByTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(ch, ch, pageable);
	}

	@Override
	public Page<Product> getAllProductsPagination(Integer pageNo, Integer pageSize) {
		Pageable pageable = PageRequest.of(pageNo, pageSize);
		return productRepository.findAll(pageable);
	}

	@Override
	public Page<Product> searchActiveProductPagination(Integer pageNo, Integer pageSize, String category, String ch) {
		
		Page<Product> pageProduct = null;
		Pageable pageable = PageRequest.of(pageNo, pageSize);
	//	pageProduct = productRepository.findByisActiveTrueAndTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(ch, ch, pageable);
		pageProduct = productRepository
				.findByIsActiveTrueAndTitleContainingIgnoreCaseOrIsActiveTrueAndCategoryContainingIgnoreCase(
				    ch, ch, pageable);
		
	/*	if(ObjectUtils.isEmpty(category)) {
			pageProduct = productRepository.findByIsActiveTrue(pageable);
		} else {
			pageProduct = productRepository.findByCategory(pageable,category);
		}*/
		return pageProduct;
		
	}

	@Override
	public long getTotalProductCount() {
		// TODO Auto-generated method stub
		return productRepository.count();
	}

	@Override
	public long getActiveProductCount() {
		// TODO Auto-generated method stub
		return productRepository.countByIsActiveTrue();
	}

	@Override
	public long getInactiveProductCount() {
		// TODO Auto-generated method stub
		return productRepository.countByIsActiveFalse();
	}

	@Override
	public List<Review> getProductReviews(Integer productId) {
		// TODO Auto-generated method stub
		Product product = getProductById(productId);
	    if(product != null) {
	        return reviewService.getReviewsByProduct(product);
	    }
	    return null;
	}

}
