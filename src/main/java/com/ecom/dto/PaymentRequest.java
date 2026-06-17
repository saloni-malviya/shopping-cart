package com.ecom.dto;

import lombok.Data;

@Data
public class PaymentRequest {

	    private String razorpayOrderId;
	    private String razorpayPaymentId;
	    private String razorpaySignature;
	    private String orderId;
	    private String paymentType;
	}

