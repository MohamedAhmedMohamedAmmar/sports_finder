package com.example.sport_finder.dto;

import java.math.BigDecimal;
import java.util.List;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class TrainingForm {

    @NotBlank(message = "Training name is required")
    @Size(min = 4, message = "Name must be at least 4 characters")
    private String name;

    @NotNull(message = "Sport is required")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long sportId;

    @NotBlank(message = "Level is required")
    private String level;

    @NotBlank(message = "Specialty is required")
    @Size(min = 3, message = "Specialty must be at least 3 characters")
    private String specialty;

    @NotNull @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal pricePerHour;

    @Min(value = 0, message = "Years of experience must be 0 or more")
    private Integer yearsExperience;

    @NotBlank @Size(min = 20, message = "Description must be at least 20 characters")
    private String description;

    @NotBlank @Size(min = 10, message = "Goals must be at least 10 characters")
    private String goals;

    private String startTime;
    private String endTime;

    @Min(value = 1, message = "Minimum sessions must be at least 1")
    private Integer minSessions;

    @NotBlank(message = "Location is required")
    private String location;

    private String equipment;
    private String requirements;
    private String certifications;
    private String achievements;
    private String imageUrl;

    private List<String> availability;
}