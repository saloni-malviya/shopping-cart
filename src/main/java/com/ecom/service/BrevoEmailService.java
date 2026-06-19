package com.ecom.service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BrevoEmailService {
	
	    @Value("${brevo.api.key}")
	    private String apiKey;

	    private final RestTemplate restTemplate = new RestTemplate();

	    public boolean sendEmail(String to, String subject, String htmlContent) {
	        try {
	            String url = "https://api.brevo.com/v3/smtp/email";

	            HttpHeaders headers = new HttpHeaders();
	            headers.setContentType(MediaType.APPLICATION_JSON);
	            headers.set("api-key", apiKey);

	            // Sender info
	            Map<String, Object> sender = new HashMap<>();
	            sender.put("name", "Ecom Store");
	            sender.put("email", "salonimalviya03@gmail.com");

	            // Recipient info
	            Map<String, Object> toMap = new HashMap<>();
	            toMap.put("email", to);
	            toMap.put("name", "Customer");

	            // Email body
	            Map<String, Object> body = new HashMap<>();
	            body.put("sender", sender);
	            body.put("to", List.of(toMap));
	            body.put("subject", subject);
	            body.put("htmlContent", htmlContent);

	            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

	            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

	            return response.getStatusCode() == HttpStatus.CREATED;
	        } catch (Exception e) {
	            e.printStackTrace();
	            return false;
	        }
	    }
	
}
