package com.sportfinder.model;

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
 * Maps to the real table `checkboxes_incourt_creation`
 * (amenity_name, is_active).
 */
@Entity
@Table(name = "checkboxes_incourt_creation")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Amenity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "amenity_name", nullable = false, unique = true)
    private String amenityName;
    
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean isActive = true;
}
