package com.grad.drds.controller;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.grad.drds.entity.User_;
import com.grad.drds.repository.UserRepository;

@Controller
public class AuthController {
	
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
    
    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }
    
    @GetMapping("/signup")
    public String showSignupPage() {
    	return "signup";
    }
    
    @PostMapping("/signup")
    public String processSignup(@RequestParam String name, @RequestParam String surname, @RequestParam String email,
            @RequestParam String password, @RequestParam String tckn, RedirectAttributes redirectAttributes) {
    	
    	if (userRepository.existsByEmail(email)) {
            redirectAttributes.addFlashAttribute("errorMessage", "This email is already registered.");
            return "redirect:/signup";
        }
    	
    	if (!isValidPassword(password)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Password must be at least 8 characters long, include uppercase and lowercase letters, and contain at least one number.");
            return "redirect:/signup";
        }
    	
    	if(tckn.length() != 11) {
    		redirectAttributes.addFlashAttribute("errorMessage", "Tckn must be 8 characters long.");
            return "redirect:/signup";
    	}
    	
        User_ user = new User_();
        user.setName(name);
        user.setSurname(surname);
        user.setEmail(email);
        user.setTckn(tckn);
        user.setPassword(passwordEncoder.encode(password));
        user.setAdmin(false);

        userRepository.save(user);
        redirectAttributes.addFlashAttribute("successMessage", "Your account has been created successfully.");
        return "redirect:/login";
    }
    
    private boolean isValidPassword(String password) {
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$";
        return password.matches(regex);
    }
}