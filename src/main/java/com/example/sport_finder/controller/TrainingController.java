package com.sportfinder.controller;

import java.util.List;

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
import com.sportfinder.dto.TrainingBookingForm;
import com.sportfinder.dto.TrainingForm;
import com.sportfinder.model.Training;
import com.sportfinder.model.TrainingBooking;
import com.sportfinder.model.User;
import com.sportfinder.service.BookingService;
import com.sportfinder.service.CourtService;
import com.sportfinder.service.EmailService;
import com.sportfinder.service.TrainingService;
import com.sportfinder.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class TrainingController {

    private final TrainingService trainingService;
    private final BookingService  bookingService;
    private final CourtService    courtService;
    private final UserService     userService;
    private final EmailService    emailService;

    @GetMapping("/sessions/{id}")
    public String viewTraining(@PathVariable Long id, Model model,
                               @AuthenticationPrincipal UserDetails principal) {
        Training training = trainingService.getById(id);
        model.addAttribute("training",    training);
        model.addAttribute("bookingForm", new TrainingBookingForm());
        if (principal != null) {
            model.addAttribute("currentUser", userService.findByEmail(principal.getUsername()));
        }
        return "trainings/view-training";
    }

    @GetMapping("/trainers/{id}")
    public String viewTrainer(@PathVariable Long id, Model model) {
        User trainer  = userService.findById(id);
        var  trainings = trainingService.getTrainingsByCoach(trainer);
        model.addAttribute("trainer",   trainer);
        model.addAttribute("trainings", trainings);
        return "trainings/view-trainer-profile";
    }

    @GetMapping("/trainings/create")
    public String createTrainingForm(Model model) {
        model.addAttribute("trainingForm", new TrainingForm());
        model.addAttribute("sports",       courtService.getAllSports());
        return "trainings/create-training";
    }

    @PostMapping("/trainings/create")
    public String saveTraining(@Valid @ModelAttribute TrainingForm trainingForm,
                               BindingResult result,
                               @AuthenticationPrincipal UserDetails principal,
                               RedirectAttributes redirect, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("sports", courtService.getAllSports());
            return "trainings/create-training";
        }
        User coach = userService.findByEmail(principal.getUsername());
        try {
            trainingService.create(trainingForm, coach);
            redirect.addFlashAttribute("success", "Training session created!");
        } catch (Exception e) {
            model.addAttribute("error",  e.getMessage());
            model.addAttribute("sports", courtService.getAllSports());
            return "trainings/create-training";
        }
        return "redirect:/my-trainings";
    }

    @PostMapping("/trainings/delete/{id}")
    public String deleteTraining(@PathVariable Long id,
                                 @AuthenticationPrincipal UserDetails principal,
                                 RedirectAttributes redirect) {
        User coach = userService.findByEmail(principal.getUsername());
        try {
            Training training = trainingService.getById(id);
            List<TrainingBooking> bookings = bookingService.getTrainingBookingsByTraining(training);
            
            // Soft delete: mark training and bookings as deleted
            trainingService.delete(id, coach);
            bookingService.markTrainingBookingsAsDeleted(training);
            redirect.addFlashAttribute("success", "Training cancelled.");
            
            // Send cancellation emails to all trainees with pending/accepted bookings
            for (TrainingBooking booking : bookings) {
                String status = booking.getStatus();
                if ("Pending".equals(status) || "Accepted".equals(status)) {
                    emailService.sendTrainingDeletedEmail(
                        booking.getTrainee().getEmail(),
                        training.getName(),
                        training.getCoach().getFullName()
                    );
                }
            }
        } catch (Exception e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/my-trainings";
    }

    @GetMapping("/coach/profile")
    public String coachProfile(Model model, @AuthenticationPrincipal UserDetails principal) {
        User coach = userService.findByEmail(principal.getUsername());
        
        // Pre-fill the form with current coach data
        ProfileUpdateDto dto = new ProfileUpdateDto();
        dto.setFullName(coach.getFullName());
        if (coach.getProfile() != null) {
            dto.setPhone(coach.getProfile().getPhone());
            dto.setLocation(coach.getProfile().getLocation());
            dto.setBio(coach.getProfile().getBio());
        }
        
        var coachBookings = bookingService.getTrainingBookingsByCoach(coach);
        double earnings = coachBookings.stream()
                .filter(b -> !"Cancelled".equals(b.getStatus()) && !"Declined".equals(b.getStatus()))
                .mapToDouble(b -> b.getTotalPrice() != null ? b.getTotalPrice().doubleValue() : 0)
                .sum();
        model.addAttribute("user",              coach);
        model.addAttribute("profileUpdateDto",  dto);
        model.addAttribute("trainings",        trainingService.getTrainingsByCoach(coach));
        model.addAttribute("bookings",         coachBookings);
        model.addAttribute("totalEarnings",    earnings);
        return "trainings/coach-profile";
    }

    @PostMapping("/coach/profile/update")
    public String updateCoachProfile(@Valid @ModelAttribute ProfileUpdateDto dto,
                                     BindingResult result,
                                     @AuthenticationPrincipal UserDetails principal,
                                     RedirectAttributes redirect, Model model) {
        User coach = userService.findByEmail(principal.getUsername());
        if (result.hasErrors()) {
            var errBookings = bookingService.getTrainingBookingsByCoach(coach);
            double errEarnings = errBookings.stream()
                    .filter(b -> !"Cancelled".equals(b.getStatus()) && !"Declined".equals(b.getStatus()))
                    .mapToDouble(b -> b.getTotalPrice() != null ? b.getTotalPrice().doubleValue() : 0)
                    .sum();
            model.addAttribute("user",          coach);
            model.addAttribute("trainings",     trainingService.getTrainingsByCoach(coach));
            model.addAttribute("bookings",      errBookings);
            model.addAttribute("totalEarnings", errEarnings);
            return "trainings/coach-profile";
        }
        try {
            boolean passwordChanged = dto.getNewPassword() != null && !dto.getNewPassword().isBlank();
            userService.updateProfile(coach.getId(), dto);
            redirect.addFlashAttribute("success", "Profile updated successfully.");
            
            // Send password change confirmation email
            if (passwordChanged) {
                emailService.sendPasswordResetConfirmation(coach.getEmail(), coach.getFullName());
            }
        } catch (Exception e) {
            var exBookings = bookingService.getTrainingBookingsByCoach(coach);
            double exEarnings = exBookings.stream()
                    .filter(b -> !"Cancelled".equals(b.getStatus()) && !"Declined".equals(b.getStatus()))
                    .mapToDouble(b -> b.getTotalPrice() != null ? b.getTotalPrice().doubleValue() : 0)
                    .sum();
            model.addAttribute("user",          coach);
            model.addAttribute("error",         e.getMessage());
            model.addAttribute("trainings",     trainingService.getTrainingsByCoach(coach));
            model.addAttribute("bookings",      exBookings);
            model.addAttribute("totalEarnings", exEarnings);
            return "trainings/coach-profile";
        }
        return "redirect:/coach/profile";
    }

    @PostMapping("/bookings/training")
    public String bookTraining(@Valid @ModelAttribute TrainingBookingForm form,
                               BindingResult result,
                               @AuthenticationPrincipal UserDetails principal,
                               RedirectAttributes redirect) {
        if (result.hasErrors()) {
            redirect.addFlashAttribute("bookingError", "Please fix form errors.");
            return "redirect:/sessions/" + form.getTrainingId();
        }
        User trainee = userService.findByEmail(principal.getUsername());
        try {
            bookingService.bookTraining(form, trainee);
            redirect.addFlashAttribute("success", "Booking submitted!");
        } catch (Exception e) {
            redirect.addFlashAttribute("bookingError", e.getMessage());
        }
        return "redirect:/sessions/" + form.getTrainingId();
    }

    @GetMapping("/my-trainings")
    public String myTrainings(Model model, @AuthenticationPrincipal UserDetails principal) {
        User coach     = userService.findByEmail(principal.getUsername());
        var  trainings = trainingService.getTrainingsByCoach(coach);
        var  bookings  = bookingService.getTrainingBookingsByCoach(coach);
        double earnings = bookings.stream()
                .filter(b -> !"Cancelled".equals(b.getStatus()) && !"Declined".equals(b.getStatus()))
                .mapToDouble(b -> b.getTotalPrice() != null ? b.getTotalPrice().doubleValue() : 0)
                .sum();
        model.addAttribute("trainings",     trainings);
        model.addAttribute("bookings",      bookings);
        model.addAttribute("totalEarnings", earnings);
        return "trainings/my-trainings";
    }

    @PostMapping("/my-trainings/booking/{id}/{action}")
    public String updateTrainingBooking(@PathVariable Long id,
                                        @PathVariable String action,
                                        @AuthenticationPrincipal UserDetails principal,
                                        RedirectAttributes redirect) {
        User coach = userService.findByEmail(principal.getUsername());
        try {
            String status = switch (action) {
                case "accept"  -> "Accepted";
                case "decline" -> "Declined";
                case "cancel"  -> "Cancelled";
                default -> throw new IllegalArgumentException("Invalid action");
            };
            bookingService.updateTrainingBookingStatus(id, coach, status);
            redirect.addFlashAttribute("success", "Booking updated.");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/my-trainings";
    }
}
