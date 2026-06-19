package com.ecom.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

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
            headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);

            // ✅ Use LinkedHashMap to maintain order
            Map<String, Object> body = new LinkedHashMap<>();
            
            // Sender
            Map<String, String> sender = new LinkedHashMap<>();
            sender.put("name", "Ecom Store");
            sender.put("email", "salonimalviya03@gmail.com");
            body.put("sender", sender);
            
            // To recipients
            List<Map<String, String>> toList = new ArrayList<>();
            Map<String, String> recipient = new LinkedHashMap<>();
            recipient.put("email", to);
            recipient.put("name", "Customer");
            toList.add(recipient);
            body.put("to", toList);
            
            // Subject
            body.put("subject", subject);
            
            // ✅ HTML Content - Properly formatted
            body.put("htmlContent", htmlContent);
            
            // ✅ Add textContent as fallback
            String textContent = htmlContent.replaceAll("<[^>]*>", "").trim();
            body.put("textContent", textContent);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            // 🔥 Debug logs
            System.out.println("📧 Sending email to: " + to);
            System.out.println("📧 Subject: " + subject);
            System.out.println("📧 API Key: " + (apiKey != null ? apiKey.substring(0, Math.min(10, apiKey.length())) + "..." : "NULL"));
            System.out.println("📧 Request Body: " + body);

            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

            System.out.println("📧 Response Status: " + response.getStatusCode());
            System.out.println("📧 Response Body: " + response.getBody());

            return response.getStatusCode() == HttpStatus.CREATED;
            
        } catch (Exception e) {
            System.err.println("❌ Email send failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}