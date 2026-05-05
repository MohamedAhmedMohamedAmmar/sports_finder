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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sportfinder.dto.CourtBookingForm;
import com.sportfinder.dto.CourtForm;
import com.sportfinder.dto.ProfileUpdateDto;
import com.sportfinder.model.Court;
import com.sportfinder.model.User;
import com.sportfinder.service.BookingService;
import com.sportfinder.service.CourtService;
import com.sportfinder.service.EmailService;
import com.sportfinder.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class CourtController {

    private final CourtService    courtService;
    private final BookingService  bookingService;
    private final UserService     userService;
    private final EmailService    emailService;

    // ── View single court ────────────────────────────────────────────────
    @GetMapping("/courts/{id}")
    public String viewCourt(@PathVariable Long id, Model model,
                            @AuthenticationPrincipal UserDetails principal) {
        Court court = courtService.getCourtById(id);
        model.addAttribute("court", court);
        model.addAttribute("bookingForm", new CourtBookingForm());
        if (principal != null) {
            model.addAttribute("currentUser", userService.findByEmail(principal.getUsername()));
        }
        return "courts/view-court";
    }

    // ── Create / Edit form ────────────────────────────────────────────────
    @GetMapping("/courts/create")
    public String createCourtForm(Model model,
                                  @RequestParam(required = false) Long courtId,
                                  @AuthenticationPrincipal UserDetails principal) {
        User owner = userService.findByEmail(principal.getUsername());
        CourtForm form = new CourtForm();

        if (courtId != null) {
            Court existing = courtService.getCourtById(courtId);
            if (existing.getOwner().getId().equals(owner.getId())) {
                form.setId(existing.getId());
                form.setName(existing.getName());
                form.setSportId(existing.getSport() != null ? existing.getSport().getId() : null);
                form.setCourtType(existing.getCourtType());
                form.setPricePerHour(existing.getPricePerHour());
                form.setAddress(existing.getAddress());
                form.setCity(existing.getCity());
                form.setDescription(existing.getDescription());
                form.setImageUrl(existing.getImageUrl());
            }
        }

        model.addAttribute("courtForm", form);
        model.addAttribute("sports",    courtService.getAllSports());
        model.addAttribute("amenities", courtService.getAllAmenities());
        model.addAttribute("editMode",  courtId != null);
        return "courts/create-court";
    }

    @PostMapping("/courts/create")
    public String saveCourt(@Valid @ModelAttribute CourtForm courtForm,
                            BindingResult result,
                            @AuthenticationPrincipal UserDetails principal,
                            RedirectAttributes redirect, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("sports",    courtService.getAllSports());
            model.addAttribute("amenities", courtService.getAllAmenities());
            return "courts/create-court";
        }
        User owner = userService.findByEmail(principal.getUsername());
        try {
            if (courtForm.getId() != null) {
                courtService.updateCourt(courtForm.getId(), courtForm, owner);
                redirect.addFlashAttribute("success", "Court updated successfully.");
            } else {
                courtService.createCourt(courtForm, owner);
                redirect.addFlashAttribute("success", "Court created successfully.");
            }
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("sports",    courtService.getAllSports());
            model.addAttribute("amenities", courtService.getAllAmenities());
            return "courts/create-court";
        }
        return "redirect:/my-courts";
    }

    @PostMapping("/courts/delete/{id}")
    public String deleteCourt(@PathVariable Long id,
                              @AuthenticationPrincipal UserDetails principal,
                              RedirectAttributes redirect) {
        User owner = userService.findByEmail(principal.getUsername());
        try {
            courtService.deleteCourt(id, owner);
            redirect.addFlashAttribute("success", "Court deleted.");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/my-courts";
    }

    // ── Book a court ──────────────────────────────────────────────────────
    @PostMapping("/bookings/court")
    public String bookCourt(@Valid @ModelAttribute CourtBookingForm form,
                            BindingResult result,
                            @AuthenticationPrincipal UserDetails principal,
                            RedirectAttributes redirect) {
        if (result.hasErrors()) {
            redirect.addFlashAttribute("bookingError", "Please fix form errors.");
            return "redirect:/courts/" + form.getCourtId();
        }
        User trainee = userService.findByEmail(principal.getUsername());
        try {
            bookingService.bookCourt(form, trainee);
            redirect.addFlashAttribute("success", "Booking submitted!");
        } catch (Exception e) {
            redirect.addFlashAttribute("bookingError", e.getMessage());
        }
        return "redirect:/courts/" + form.getCourtId();
    }

    // ── My Courts dashboard ───────────────────────────────────────────────
    @GetMapping("/my-courts")
    public String myCourts(Model model, @AuthenticationPrincipal UserDetails principal) {
        User owner    = userService.findByEmail(principal.getUsername());
        var  courts   = courtService.getCourtsByOwner(owner);
        var  bookings = bookingService.getCourtBookingsByOwner(owner);
        double revenue = bookings.stream()
                .filter(b -> !"Cancelled".equals(b.getStatus()) && !"Declined".equals(b.getStatus()))
                .mapToDouble(b -> b.getTotalPrice() != null ? b.getTotalPrice().doubleValue() : 0)
                .sum();
        model.addAttribute("courts",       courts);
        model.addAttribute("bookings",     bookings);
        model.addAttribute("totalRevenue", revenue);
        return "courts/my-courts";
    }
    @GetMapping("/owner/profile")
    public String ownerProfile(Model model, @AuthenticationPrincipal UserDetails principal) {
        User owner = userService.findByEmail(principal.getUsername());
        
        // Pre-fill the form with current owner data
        ProfileUpdateDto dto = new ProfileUpdateDto();
        dto.setFullName(owner.getFullName());
        if (owner.getProfile() != null) {
            dto.setPhone(owner.getProfile().getPhone());
            dto.setLocation(owner.getProfile().getLocation());
            dto.setBio(owner.getProfile().getBio());
        }
        
        var ownerBookings = bookingService.getCourtBookingsByOwner(owner);
        double revenue = ownerBookings.stream()
                .filter(b -> !"Cancelled".equals(b.getStatus()) && !"Declined".equals(b.getStatus()))
                .mapToDouble(b -> b.getTotalPrice() != null ? b.getTotalPrice().doubleValue() : 0)
                .sum();
        model.addAttribute("user",              owner);
        model.addAttribute("profileUpdateDto",  dto);
        model.addAttribute("courts",           courtService.getCourtsByOwner(owner));
        model.addAttribute("bookings",         ownerBookings);
        model.addAttribute("totalRevenue",     revenue);
        return "courts/owner-profile";
    }

    @PostMapping("/owner/profile/update")
    public String updateOwnerProfile(@Valid @ModelAttribute ProfileUpdateDto dto,
                                     BindingResult result,
                                     @AuthenticationPrincipal UserDetails principal,
                                     RedirectAttributes redirect, Model model) {
        User owner = userService.findByEmail(principal.getUsername());
        if (result.hasErrors()) {
            var errBookings = bookingService.getCourtBookingsByOwner(owner);
            double errRevenue = errBookings.stream()
                    .filter(b -> !"Cancelled".equals(b.getStatus()) && !"Declined".equals(b.getStatus()))
                    .mapToDouble(b -> b.getTotalPrice() != null ? b.getTotalPrice().doubleValue() : 0)
                    .sum();
            model.addAttribute("user",          owner);
            model.addAttribute("courts",        courtService.getCourtsByOwner(owner));
            model.addAttribute("bookings",      errBookings);
            model.addAttribute("totalRevenue",  errRevenue);
            return "courts/owner-profile";
        }
        try {
            boolean passwordChanged = dto.getNewPassword() != null && !dto.getNewPassword().isBlank();
            userService.updateProfile(owner.getId(), dto);
            redirect.addFlashAttribute("success", "Profile updated successfully.");
            
            // Send password change confirmation email
            if (passwordChanged) {
                emailService.sendPasswordResetConfirmation(owner.getEmail(), owner.getFullName());
            }
        } catch (Exception e) {
            var exBookings = bookingService.getCourtBookingsByOwner(owner);
            double exRevenue = exBookings.stream()
                    .filter(b -> !"Cancelled".equals(b.getStatus()) && !"Declined".equals(b.getStatus()))
                    .mapToDouble(b -> b.getTotalPrice() != null ? b.getTotalPrice().doubleValue() : 0)
                    .sum();
            model.addAttribute("user",          owner);
            model.addAttribute("error",         e.getMessage());
            model.addAttribute("courts",        courtService.getCourtsByOwner(owner));
            model.addAttribute("bookings",      exBookings);
            model.addAttribute("totalRevenue",  exRevenue);
            return "courts/owner-profile";
        }
        return "redirect:/owner/profile";
    }
    // ── Accept / Decline / Cancel court booking ───────────────────────────
    @PostMapping("/my-courts/booking/{id}/{action}")
    public String updateCourtBooking(@PathVariable Long id,
                                     @PathVariable String action,
                                     @AuthenticationPrincipal UserDetails principal,
                                     RedirectAttributes redirect) {
        User owner = userService.findByEmail(principal.getUsername());
        try {
            String status = switch (action) {
                case "accept"  -> "Accepted";
                case "decline" -> "Declined";
                case "cancel"  -> "Cancelled";
                default -> throw new IllegalArgumentException("Invalid action");
            };
            bookingService.updateCourtBookingStatus(id, owner, status);
            redirect.addFlashAttribute("success", "Booking updated.");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/my-courts";
    }
}
