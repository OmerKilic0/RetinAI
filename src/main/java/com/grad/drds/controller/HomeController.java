package com.grad.drds.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.grad.drds.entity.User_;
import com.grad.drds.service.UserService;

@Controller
public class HomeController {

	private UserService userService;
	
	public HomeController(UserService userService) {
		this.userService = userService;
	}
	
	@GetMapping("/")
	public String mainPage(Model model) {
		return "main-page";
	}
	
	@GetMapping("/detection")
	public String showDetectionPage(Model model) {
		User_ user = userService.authenticateUser();
		model.addAttribute("fullName", user.getName() + " " + user.getSurname());
		model.addAttribute("currentUser", user);
		return "detection";
	}
}
