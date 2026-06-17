/*package com.ecom.util;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
 
 @Override
 public void addResourceHandlers(ResourceHandlerRegistry registry) {
     registry.addResourceHandler("/uploads/**")
             .addResourceLocations("file:C:/ecom_images/");
 }
}*/

package com.ecom.util;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    // Ab local file mapping ki zaroorat nahi
    // Cloudinary se images serve hongi
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Local uploads folder mapping hata do
        // registry.addResourceHandler("/uploads/**")
        //         .addResourceLocations("file:C:/ecom_images/");
        
        // Agar kuch aur mapping hai toh rakho, nahi toh sab hata do
    }
}

