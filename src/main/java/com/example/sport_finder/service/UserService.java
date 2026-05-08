package com.sportfinder.service;

import java.time.LocalDate;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sportfinder.dto.ProfileUpdateDto;
import com.sportfinder.dto.SignupDto;
import com.sportfinder.model.User;
import com.sportfinder.model.UserProfile;
import com.sportfinder.repository.UserProfileRepository;
import com.sportfinder.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final PasswordEncoder passwordEncoder;

    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    @Transactional
    public User register(SignupDto dto) {
        if (emailExists(dto.getEmail())) {
            throw new IllegalArgumentException("Email already registered.");
        }

        User.Role role = switch (dto.getRole().trim().toUpperCase().replace(" ", "_")) {
            case "COACH"       -> User.Role.COACH;
            case "COURT_OWNER" -> User.Role.COURT_OWNER;
            case "ADMIN"       -> User.Role.ADMIN;
            default            -> User.Role.TRAINEE;
        };

        User user = User.builder()
                .fullName(dto.getFullName())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(role)
                .isActive(true)
                .build();

        User saved = userRepository.save(user);

        // Create an empty profile row in user_profiles
        UserProfile profile = UserProfile.builder()
                .user(saved)
                .phone("")
                .bio("")
                .location("")
                .avatar("")
                .joinedDate(LocalDate.now())
                .build();
        userProfileRepository.save(profile);

        return saved;
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + email));
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
    }

    @Transactional
    public void updateProfile(Long userId, ProfileUpdateDto dto) {
        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User u = findById(userId);
                    return UserProfile.builder().user(u).joinedDate(LocalDate.now()).build();
                });
        profile.setPhone(dto.getPhone() != null ? dto.getPhone() : "");
        profile.setLocation(dto.getLocation() != null ? dto.getLocation() : "");
        profile.setBio(dto.getBio() != null ? dto.getBio() : "");
        userProfileRepository.save(profile);

        // Also update fullName on users table
        User user = findById(userId);
        if (dto.getFullName() != null && !dto.getFullName().isBlank()) {
            user.setFullName(dto.getFullName());
            userRepository.save(user);
        }

        // Update password if provided
        if (dto.getNewPassword() != null && !dto.getNewPassword().isBlank()) {
            if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
                throw new IllegalArgumentException("Current password is incorrect");
            }
            if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
                throw new IllegalArgumentException("New passwords do not match");
            }
            user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
            userRepository.save(user);
        }
    }
}
