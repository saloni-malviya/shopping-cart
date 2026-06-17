package com.ecom.service.impl;


import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import com.ecom.model.UserDtls;
import com.ecom.repository.UserRepository;
import com.ecom.service.CloudinaryImageService;
import com.ecom.service.UserService;
import com.ecom.util.AppConstant;
import com.ecom.util.FileUploadUtil;

@Service
public class UserServiceImpl implements UserService{
	
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private CloudinaryImageService cloudinaryImageService;

	@Override
	public UserDtls saveUser(UserDtls user) {
		
		user.setRole("ROLE_USER");
		user.setIsEnable(true);
		user.setAccountNonLocked(true);
		user.setFailedAttempt(0);
		
		
		String encodePassword = passwordEncoder.encode(user.getPassword());
		user.setPassword(encodePassword);
		UserDtls saveUser = userRepository.save(user);
		return saveUser;
	}

	@Override
	public UserDtls getUserByEmail(String email) {
		
		return userRepository.findByEmail(email);
	}

	@Override
	public List<UserDtls> getUsers(String role) {
		return userRepository.findByRole(role);
		
	}

	@Override
	public Boolean updateAccountStatus(Integer id, Boolean status) {
		Optional<UserDtls> findByuser = userRepository.findById(id);
		if(findByuser.isPresent()) {
			UserDtls userDtls = findByuser.get();
			userDtls.setIsEnable(status);
			userRepository.save(userDtls);
			return true;
		}
		return false;
	}

	@Override
	public void increaseFailedAttempt(UserDtls user) {
		int attempt = user.getFailedAttempt() +1;
		user.setFailedAttempt(attempt);
		userRepository.save(user);
		
	}

	@Override
	public void userAccountLock(UserDtls user) {
		user.setAccountNonLocked(false);
		user.setLockTime(new Date());
		userRepository.save(user);
		
	}

	@Override
	public boolean unlockAccountTimeExpired(UserDtls user) {
		long lockTime = user.getLockTime().getTime();
		long unLockTime = lockTime + AppConstant.UNLOCK_DURATION_TIME;
		
		long currentTime = System.currentTimeMillis();
		
		if(unLockTime < currentTime) {
			user.setAccountNonLocked(true);
			user.setFailedAttempt(0);
			user.setLockTime(null);
			userRepository.save(user);
			return true;
		}
		return false;
	}

	@Override
	public void resetAttempt(int userId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateUserResetToken(String email, String resetToken) {
		UserDtls findByEmail = userRepository.findByEmail(email);
		findByEmail.setResetToken(resetToken);
		userRepository.save(findByEmail);
		
	}

	@Override
	public UserDtls getUserByToken(String token) {
		return userRepository.findByResetToken(token);
		
	}

	@Override
	public UserDtls updateUser(UserDtls user) {
		return userRepository.save(user);
	
	}

	@Override
	public UserDtls updateUserProfile(UserDtls user, MultipartFile img) {
		UserDtls dbUser = userRepository.findById(user.getId()).get();
	//	if(!img.isEmpty()) {
	//		dbUser.setProfileImage(img.getOriginalFilename());
	//	}
		
		if(!ObjectUtils.isEmpty(dbUser)) {
			dbUser.setName(user.getName());
			dbUser.setMobileNumber(user.getMobileNumber());
			dbUser.setAddress(user.getAddress());
			dbUser.setCity(user.getCity());
			dbUser.setState(user.getState());
			dbUser.setPincode(user.getPincode());
		//	dbUser=userRepository.save(dbUser);
		}
		try {
		/*	if(!img.isEmpty()) {
				// FileUploadUtil ka method use kar - jo external folder me save karega
		        String savedImageName = FileUploadUtil.saveImage(img, "profile_img");
		        dbUser.setProfileImage(savedImageName);  // Naya image name set kar
			//	File saveFile = new ClassPathResource("static/img").getFile();
			//	Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "profile_img" + File.separator + img.getOriginalFilename());
			//	System.out.println(path);
				//Files.copy(img.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			}*/
			if(!img.isEmpty()) {
	            // 🔥 Cloudinary pe upload karo
	            String publicId = cloudinaryImageService.uploadImage(img, "profile_img");
	            if(publicId != null) {
	                dbUser.setProfileImage(publicId);
	            }
	        }
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		dbUser=userRepository.save(dbUser);
		return dbUser;
	}

	@Override
	public UserDtls saveAdmin(UserDtls user) {
		user.setRole("ROLE_ADMIN");
		user.setIsEnable(true);
		user.setAccountNonLocked(true);
		user.setFailedAttempt(0);
		
		
		String encodePassword = passwordEncoder.encode(user.getPassword());
		user.setPassword(encodePassword);
		UserDtls saveUser = userRepository.save(user);
		return saveUser;
		
	}

	@Override
	public Boolean existsEmail(String email) {
		
		return userRepository.existsByEmail(email);
	}

	@Override
	public long getTotalUserCount(String role) {
		// TODO Auto-generated method stub
		return userRepository.countByRole(role);
	}

	@Override
	public long getActiveUserCount(String role) {
		// TODO Auto-generated method stub
		return userRepository.countByRoleAndIsEnable(role, true);
	}

	@Override
	public long getInactiveUserCount(String role) {
		// TODO Auto-generated method stub
		return userRepository.countByRoleAndIsEnable(role, false);
	}

}
