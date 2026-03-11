package com.sanyark.leavemanagement.config;

import com.sanyark.leavemanagement.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/manager/**").hasRole("MANAGER")
                        .requestMatchers("/api/dashboard/manager").hasRole("MANAGER")
                        .requestMatchers("/api/dashboard/employee").hasAnyRole("EMPLOYEE", "MANAGER")
                        .requestMatchers("/api/notifications/**").hasAnyRole("EMPLOYEE", "MANAGER")
                        .requestMatchers("/api/leaves/**").hasAnyRole("EMPLOYEE", "MANAGER")
                        .requestMatchers("/api/leave-balances/my").hasAnyRole("EMPLOYEE", "MANAGER")
                        .requestMatchers("/api/leave-balances/employee/**").hasRole("MANAGER")
                        .requestMatchers("/api/calendar/team").hasRole("MANAGER")
                        .requestMatchers("/api/calendar/**").hasAnyRole("EMPLOYEE", "MANAGER")
                        .requestMatchers("/api/holidays/**").authenticated()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}