package com.tu.votingapp.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final TokenAuthenticationFilter tokenAuthenticationFilter; // Inject your custom filter

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF for stateless APIs (common for token auth)
                .csrf(csrf -> csrf.disable())
                // Configure session management to be stateless
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Configure authorization rules
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/users/login").permitAll() // Allow login
                        .requestMatchers("/api/users").permitAll()       // Allow user creation (registration)
                        // Add other public endpoints here (e.g., actuator health)
                        // .requestMatchers("/actuator/**").permitAll()
                        .anyRequest().authenticated() // Require authentication for all other requests
                );
        // Add custom exception handling if needed (e.g., for 401 Unauthorized)
        // .exceptionHandling(exceptions -> exceptions
        //     .authenticationEntryPoint(yourAuthenticationEntryPoint)
        // );

        // --- Add your custom filter BEFORE the standard UsernamePasswordAuthenticationFilter ---
        http.addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
