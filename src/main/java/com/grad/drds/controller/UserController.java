package com.grad.drds.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.grad.drds.entity.User_;
import com.grad.drds.service.UserService;

@Controller
@RequestMapping("/")
public class UserController {

	@Autowired
    private UserService userService;
	private PasswordEncoder passwordEncoder;
	
	public UserController(UserService userService, PasswordEncoder passwordEncoder) {
		this.userService = userService;
		this.passwordEncoder = passwordEncoder;
	}
	
	@GetMapping("/profile")
	public String showUserProfile(Model model) {
	    User_ user = userService.authenticateUser();
	    model.addAttribute("fullName", user.getName() + " " + user.getSurname());
	    model.addAttribute("profile", user);
	    return "profile";
	}
	
	@PostMapping("/update-profile")
	public String updateProfile(@RequestParam String name, @RequestParam String surname,
	                            @RequestParam(required = false) String oldPassword,
	                            @RequestParam(required = false) String newPassword,
	                            RedirectAttributes redirectAttributes) {
	    User_ user = userService.authenticateUser();

	    user.setName(name.trim());
	    user.setSurname(surname.trim());

	    if (oldPassword != null && !oldPassword.isBlank()) {
	        if (passwordEncoder.matches(oldPassword, user.getPassword())) {
	            // Yeni şifre geçerli mi?
	            if (!isValidPassword(newPassword)) {
	                redirectAttributes.addFlashAttribute("errorMessage", 
	                    "Password must be at least 8 characters long, include uppercase and lowercase letters, and contain at least one number.");
	                return "redirect:/profile";
	            }

	            user.setPassword(passwordEncoder.encode(newPassword));
	            redirectAttributes.addFlashAttribute("successMessage", "Profile and password updated successfully.");
	        } else {
	            redirectAttributes.addFlashAttribute("errorMessage", "Old password is incorrect.");
	            return "redirect:/profile";
	        }
	    } else {
	        redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully.");
	    }

	    userService.saveUser(user);
	    return "redirect:/profile";
	}
	
	private boolean isValidPassword(String password) {
	    String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$";
	    return password != null && password.matches(regex);
	}
}