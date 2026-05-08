package com.example.sport_finder.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.sport_finder.dto.SignupDto;
import com.example.sport_finder.model.User;
import com.example.sport_finder.service.EmailService;
import com.example.sport_finder.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final EmailService emailService;

    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String error, Model model) {
        if (error != null) model.addAttribute("error", "Invalid email or password.");
        return "auth/login";
    }

    @GetMapping("/signup")
    public String signupPage(Model model) {
        model.addAttribute("signupDto", new SignupDto());
        return "auth/signup";
    }

    @PostMapping("/signup")
    public String signup(@Valid @ModelAttribute SignupDto signupDto,
                         BindingResult result,
                         RedirectAttributes redirectAttrs,
                         Model model) {
        if (result.hasErrors()) {
            return "auth/signup";
        }
        if (userService.emailExists(signupDto.getEmail())) {
            model.addAttribute("error", "Email is already registered.");
            return "auth/signup";
        }
        try {
            User registeredUser = userService.register(signupDto);
            // Send welcome email asynchronously
            emailService.sendWelcomeEmail(registeredUser.getEmail(), registeredUser.getFullName());
            redirectAttrs.addFlashAttribute("success", "Account created! Please log in.");
            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "auth/signup";
        }
    }
}
