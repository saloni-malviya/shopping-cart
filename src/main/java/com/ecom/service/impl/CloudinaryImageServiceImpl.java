/*package com.ecom.service.impl;
import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.ecom.service.CloudinaryImageService;

@Service
public class CloudinaryImageServiceImpl implements CloudinaryImageService {
	@Autowired
    private Cloudinary cloudinary;
	
	@Override
	public String uploadImage(MultipartFile file, String folderName) {
		try {
            Map<String, Object> uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                    "folder", "ecom/" + folderName,
                    "use_filename", true,
                    "unique_filename", false,
                    "overwrite", true
                )
            );
         // Public ID return karo (issey delete kar sakte ho baad mein)
            return (String) uploadResult.get("public_id");
            
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
		
		
	}

	@Override
	public void deleteImage(String publicId) {
		// TODO Auto-generated method stub
		try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (IOException e) {
            e.printStackTrace();
        }
	}

	@Override
	public String getImageUrl(String publicId) {
		// TODO Auto-generated method stub
		// Cloudinary URL generate karo
        return cloudinary.url().generate(publicId);
	}

}*/

package com.ecom.service.impl;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.ecom.service.CloudinaryImageService;

@Service
public class CloudinaryImageServiceImpl implements CloudinaryImageService {
	
    @Autowired
    private Cloudinary cloudinary;
    
    @Value("${cloudinary.cloud.name}")
    private String cloudName;
    
    @Override
    public String uploadImage(MultipartFile file, String folderName) {
        try {
            Map<String, Object> uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                    "folder", "ecom/" + folderName,
                    "public_id", file.getOriginalFilename().split("\\.")[0],
                    "use_filename", true,
                    "unique_filename", false,
                    "overwrite", true
                )
            );
            return (String) uploadResult.get("public_id");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void deleteImage(String publicId) {
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getImageUrl(String publicId) {
        // 🔥 FIX: Direct URL generate karo
        if(publicId == null || publicId.isEmpty() || "default.jpg".equals(publicId)) {
            return "/img/default.jpg";
        }
        
        if(publicId.startsWith("http")) {
            return publicId;
        }
        
        // 🔥 Public ID se extension hata do agar hai toh
        String cleanPublicId = publicId;
        if(publicId.contains(".")) {
            cleanPublicId = publicId.substring(0, publicId.lastIndexOf("."));
        }
        
        return "https://res.cloudinary.com/" + cloudName + "/image/upload/" + cleanPublicId;
    }
}
