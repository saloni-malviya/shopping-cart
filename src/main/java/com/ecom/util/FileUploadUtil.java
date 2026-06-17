/*package com.ecom.util;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import org.springframework.web.multipart.MultipartFile;

public class FileUploadUtil {
 
 public static String saveImage(MultipartFile file, String folderName) throws Exception {
     String uploadDir = "C:/ecom_images/" + folderName + "/";
     File directory = new File(uploadDir);
     if (!directory.exists()) {
         directory.mkdirs();
     }
     
     String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
     Path path = Paths.get(uploadDir + fileName);
     Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
     return fileName;
 }
}*/

package com.ecom.util;

import org.springframework.web.multipart.MultipartFile;

public class FileUploadUtil {
    
    // Yeh method ab cloudinary use karega
    public static String saveImage(MultipartFile file, String folderName) throws Exception {
        // Yeh method ab CloudinaryImageService use karega
        // But hum direct call nahi kar sakte kyunki static method hai
        // Isliye hum isko modify karte hain - ab yeh sirf fileName return karega
        // Actual upload CloudinaryImageService mein hoga
        
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        
        // Unique fileName generate karo
        String fileName = System.currentTimeMillis() + extension;
        
        // Yeh hum upload ke baad publicId ke roop mein use karenge
        return "ecom/" + folderName + "/" + fileName;
    }
}