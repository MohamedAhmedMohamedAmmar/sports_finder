package com.example.sport_finder.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Maps to the real `courts` table.
 *
 * Real columns:
 *   id, owner_id, sport_id, name, court_type(enum indoor/outdoor),
 *   price_per_hour, address, city, description, image_url, created_at
 *
 * Schedule is in the separate `court_schedule` table (CourtSchedule entity).
 * Amenities join through `court_amenities` (court_id, amenity_id).
 */
@Entity
@Table(name = "courts")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder

public class Court {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sport_id", nullable = false)
    private Sport sport;

    /**
     * Real DB: ENUM('indoor','outdoor') — lowercase.
     * We store as STRING and normalise on read.
     */
    @Column(name = "court_type", nullable = false)
    private String courtType;   // "indoor" | "outdoor"

    @DecimalMin("0.01")
    @Column(name = "price_per_hour", nullable = false)
    private BigDecimal pricePerHour;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String city;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "image_url")
    private String imageUrl;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /** Many-to-many via court_amenities join table. */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "court_amenities",
        joinColumns        = @JoinColumn(name = "court_id"),
        inverseJoinColumns = @JoinColumn(name = "amenity_id")
    )
    @Builder.Default
    private Set<Amenity> amenities = new HashSet<>();

    /** Convenience: capitalised type for display. */
    public String getCourtTypeDisplay() {
        if (courtType == null) return "";
        return courtType.substring(0, 1).toUpperCase() + courtType.substring(1).toLowerCase();
    }
}
