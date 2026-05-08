package com.sportfinder.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TrainingBookingForm {

    @NotNull
    private Long trainingId;

    @NotBlank(message = "Preferred date is required")
    private String preferredDate;  // yyyy-MM-dd

    @NotBlank(message = "Preferred time is required")
    private String preferredTime;  // HH:mm

    @Min(value = 1, message = "At least 1 session required")
    private int sessionsCount;

    private String levelRequested;
    private String message;
}
