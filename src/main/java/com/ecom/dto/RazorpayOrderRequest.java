package com.ecom.dto;

import lombok.Data;

@Data
public class RazorpayOrderRequest {

	    private Integer amount;
	    private String currency;
	    private String receipt;
	    private String paymentCapture;
	}

