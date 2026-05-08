package com.example.sport_finder.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Maps to the real `court_bookings` table.
 *
 * Real columns:
 *   id, court_id, trainee_id, booking_date, start_time, end_time,
 *   duration_hours, players_count, total_price, contact_phone, notes,
 *   status(Pending|Accepted|Declined|Cancelled|Modified),
 *   attendance_confirmed, confirmed_by, confirmed_at,
 *   created_at, updated_at
 */
@Entity
@Table(name = "court_bookings")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder

public class CourtBooking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "court_id", nullable = false)
    private Court court;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainee_id", nullable = false)
    private User trainee;

    @Column(name = "booking_date", nullable = false)
    private LocalDate bookingDate;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "duration_hours")
    private BigDecimal durationHours;

    @Column(name = "players_count")
    private Integer playersCount;

    @Column(name = "total_price", nullable = false)
    private BigDecimal totalPrice;

    @Column(name = "contact_phone")
    private String contactPhone;

    @Column(columnDefinition = "TEXT")
    private String notes;

    /**
     * Real ENUM: 'Pending','Accepted','Declined','Cancelled','Modified'
     * Note: Title-case, not upper-case!
     */
    @Column(nullable = false)
    @Builder.Default
    private String status = "Pending";

    @Column(name = "attendance_confirmed", nullable = false)
    @Builder.Default
    private boolean attendanceConfirmed = false;

    @Column(name = "confirmed_by")
    private Long confirmedBy;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /** Returns CSS class based on status for template styling. */
    public String getStatusClass() {
        if (status == null) return "status-pending"; // Default if status is null
        return switch (status.toUpperCase()) {
            case "ACCEPTED" -> "status-confirmed";
            case "PENDING" -> "status-pending";
            case "DECLINED" -> "status-declined";
            case "CANCELLED" -> "status-cancelled";
            default -> "status-pending"; // Fallback for any unexpected status
        };
    }
}
