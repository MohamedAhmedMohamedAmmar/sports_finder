package com.example.sport_finder.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Maps to the real `trainings` table.
 *
 * Real columns:
 *   id, coach_id, sport_id, name,
 *   level ENUM('Beginner','Intermediate','Advanced','Professional','All Levels'),
 *   specialty, price_per_hour, years_experience, description, goals,
 *   min_sessions, location, equipment, requirements,
 *   certifications, achievements, image_url,
 *   status ENUM('Active','Inactive'), created_at
 *
 * Schedule is in the separate `training_availability` table.
 */
@Entity
@Table(name = "trainings")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Training {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coach_id", nullable = false)
    private User coach;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sport_id", nullable = false)
    private Sport sport;

    /** ENUM: 'Beginner','Intermediate','Advanced','Professional','All Levels' */
    @Column(nullable = false)
    private String level;

    private String specialty;

    @DecimalMin("0.01")
    @Column(name = "price_per_hour", nullable = false)
    private BigDecimal pricePerHour;

    @Column(name = "years_experience")
    private Integer yearsExperience;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String goals;

    @Column(name = "min_sessions")
    private Integer minSessions;

    private String location;

    @Column(columnDefinition = "TEXT")
    private String equipment;

    @Column(columnDefinition = "TEXT")
    private String requirements;

    @Column(columnDefinition = "TEXT")
    private String certifications;

    @Column(columnDefinition = "TEXT")
    private String achievements;

    @Column(name = "image_url")
    private String imageUrl;

    /** ENUM: 'Active','Inactive' — Title-case! */
    @Column(nullable = false)
    @Builder.Default
    private String status = "Active";

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
