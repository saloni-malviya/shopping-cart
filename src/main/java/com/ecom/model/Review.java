package com.ecom.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Review {
	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Integer id;
	    
	    @ManyToOne
	    private UserDtls user;
	    
	    @ManyToOne
	    private Product product;
	    
	    @Column(length = 1000)
	    private String reviewText;
	    
	    private Integer rating;  // 1 to 5 stars
	    
	    private LocalDateTime reviewDate;
	    
	    private Boolean isApproved;  // Admin approval ke liye (optional)
	
}
