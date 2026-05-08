package com.sportfinder.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CourtBookingForm {

    @NotNull
    private Long courtId;

    @NotBlank(message = "Booking date is required")
    private String bookingDate;   // yyyy-MM-dd

    @NotBlank(message = "Start time is required")
    private String startTime;     // HH:mm

    @NotBlank(message = "End time is required")
    private String endTime;       // HH:mm

    @Min(value = 1) @Max(value = 50)
    private int playersCount;

    @NotBlank(message = "Contact phone is required")
    private String contactPhone;

    private String notes;
    
}
