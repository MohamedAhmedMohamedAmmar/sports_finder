package com.sportfinder.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CourtForm {

    private Long id; // for edit mode

    @NotBlank(message = "Court name is required")
    @Size(min = 4, message = "Court name must be at least 4 characters")
    private String name;

    @NotNull(message = "Sport is required")
    private Long sportId;

    @NotBlank(message = "Court type is required")
    private String courtType; // INDOOR / OUTDOOR

    @NotNull @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal pricePerHour;

    @NotBlank(message = "Address is required")
    @Size(min = 5, message = "Address must be at least 5 characters")
    private String address;

    @NotBlank(message = "City is required")
    private String city;

    private String description;
    private String imageUrl;
    private String openTime;
    private String closeTime;

    private List<String> daysOpen;
    private List<Long> amenityIds;
}
