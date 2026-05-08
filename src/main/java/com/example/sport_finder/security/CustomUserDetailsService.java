package com.example.sport_finder.security;

import com.example.sport_finder.model.User;
import com.example.sport_finder.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        // Guard: inactive users cannot log in
        if (!user.isActive()) {
            throw new UsernameNotFoundException("Account is disabled: " + email);
        }

        // Real DB role values: TRAINEE, COACH, COURT_OWNER, ADMIN
        // Spring Security expects ROLE_ prefix
        String authority = "ROLE_" + user.getRole().name();

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),          // BCrypt hash stored in DB
                List.of(new SimpleGrantedAuthority(authority))
        );
    }
}
