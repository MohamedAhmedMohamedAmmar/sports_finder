package com.sportfinder.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sportfinder.dto.ProfileUpdateDto;
import com.sportfinder.model.User;
import com.sportfinder.service.BookingService;
import com.sportfinder.service.EmailService;
import com.sportfinder.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ProfileController {

    private final UserService   userService;
    private final BookingService bookingService;
    private final EmailService emailService;

    @GetMapping("/profile")
    public String profile(Model model, @AuthenticationPrincipal UserDetails principal) {
        User user = userService.findByEmail(principal.getUsername());
        
        // Pre-fill the form with current user data
        ProfileUpdateDto dto = new ProfileUpdateDto();
        dto.setFullName(user.getFullName());
        if (user.getProfile() != null) {
            dto.setPhone(user.getProfile().getPhone());
            dto.setLocation(user.getProfile().getLocation());
            dto.setBio(user.getProfile().getBio());
        }
        
        model.addAttribute("user",              user);
        model.addAttribute("profileUpdateDto",  dto);
        model.addAttribute("courtBookings",     bookingService.getCourtBookingsByTrainee(user));
        model.addAttribute("trainingBookings",  bookingService.getTrainingBookingsByTrainee(user));
        return "profile/profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@Valid @ModelAttribute ProfileUpdateDto dto,
                                BindingResult result,
                                @AuthenticationPrincipal UserDetails principal,
                                RedirectAttributes redirect, Model model) {
        User user = userService.findByEmail(principal.getUsername());
        if (result.hasErrors()) {
            model.addAttribute("user",             user);
            model.addAttribute("courtBookings",    bookingService.getCourtBookingsByTrainee(user));
            model.addAttribute("trainingBookings", bookingService.getTrainingBookingsByTrainee(user));
            return "profile/profile";
        }
        try {
            boolean passwordChanged = dto.getNewPassword() != null && !dto.getNewPassword().isBlank();
            userService.updateProfile(user.getId(), dto);
            redirect.addFlashAttribute("success", "Profile updated successfully.");
            
            // Send password change confirmation email
            if (passwordChanged) {
                emailService.sendPasswordResetConfirmation(user.getEmail(), user.getFullName());
            }
        } catch (Exception e) {
            model.addAttribute("user",             user);
            model.addAttribute("error",            e.getMessage());
            model.addAttribute("courtBookings",    bookingService.getCourtBookingsByTrainee(user));
            model.addAttribute("trainingBookings", bookingService.getTrainingBookingsByTrainee(user));
            return "profile/profile";
        }
        return "redirect:/profile";
    }

    @PostMapping("/profile/cancel-court/{bookingId}")
    public String cancelCourtBooking(@PathVariable Long bookingId,
                                     @AuthenticationPrincipal UserDetails principal,
                                     RedirectAttributes redirect) {
        User user = userService.findByEmail(principal.getUsername());
        try {
            bookingService.cancelCourtBooking(bookingId, user);
            redirect.addFlashAttribute("success", "Booking cancelled.");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/profile";
    }

    @PostMapping("/profile/cancel-training/{bookingId}")
    public String cancelTrainingBooking(@PathVariable Long bookingId,
                                        @AuthenticationPrincipal UserDetails principal,
                                        RedirectAttributes redirect) {
        User user = userService.findByEmail(principal.getUsername());
        try {
            bookingService.cancelTrainingBooking(bookingId, user);
            redirect.addFlashAttribute("success", "Booking cancelled.");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/profile";
    }
}
