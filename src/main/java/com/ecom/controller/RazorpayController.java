package com.ecom.controller;

import java.security.Principal;
import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ecom.config.RazorpayConfig;
import com.ecom.dto.RazorpayOrderResponse;
import com.ecom.dto.PaymentRequest;
import com.ecom.model.Cart;
import com.ecom.model.OrderRequest;
import com.ecom.model.UserDtls;
import com.ecom.service.CartService;
import com.ecom.service.OrderService;
import com.ecom.service.RazorpayService;  // Make sure this import exists
import com.ecom.service.UserService;
import com.ecom.util.CommonUtil;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/payment")
public class RazorpayController {
    
    @Autowired
    private RazorpayService razorpayService;  // ✅ This should work now
    
    @Autowired
    private RazorpayConfig razorpayConfig;
    
    @Autowired
    private CartService cartService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private CommonUtil commonUtil;
    
    @GetMapping("/create-order")
    @ResponseBody
    public String createOrder(Principal p) throws Exception {
        try {
            UserDtls user = commonUtil.getLoggedInUserDetails(p);
            if (user == null) {
                return "{\"error\": \"User not logged in\"}";
            }
            
            List<Cart> carts = cartService.getCartsByUser(user.getId());
            
            if(carts == null || carts.isEmpty()) {
                return "{\"error\": \"Cart is empty\"}";
            }
            
            // Calculate total properly
            Double totalOrderPrice = carts.stream()
                    .mapToDouble(Cart::getTotalPrice)
                    .sum();
            
            Double finalAmount = totalOrderPrice + 250 + 100;
            
            // Convert to paise (Razorpay expects amount in paise)
            Integer amountInPaise = (int) (finalAmount * 100);
            
            String receipt = "receipt_" + System.currentTimeMillis();
            RazorpayOrderResponse order = razorpayService.createOrder(amountInPaise, receipt);
            
            JSONObject response = new JSONObject();
            response.put("id", order.getId());
            response.put("amount", order.getAmount());
            response.put("currency", order.getCurrency());
            response.put("key", razorpayConfig.getRazorpayKeyId());
            
            System.out.println("Order created successfully: " + response.toString()); // Debug log
            
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\": \"" + e.getMessage() + "\"}";
        }
    }
    
    @PostMapping("/verify")
    @ResponseBody
    public String verifyPayment(@RequestBody PaymentRequest paymentRequest, Principal p, HttpSession session) {
        try {
            System.out.println("Verifying payment: " + paymentRequest);
            
            JSONObject attributes = new JSONObject();
            attributes.put("razorpay_order_id", paymentRequest.getRazorpayOrderId());
            attributes.put("razorpay_payment_id", paymentRequest.getRazorpayPaymentId());
            attributes.put("razorpay_signature", paymentRequest.getRazorpaySignature());
            
            boolean isValid = razorpayService.verifyPaymentSignature(attributes);
            
            if(isValid) {
                UserDtls user = commonUtil.getLoggedInUserDetails(p);
                if (user == null) {
                    return "{\"status\": \"failed\", \"message\": \"User not found\"}";
                }
                
                // Get pending order from session
                OrderRequest orderRequest = (OrderRequest) session.getAttribute("pendingOrder");
                if(orderRequest != null) {
                    orderRequest.setRazorpayOrderId(paymentRequest.getRazorpayOrderId());
                    orderRequest.setRazorpayPaymentId(paymentRequest.getRazorpayPaymentId());
                    orderRequest.setRazorpaySignature(paymentRequest.getRazorpaySignature());
                    
                    orderService.saveOrder(user.getId(), orderRequest);
                    session.removeAttribute("pendingOrder");
                    session.removeAttribute("razorpayAmount");
                    
                    return "{\"status\": \"success\"}";
                } else {
                    return "{\"status\": \"failed\", \"message\": \"No pending order found\"}";
                }
            }
            
            return "{\"status\": \"failed\", \"message\": \"Payment verification failed\"}";
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"status\": \"failed\", \"message\": \"" + e.getMessage() + "\"}";
        }
    }
    
    
}

            
            