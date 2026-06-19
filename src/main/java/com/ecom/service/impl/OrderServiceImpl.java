package com.ecom.service.impl;

import java.time.LocalDate;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.ecom.model.Cart;
import com.ecom.model.OrderAddress;
import com.ecom.model.OrderRequest;
import com.ecom.model.ProductOrder;
import com.ecom.repository.CartRepository;
import com.ecom.repository.ProductOrderRepository;
import com.ecom.repository.ProductRepository;
import com.ecom.repository.UserRepository;
import com.ecom.service.OrderService;
import com.ecom.util.CommonUtil;
import com.ecom.util.OrderStatus;
import com.ecom.model.Product;
import com.ecom.model.UserDtls;
@Service
public class OrderServiceImpl implements OrderService{
	
	@Autowired
	private ProductOrderRepository orderRepository;
	
	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private CommonUtil commonUtil;
	
	@Autowired
	private ProductRepository productRepository;  // 🔥 YEH ADD KARO - ye missing tha
    
    @Autowired
    private UserRepository userRepository;


	@Override
	public void saveOrder(Integer userId, OrderRequest orderRequest) throws Exception {
		
		List<Cart> carts = cartRepository.findByUserId(userId);
		 if(carts.isEmpty()) {
		        throw new Exception("Cart is empty - kuch nahi hai bechne ko!");
		    }
		
		for(Cart cart:carts) {
			ProductOrder order = new ProductOrder();
			order.setOrderId(UUID.randomUUID().toString());
			order.setOrderDate(LocalDate.now());
			
			order.setProduct(cart.getProduct());
			order.setPrice(cart.getProduct().getDiscountPrice());
			
			order.setQuantity(cart.getQuantity());
			order.setUser(cart.getUser());
			
			order.setStatus(OrderStatus.IN_PROGRESS.getName());
			order.setPaymentType(orderRequest.getPaymentType());
			
			 // Set Razorpay details if online payment
            if("ONLINE".equals(orderRequest.getPaymentType())) {
                order.setRazorpayOrderId(orderRequest.getRazorpayOrderId());
                order.setRazorpayPaymentId(orderRequest.getRazorpayPaymentId());
                order.setRazorpaySignature(orderRequest.getRazorpaySignature());
            }

			
			OrderAddress address = new OrderAddress();
			address.setFirstName(orderRequest.getFirstName());
			address.setLastName(orderRequest.getLastName());
			address.setEmail(orderRequest.getEmail());
			address.setMobileNo(orderRequest.getMobileNo());
			address.setAddress(orderRequest.getAddress());
			address.setCity(orderRequest.getCity());
			address.setState(orderRequest.getState());
			address.setPincode(orderRequest.getPincode());
			
			order.setOrderAddress(address);
			ProductOrder saveOrder = orderRepository.save(order);
		//	commonUtil.sendMailForProductOrder(saveOrder, "success");
			
			

			try {
			    commonUtil.sendMailForProductOrder(saveOrder, "success");
			} catch (Exception e) {
			    e.printStackTrace();
			    System.out.println("Mail sending failed but order saved successfully");
			}
			
			 // Clear cart after order
            //cartRepository.deleteAll(carts);

		}
		// Clear cart after order
        cartRepository.deleteAll(carts);
       
	}
	
    
    

	@Override
	public List<ProductOrder> getOrdersByUser(Integer userId) {
		List<ProductOrder> orders = orderRepository.findByUserId(userId);
		return orders;
		
	}

	@Override
	public ProductOrder updateOrderStatus(Integer id, String status) {
		Optional<ProductOrder> findById = orderRepository.findById(id);
		if(findById.isPresent()) {
			ProductOrder productOrder = findById.get();
			productOrder.setStatus(status);
			ProductOrder updateOrder = orderRepository.save(productOrder);
			return updateOrder;
		}
		return null;
	}

	@Override
	public List<ProductOrder> getAllOrders() {
		
		return orderRepository.findAll();
	}

	@Override
	public ProductOrder getOrdersByOrderId(String orderId) {
		// TODO Auto-generated method stub
		return orderRepository.findByOrderId(orderId);
	}

	@Override
	public Page<ProductOrder> getAllOrdersPagination(Integer pageNo, Integer pageSize) {
		Pageable pageable = PageRequest.of(pageNo, pageSize);
		return orderRepository.findAll(pageable);
	}

	 @Override
	    public ProductOrder getOrderByRazorpayOrderId(String razorpayOrderId) {
	        return orderRepository.findByRazorpayOrderId(razorpayOrderId);
	    }
	    
	    @Override
	    public void updatePaymentDetails(Integer orderId, String razorpayOrderId, String razorpayPaymentId, String razorpaySignature) {
	        Optional<ProductOrder> findById = orderRepository.findById(orderId);
	        if(findById.isPresent()) {
	            ProductOrder order = findById.get();
	            order.setRazorpayOrderId(razorpayOrderId);
	            order.setRazorpayPaymentId(razorpayPaymentId);
	            order.setRazorpaySignature(razorpaySignature);
	            orderRepository.save(order);
	        }
	    }




		@Override
		public long getTotalOrderCount() {
			// TODO Auto-generated method stub
			return orderRepository.count();
		}




		@Override
		public long getOrderCountByStatus(String status) {
			// TODO Auto-generated method stub
			return orderRepository.countByStatus(status);
		}
	
}
