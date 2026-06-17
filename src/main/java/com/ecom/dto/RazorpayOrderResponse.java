package com.ecom.dto;

import lombok.Data;

@Data
public class RazorpayOrderResponse {
    private String id;
    private String entity;
    private Integer amount;
    private Integer amountPaid;
    private Integer amountDue;
    private String currency;
    private String receipt;
    private String status;
    private Integer attempts;
    private Long createdAt;  // Keep as Long, but we'll handle conversion
}
