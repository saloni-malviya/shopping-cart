package com.ecom.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.ecom.model.OrderRequest;
import com.ecom.model.ProductOrder;

public interface OrderService {
	public void saveOrder(Integer userId, OrderRequest orderRequest) throws Exception;
	public List<ProductOrder> getOrdersByUser(Integer userId);
	public ProductOrder updateOrderStatus(Integer id, String status);
	public List<ProductOrder> getAllOrders();
	public ProductOrder getOrdersByOrderId(String orderId);
	public Page<ProductOrder> getAllOrdersPagination(Integer pageNo, Integer pageSize);
	
	 // New method for Razorpay orders
    public ProductOrder getOrderByRazorpayOrderId(String razorpayOrderId);
    public void updatePaymentDetails(Integer orderId, String razorpayOrderId, String razorpayPaymentId, String razorpaySignature);

  //  void saveOrder(Integer userId, OrderRequest orderRequest, Boolean isBuyNow, Integer buyNowProductId, Integer buyNowQuantity) throws Exception;


    public long getTotalOrderCount();
    public long getOrderCountByStatus(String status);
}



    
    
   