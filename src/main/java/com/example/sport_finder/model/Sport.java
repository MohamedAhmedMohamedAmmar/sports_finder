package com.example.sport_finder.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Maps to the real `sports` table (has is_active column).
 */
@Entity
@Table(name = "sports")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder

public class Sport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sport_name", nullable = false, unique = true)
    private String sportName;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean isActive = true;
}
