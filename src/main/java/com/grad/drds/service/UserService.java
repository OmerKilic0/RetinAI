package com.grad.drds.service;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.grad.drds.entity.User_;
import com.grad.drds.repository.UserRepository;

@Service
public class UserService {

	private UserRepository userRepository;
	
	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	public User_ findUserById(int id) {
		return userRepository.findById(id);
	}
	
	public User_ findByEmail(String email) {
		return userRepository.findByEmail(email);
	}
	
	public User_ saveUser(User_ user) {
		return userRepository.save(user);
	}
	
	public List<User_> getAllUsers(){
		return userRepository.findAll();
	}
	
	public User_ authenticateUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    
	    if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
	        return null;
	    }

	    String email = auth.getName();
	    return userRepository.findByEmail(email);
	}
}
