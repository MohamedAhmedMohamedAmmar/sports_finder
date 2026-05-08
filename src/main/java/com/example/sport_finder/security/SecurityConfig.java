package com.sportfinder.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Real DB uses $2y$ (PHP BCrypt) which is identical to $2a$ — Java reads it fine.
        return new BCryptPasswordEncoder(10);
    }

    @Bean
    public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authenticationProvider(authProvider())
            .authorizeHttpRequests(auth -> auth
                // ── Public ──────────────────────────────────────────────
                .requestMatchers(
                    "/", "/login", "/signup",
                    "/browse", "/browse-sessions",
                    "/courts/**", "/sessions/**", "/trainers/**",
                    "/css/**", "/js/**", "/images/**", "/error"
                ).permitAll()

                // ── Court Owner ─────────────────────────────────────────
                .requestMatchers("/my-courts/**", "/courts/create", "/courts/edit/**",
                                 "/owner/profile", "/owner/profile/update")
                    .hasRole("COURT_OWNER")

                // ── Coach ────────────────────────────────────────────────
                .requestMatchers("/my-trainings/**", "/trainings/create", "/trainings/delete/**",
                                 "/coach/profile", "/coach/profile/update")
                    .hasRole("COACH")

                // ── Any authenticated user ────────────────────────────
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .usernameParameter("email")
                .passwordParameter("password")
                // Redirect based on role after successful login
                .successHandler((req, res, auth) -> {
                    String role = auth.getAuthorities().iterator().next().getAuthority();
                    switch (role) {
                        case "ROLE_COURT_OWNER" -> res.sendRedirect("/my-courts");
                        case "ROLE_COACH"       -> res.sendRedirect("/my-trainings");
                        case "ROLE_ADMIN"       -> res.sendRedirect("/my-courts");
                        default                 -> res.sendRedirect("/profile");
                    }
                })
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            );

        return http.build();
    }
}
