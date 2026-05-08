package com.example.sport_finder.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Async("taskExecutor")
    public void sendTrainingDeletedEmail(String recipientEmail, String trainingName, String coachName) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(recipientEmail);
            message.setSubject("Training Session Cancelled - " + trainingName);
            message.setText("Hello,\n\n" +
                    "The training session \"" + trainingName + "\" by coach " + coachName + " has been cancelled.\n\n" +
                    "If you had any bookings for this session, they have been automatically cancelled.\n" +
                    "Please check your profile for more details.\n\n" +
                    "Best regards,\n" +
                    "SportFinder Team");
            message.setFrom("noreply@sportfinder.com");
            
            mailSender.send(message);
            log.info("Training deletion email sent to: {}", recipientEmail);
        } catch (Exception e) {
            log.error("Failed to send training deletion email to: {}", recipientEmail, e);
        }
    }

    @Async("taskExecutor")
    public void sendPasswordResetConfirmation(String email, String userName) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Password Updated - SportFinder");
            message.setText("Hello " + userName + ",\n\n" +
                    "Your password has been successfully updated.\n\n" +
                    "If you didn't make this change, please contact our support team immediately.\n\n" +
                    "Best regards,\n" +
                    "SportFinder Team");
            message.setFrom("noreply@sportfinder.com");
            
            mailSender.send(message);
            log.info("Password reset confirmation email sent to: {}", email);
        } catch (Exception e) {
            log.error("Failed to send password reset email to: {}", email, e);
        }
    }

    @Async("taskExecutor")
    public void sendWelcomeEmail(String email, String fullName) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Welcome to SportFinder!");
            message.setText("Hi " + fullName + ",\n\n" +
                    "Thank you for registering with SportFinder. " +
                    "You can now book courts and trainers in your area.\n\n" +
                    "Best regards,\n" +
                    "SportFinder Team");
            message.setFrom("noreply@sportfinder.com");
            
            mailSender.send(message);
            log.info("Welcome email sent to: {}", email);
        } catch (Exception e) {
            log.error("Failed to send welcome email to: {}", email, e);
        }
    }

    @Async("taskExecutor")
    public void sendBookingConfirmation(String email, String bookingDetails) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Booking Confirmation");
            message.setText("Your booking has been confirmed!\n\n" +
                    bookingDetails + "\n\n" +
                    "Thank you for using SportFinder!");
            message.setFrom("noreply@sportfinder.com");
            
            mailSender.send(message);
            log.info("Booking confirmation sent to: {}", email);
        } catch (Exception e) {
            log.error("Failed to send booking confirmation to: {}", email, e);
        }
    }
}