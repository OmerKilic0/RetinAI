package com.grad.drds.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CustomErrorController {

    @RequestMapping("/error-custom")
    public String handleError(Model model, @RequestParam(value = "msg", required = false) String msg) {
        model.addAttribute("errorMessage", msg != null ? msg : "An unexpected error occurred.");
        return "error";
    }
}
