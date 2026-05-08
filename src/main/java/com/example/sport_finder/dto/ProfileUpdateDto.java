package com.example.sport_finder.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProfileUpdateDto {

    @NotBlank(message = "Full name is required")
    private String fullName;

    private String phone;

    private String location;

    private String bio;

    private String currentPassword;

    private String newPassword;

    private String confirmPassword;
}
