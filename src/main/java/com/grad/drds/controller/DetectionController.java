package com.grad.drds.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.grad.drds.entity.User_;
import com.grad.drds.service.UserService;

@Controller
public class DetectionController {

	private UserService userService;
	
	public DetectionController(UserService userService) {
		this.userService = userService;
	}
	
	@GetMapping("/result")
    public String showDetectionResult(Model model) {
		User_ user = userService.authenticateUser();
		model.addAttribute("fullName", user.getName() + " " + user.getSurname());
//        model.addAttribute("imageUrl", imageUrl);  // Image URL to display
//        model.addAttribute("severityLevel", severityLevel);  // Severity level of the result
//        model.addAttribute("analysisNotes", analysisNotes);  // Detailed analysis notes
        model.addAttribute("pdfDownloadUrl", generatePdfDownloadUrl());  // URL for PDF download
        return "result";
    }

    private String generatePdfDownloadUrl() {
        return "/path/to/generated/analysis.pdf";
    }
}
