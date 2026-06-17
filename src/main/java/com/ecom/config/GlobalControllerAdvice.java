package com.ecom.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.ecom.util.ImageUrlHelper;

@ControllerAdvice
public class GlobalControllerAdvice {
	@Autowired
    private ImageUrlHelper imageUrlHelper;
    
    @ModelAttribute("imageHelper")
    public ImageUrlHelper getImageHelper() {
        return imageUrlHelper;
    }
}
