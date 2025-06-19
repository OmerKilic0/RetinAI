package com.grad.drds.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.grad.drds.repository.ReportRequestRepository;
import com.grad.drds.repository.UserRepository;

@Controller
@RequestMapping("/admin")
public class AdminController {

	private UserRepository userRepository;
	private ReportRequestRepository reportRepository;

	public AdminController(UserRepository userRepository, ReportRequestRepository reportRepository) {
		this.userRepository = userRepository;
		this.reportRepository = reportRepository;
	}
	
    @GetMapping("/manage-users")
    public String showManageUsers(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "manage-users";
    }

    @PostMapping("/delete-user/{id}")
    public String deleteUser(@PathVariable int id) {
        userRepository.deleteById(id);
        return "redirect:/admin/manage-users";
    }
    
    @GetMapping("/manage-reports")
    public String showManageReports(Model model) {
        model.addAttribute("reports", reportRepository.findAll());
        return "manage-reports";
    }

    @PostMapping("/delete-report/{id}")
    public String deleteReport(@PathVariable int id) {
        reportRepository.deleteById(id);
        return "redirect:/admin/manage-reports";
    }
}
