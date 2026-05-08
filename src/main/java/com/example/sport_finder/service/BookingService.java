package com.sportfinder.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sportfinder.dto.CourtBookingForm;
import com.sportfinder.dto.TrainingBookingForm;
import com.sportfinder.model.Court;
import com.sportfinder.model.CourtBooking;
import com.sportfinder.model.Training;
import com.sportfinder.model.TrainingBooking;
import com.sportfinder.model.User;
import com.sportfinder.repository.CourtBookingRepository;
import com.sportfinder.repository.CourtRepository;
import com.sportfinder.repository.TrainingBookingRepository;
import com.sportfinder.repository.TrainingRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final CourtBookingRepository    courtBookingRepo;
    private final TrainingBookingRepository trainingBookingRepo;
    private final CourtRepository           courtRepo;
    private final TrainingRepository        trainingRepo;

    // ─── Status constants (must match real DB ENUM: Title-case) ──────────
    private static final String PENDING   = "Pending";
    private static final String ACCEPTED  = "Accepted";
    private static final String DECLINED  = "Declined";
    private static final String CANCELLED = "Cancelled";

    @Transactional
    public CourtBooking bookCourt(CourtBookingForm form, User trainee) {
        Court court = courtRepo.findById(form.getCourtId())
                .orElseThrow(() -> new IllegalArgumentException("Court not found"));

        LocalDate date  = LocalDate.parse(form.getBookingDate());
        LocalTime start = LocalTime.parse(form.getStartTime());
        LocalTime end   = LocalTime.parse(form.getEndTime());

        if (!end.isAfter(start)) throw new IllegalArgumentException("End time must be after start time.");
        if (date.isBefore(LocalDate.now())) throw new IllegalArgumentException("Booking date must be today or in the future.");

        List<CourtBooking> conflicts = courtBookingRepo.findConflicting(court, date, start, end);
        if (!conflicts.isEmpty()) throw new IllegalArgumentException("This time slot is already booked.");

        long minutes = ChronoUnit.MINUTES.between(start, end);
        // 2.33
        BigDecimal hours      = BigDecimal.valueOf(minutes).divide(BigDecimal.valueOf(60), 2, java.math.RoundingMode.HALF_UP);
        BigDecimal totalPrice = court.getPricePerHour().multiply(hours);

        CourtBooking booking = CourtBooking.builder()
                .court(court)
                .trainee(trainee)
                .bookingDate(date)
                .startTime(start)
                .endTime(end)
                .durationHours(hours)
                .playersCount(form.getPlayersCount())
                .contactPhone(form.getContactPhone())
                .notes(form.getNotes())
                .totalPrice(totalPrice)
                .status(PENDING)
                .build();

        return courtBookingRepo.save(booking);
    }

    @Transactional
    public void updateCourtBookingStatus(Long bookingId, User owner, String newStatus) {
        List<CourtBooking> ownerBookings = courtBookingRepo.findByCourtOwner(owner);
        CourtBooking booking = ownerBookings.stream()
                .filter(b -> b.getId().equals(bookingId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Booking not found or access denied"));
        String current = booking.getStatus();
        if (ACCEPTED.equals(newStatus) || DECLINED.equals(newStatus)) {
            if (!PENDING.equals(current)) {
                throw new IllegalArgumentException("Only Pending bookings can be accepted or declined.");
            }
        }
        if (CANCELLED.equals(newStatus) && !ACCEPTED.equals(current)) {
            throw new IllegalArgumentException("Only Accepted bookings can be cancelled by the owner.");
        }
        booking.setStatus(newStatus);
        courtBookingRepo.save(booking);
    }

    @Transactional
    public void cancelCourtBooking(Long bookingId, User trainee) {
        CourtBooking booking = courtBookingRepo.findByIdAndTraineeId(bookingId, trainee.getId())
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
        if (!PENDING.equals(booking.getStatus()) && !ACCEPTED.equals(booking.getStatus())) {
            throw new IllegalArgumentException("Only Pending or Accepted bookings can be cancelled.");
        }
        booking.setStatus(CANCELLED);
        courtBookingRepo.save(booking);
    }

    public List<CourtBooking> getCourtBookingsByOwner(User owner) {
        return courtBookingRepo.findByCourtOwner(owner);
    }

    public List<CourtBooking> getCourtBookingsByTrainee(User trainee) {
        return courtBookingRepo.findByTraineeOrderByCreatedAtDesc(trainee);
    }

    // ════════════════════════════════════════════════════════════════════
    //  TRAINING BOOKINGS
    // ════════════════════════════════════════════════════════════════════

    @Transactional
    public TrainingBooking bookTraining(TrainingBookingForm form, User trainee) {
        Training training = trainingRepo.findById(form.getTrainingId())
                .orElseThrow(() -> new IllegalArgumentException("Training not found"));

        LocalDate date = LocalDate.parse(form.getPreferredDate());
        if (date.isBefore(LocalDate.now())) throw new IllegalArgumentException("Date must be today or in the future.");

        BigDecimal pricePerSession = training.getPricePerHour();
        BigDecimal totalPrice      = pricePerSession.multiply(BigDecimal.valueOf(form.getSessionsCount()));

        TrainingBooking booking = TrainingBooking.builder()
                .training(training)
                .trainee(trainee)
                .preferredDate(date)
                .preferredTime(LocalTime.parse(form.getPreferredTime()))
                .sessionsCount(form.getSessionsCount())
                .levelRequested(form.getLevelRequested())
                .pricePerSession(pricePerSession)
                .totalPrice(totalPrice)
                .message(form.getMessage())
                .status(PENDING)
                .build();

        return trainingBookingRepo.save(booking);
    }

    @Transactional
    public void updateTrainingBookingStatus(Long bookingId, User coach, String newStatus) {
        TrainingBooking booking = trainingBookingRepo.findByIdAndCoach(bookingId, coach)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found or access denied"));
        String current = booking.getStatus();
        if (ACCEPTED.equals(newStatus) || DECLINED.equals(newStatus)) {
            if (!PENDING.equals(current)) {
                throw new IllegalArgumentException("Only Pending bookings can be accepted or declined.");
            }
        }
        if (CANCELLED.equals(newStatus) && !ACCEPTED.equals(current)) {
            throw new IllegalArgumentException("Only Accepted bookings can be cancelled by the coach.");
        }
        booking.setStatus(newStatus);
        trainingBookingRepo.save(booking);
    }

    @Transactional
    public void cancelTrainingBooking(Long bookingId, User trainee) {
        TrainingBooking booking = trainingBookingRepo.findByIdAndTraineeId(bookingId, trainee.getId())
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
        if (!PENDING.equals(booking.getStatus()) && !ACCEPTED.equals(booking.getStatus())) {
            throw new IllegalArgumentException("Only Pending or Accepted bookings can be cancelled.");
        }
        booking.setStatus(CANCELLED);
        trainingBookingRepo.save(booking);
    }

    public List<TrainingBooking> getTrainingBookingsByCoach(User coach) {
        return trainingBookingRepo.findByCoach(coach);
    }

    public List<TrainingBooking> getTrainingBookingsByTrainee(User trainee) {
        return trainingBookingRepo.findByTraineeOrderByCreatedAtDesc(trainee);
    }

    public List<TrainingBooking> getTrainingBookingsByTraining(Training training) {
        return trainingBookingRepo.findByTraining(training);
    }

    @Transactional
    public void markTrainingBookingsAsDeleted(Training training) {
        List<TrainingBooking> bookings = trainingBookingRepo.findByTraining(training);
        for (TrainingBooking booking : bookings) {
            booking.setStatus("Deleted");
            trainingBookingRepo.save(booking);
        }
    }
}
