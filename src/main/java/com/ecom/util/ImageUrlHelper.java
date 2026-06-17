/*package com.ecom.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ecom.service.CloudinaryImageService;

@Component
public class ImageUrlHelper {
	@Autowired
    private CloudinaryImageService cloudinaryImageService;
    
    public String getCategoryImageUrl(String imageName) {
        if(imageName == null || imageName.isEmpty() || "default.jpg".equals(imageName)) {
            return "/img/default-category.jpg";
        }
        // Agar imageName mein "ecom/" hai toh publicId hai
        if(imageName.startsWith("ecom/")) {
            return cloudinaryImageService.getImageUrl(imageName);
        }
        // Purani local images ke liye fallback
        return "/uploads/Category_img/" + imageName;
    }
    
    public String getProductImageUrl(String imageName) {
        if(imageName == null || imageName.isEmpty() || "default.jpg".equals(imageName)) {
            return "/img/default-product.jpg";
        }
        if(imageName.startsWith("ecom/")) {
            return cloudinaryImageService.getImageUrl(imageName);
        }
        return "/uploads/Product/" + imageName;
    }
    
    public String getProfileImageUrl(String imageName) {
        if(imageName == null || imageName.isEmpty() || "default.jpg".equals(imageName)) {
            return "/img/default-profile.jpg";
        }
        if(imageName.startsWith("ecom/")) {
            return cloudinaryImageService.getImageUrl(imageName);
        }
        return "/uploads/profile_img/" + imageName;
    }

    
}*/

package com.ecom.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ecom.service.CloudinaryImageService;

@Component
public class ImageUrlHelper {
	
    @Autowired
    private CloudinaryImageService cloudinaryImageService;
    
    public String getCategoryImageUrl(String imageName) {
        if(imageName == null || imageName.isEmpty() || "default.jpg".equals(imageName)) {
            return "/img/default-category.jpg";
        }
        
        // 🔥 Agar imageName "http" se start ho toh direct return
        if(imageName.startsWith("http")) {
            return imageName;
        }
        
        // 🔥 Agar imageName mein "ecom/" hai ya "/" nahi hai toh Cloudinary URL
        if(imageName.startsWith("ecom/") || !imageName.contains("/")) {
            return cloudinaryImageService.getImageUrl(imageName);
        }
        
        // Local fallback (safety)
        return "/uploads/Category_img/" + imageName;
    }
    
    public String getProductImageUrl(String imageName) {
        if(imageName == null || imageName.isEmpty() || "default.jpg".equals(imageName)) {
            return "/img/default-product.jpg";
        }
        
        if(imageName.startsWith("http")) {
            return imageName;
        }
        
        if(imageName.startsWith("ecom/") || !imageName.contains("/")) {
            return cloudinaryImageService.getImageUrl(imageName);
        }
        
        return "/uploads/Product/" + imageName;
    }
    
    public String getProfileImageUrl(String imageName) {
        if(imageName == null || imageName.isEmpty() || "default.jpg".equals(imageName)) {
            return "/img/default-profile.jpg";
        }
        
        if(imageName.startsWith("http")) {
            return imageName;
        }
        
        if(imageName.startsWith("ecom/") || !imageName.contains("/")) {
            return cloudinaryImageService.getImageUrl(imageName);
        }
        
        return "/uploads/profile_img/" + imageName;
    }
}
