package com.sportfinder.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Maps to the real `training_bookings` table.
 *
 * Real columns:
 *   id, training_id, trainee_id, preferred_date, preferred_time,
 *   sessions_count, level_requested,
 *   price_per_session, total_price,
 *   message, status (Pending|Accepted|Declined|Cancelled|Modified),
 *   attendance_confirmed, confirmed_by, confirmed_at,
 *   created_at, updated_at, coach_note
 */
@Entity
@Table(name = "training_bookings")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TrainingBooking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "training_id", nullable = false)
    private Training training;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainee_id", nullable = false)
    private User trainee;

    @Column(name = "preferred_date", nullable = false)
    private LocalDate preferredDate;

    @Column(name = "preferred_time", nullable = false)
    private LocalTime preferredTime;

    @Column(name = "sessions_count")
    private Integer sessionsCount;

    @Column(name = "level_requested")
    private String levelRequested;

    @Column(name = "price_per_session")
    private BigDecimal pricePerSession;

    @Column(name = "total_price")
    private BigDecimal totalPrice;

    @Column(columnDefinition = "TEXT")
    private String message;

    /** ENUM: 'Pending','Accepted','Declined','Cancelled','Modified' — Title-case! */
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

    @Column(name = "coach_note", columnDefinition = "TEXT")
    private String coachNote;

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
