package com.ecom.service;

import com.ecom.dto.RazorpayOrderResponse;
import com.razorpay.RazorpayException;
import org.json.JSONObject;

public interface RazorpayService {
	    RazorpayOrderResponse createOrder(Integer amount, String receipt) throws RazorpayException;
	    Boolean verifyPaymentSignature(JSONObject attributes) throws RazorpayException;
	}

