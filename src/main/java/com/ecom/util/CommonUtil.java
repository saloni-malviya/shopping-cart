package com.ecom.util;

import java.io.UnsupportedEncodingException;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.ecom.model.ProductOrder;
import com.ecom.model.UserDtls;
import com.ecom.service.BrevoEmailService; 
import com.ecom.service.UserService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class CommonUtil {

	//@Autowired
	//private JavaMailSender mailSender;

	@Autowired
	private UserService userService;
	@Autowired
    private BrevoEmailService brevoEmailService;
	

	/*public Boolean sendMail(String url, String reciepantEmail) throws UnsupportedEncodingException, MessagingException {

		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);

		helper.setFrom("salonimalviya03@gmail.com", "Shopping Cart");
		helper.setTo(reciepantEmail);

		String content = "<p>Hello,</p>" + "<p>You have requested to reset your password.</p>"
				+ "<p>Click the link below to change your password:</p>" + "<p><a href=\"" + url
				+ "\">Change my password</a></p>";

		helper.setSubject("Password Reset");
		helper.setText(content, true);
		mailSender.send(message);
		return true;
	}*/
	// 🔥 SEND MAIL METHOD - AB API USE KAREGA
	public Boolean sendMail(String url, String recipientEmail) {
	    try {
	        // ✅ Proper HTML document
	        String content = "<!DOCTYPE html>"
	            + "<html>"
	            + "<head>"
	            + "<meta charset='UTF-8'>"
	            + "<style>"
	            + "body { font-family: Arial, sans-serif; }"
	            + ".btn { background-color: #4f46e5; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px; display: inline-block; }"
	            + "</style>"
	            + "</head>"
	            + "<body>"
	            + "<h2>🔐 Password Reset Request</h2>"
	            + "<p>Hello,</p>"
	            + "<p>You have requested to reset your password for your Ecom Store account.</p>"
	            + "<p>Click the button below to reset your password:</p>"
	            + "<p><a href='" + url + "' class='btn'>Reset Password</a></p>"
	            + "<p>This link will expire in <b>24 hours</b>.</p>"
	            + "<hr>"
	            + "<p>If you did not request this, please ignore this email.</p>"
	            + "<p>Regards,<br>Ecom Store Team</p>"
	            + "</body>"
	            + "</html>";
	        
	        return brevoEmailService.sendEmail(recipientEmail, "Password Reset - Ecom Store", content);
	    } catch (Exception e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	

	public static String generateUrl(HttpServletRequest request) {
		String siteUrl = request.getRequestURL().toString();
		return siteUrl.replace(request.getServletPath(), "");
	}

	String msg = null;

	/*public Boolean sendMailForProductOrder(ProductOrder order, String status) throws Exception {
		msg = "<p>Hello [[name]],</p>" + "<p>Thank you for shopping with us.</p>"
				+ "<p>Your order has been placed successfully.</p>" + "<p>Order Status:<b>[[orderStatus]]</b></p>"
				+ "<p><b>Product Details:</b></p>" + "<p>Name : [[productName]]</p>" + "<p>Category : [[category]]</p>"
				+ "<p>Quantity : [[quantity]]</p>" + "<p>Price : [[price]]</p>"
				+ "<p>Payment Type : [[paymentType]]</p>";

		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);

		helper.setFrom("salonimalviya03@gmail.com", "Shopping Cart");
		helper.setTo(order.getOrderAddress().getEmail());

		
		msg = msg.replace("[[name]]", order.getOrderAddress().getFirstName());
		msg = msg.replace("[[orderStatus]]", status);
		msg = msg.replace("[[productName]]", order.getProduct().getTitle());
		msg = msg.replace("[[category]]", order.getProduct().getCategory());
		msg = msg.replace("[[quantity]]", order.getQuantity().toString());
		msg = msg.replace("[[price]]", order.getPrice().toString());
		msg = msg.replace("[[paymentType]]", order.getPaymentType());

		helper.setSubject("Product Order Status");
		helper.setText(msg, true);
		mailSender.send(message);
		return true;
	}*/

	public Boolean sendMailForProductOrder(ProductOrder order, String status) {
	    try {
	        String content = "<!DOCTYPE html>"
	            + "<html>"
	            + "<head><meta charset='UTF-8'></head>"
	            + "<body>"
	            + "<h2>✅ Order Confirmation</h2>"
	            + "<p>Hello <b>" + order.getOrderAddress().getFirstName() + "</b>,</p>"
	            + "<p>Thank you for shopping with us!</p>"
	            + "<p><b>Order Status:</b> " + status + "</p>"
	            + "<hr>"
	            + "<h3>📦 Product Details:</h3>"
	            + "<table border='1' cellpadding='8' style='border-collapse:collapse;'>"
	            + "<tr><th>Product</th><td>" + order.getProduct().getTitle() + "</td></tr>"
	            + "<tr><th>Category</th><td>" + order.getProduct().getCategory() + "</td></tr>"
	            + "<tr><th>Quantity</th><td>" + order.getQuantity() + "</td></tr>"
	            + "<tr><th>Price</th><td>₹" + order.getPrice() + "</td></tr>"
	            + "<tr><th>Payment Type</th><td>" + order.getPaymentType() + "</td></tr>"
	            + "</table>"
	            + "<hr>"
	            + "<p>Your order will be delivered within <b>7 days</b>.</p>"
	            + "<p>Thank you for shopping with us!</p>"
	            + "<p>Regards,<br>Ecom Store Team</p>"
	            + "</body>"
	            + "</html>";
	        
	        return brevoEmailService.sendEmail(
	            order.getOrderAddress().getEmail(),
	            "Order Confirmation - #" + order.getOrderId(),
	            content
	        );
	    } catch (Exception e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	
	public UserDtls getLoggedInUserDetails(Principal p) {
		String email = p.getName();
		UserDtls userDtls = userService.getUserByEmail(email);
		return userDtls;
	}

}
