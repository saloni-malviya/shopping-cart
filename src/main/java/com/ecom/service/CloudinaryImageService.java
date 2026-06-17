package com.ecom.service;
import org.springframework.web.multipart.MultipartFile;

public interface CloudinaryImageService {
	String uploadImage(MultipartFile file, String folderName);
    void deleteImage(String publicId);
    String getImageUrl(String publicId);
}
