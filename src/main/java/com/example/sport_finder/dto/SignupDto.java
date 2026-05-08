package com.example.sport_finder.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SignupDto {

    @NotBlank(message = "Full name is required")
    @Size(min = 3, message = "Name must be at least 3 characters")
    private String fullName;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "Role is required")
    private String role;  // "TRAINEE", "COACH", "COURT_OWNER"
}
