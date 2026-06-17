package com.ecom.service.impl;
/*
import com.ecom.config.RazorpayConfig;
import com.ecom.dto.RazorpayOrderResponse;
import com.ecom.service.RazorpayService;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RazorpayServiceImpl implements RazorpayService{

	    @Autowired
	    private RazorpayConfig razorpayConfig;
	    
	    private RazorpayClient client;
	    
	    private RazorpayClient getClient() throws RazorpayException {
	        if (client == null) {
	            client = new RazorpayClient(razorpayConfig.getRazorpayKeyId(), 
	                                        razorpayConfig.getRazorpayKeySecret());
	        }
	        return client;
	    }
	    
	    @Override
	    public RazorpayOrderResponse createOrder(Integer amount, String receipt) throws RazorpayException {
	        try {
	            JSONObject orderRequest = new JSONObject();
	            orderRequest.put("amount", amount); // amount in paise
	            orderRequest.put("currency", "INR");
	            orderRequest.put("receipt", receipt);
	            orderRequest.put("payment_capture", true);
	            
	            Order order = getClient().orders.create(orderRequest);
	            
	            RazorpayOrderResponse response = new RazorpayOrderResponse();
	            response.setId(order.get("id"));
	            response.setEntity(order.get("entity"));
	            response.setAmount(order.get("amount"));
	            response.setAmountPaid(order.get("amount_paid"));
	            response.setAmountDue(order.get("amount_due"));
	            response.setCurrency(order.get("currency"));
	            response.setReceipt(order.get("receipt"));
	            response.setStatus(order.get("status"));
	            response.setAttempts(order.get("attempts"));
	           // response.setCreatedAt(order.get("created_at"));
	            response.setCreatedAt((Long) order.get("created_at"));
	            
	            return response;
	        } catch (RazorpayException e) {
	            throw new RazorpayException(e.getMessage());
	        }
	    }
	    
	    @Override
	    public Boolean verifyPaymentSignature(JSONObject attributes) throws RazorpayException {
	        try {
	            boolean isValid = Utils.verifyPaymentSignature(attributes, razorpayConfig.getRazorpayKeySecret());
	            return isValid;
	        } catch (RazorpayException e) {
	            throw new RazorpayException(e.getMessage());
	        }
	    }
	}

*/
//package com.ecom.service.impl;

import com.ecom.config.RazorpayConfig;
import com.ecom.dto.RazorpayOrderResponse;
import com.ecom.service.RazorpayService;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RazorpayServiceImpl implements RazorpayService {

    @Autowired
    private RazorpayConfig razorpayConfig;
    
    private RazorpayClient client;
    
    private RazorpayClient getClient() throws RazorpayException {
        if (client == null) {
            client = new RazorpayClient(razorpayConfig.getRazorpayKeyId(), 
                                        razorpayConfig.getRazorpayKeySecret());
        }
        return client;
    }
    
    @Override
    public RazorpayOrderResponse createOrder(Integer amount, String receipt) throws RazorpayException {
        try {
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amount);
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", receipt);
            orderRequest.put("payment_capture", true);
            
            Order order = getClient().orders.create(orderRequest);
            
            RazorpayOrderResponse response = new RazorpayOrderResponse();
            response.setId(order.get("id"));
            response.setEntity(order.get("entity"));
            response.setAmount(order.get("amount"));
            response.setAmountPaid(order.get("amount_paid"));
            response.setAmountDue(order.get("amount_due"));
            response.setCurrency(order.get("currency"));
            response.setReceipt(order.get("receipt"));
            response.setStatus(order.get("status"));
            response.setAttempts(order.get("attempts"));
            
            // FIX: Handle createdAt properly - convert to Long
         //   Object createdAtObj = order.get("created_at");
           
            Object createdAtObj = order.get("created_at");
            if (createdAtObj instanceof Number) {
                response.setCreatedAt(((Number) createdAtObj).longValue());
            } else {
                response.setCreatedAt(System.currentTimeMillis() / 1000);
            }
            
            
            return response;
        } catch (RazorpayException e) {
            throw new RazorpayException(e.getMessage());
        }
    }
    
    @Override
    public Boolean verifyPaymentSignature(JSONObject attributes) throws RazorpayException {
        try {
            boolean isValid = Utils.verifyPaymentSignature(attributes, razorpayConfig.getRazorpayKeySecret());
            return isValid;
        } catch (RazorpayException e) {
            throw new RazorpayException(e.getMessage());
        }
    }
}