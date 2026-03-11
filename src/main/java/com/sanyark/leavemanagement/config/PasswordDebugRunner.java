package com.sanyark.leavemanagement.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordDebugRunner implements CommandLineRunner {

    private final PasswordEncoder passwordEncoder;

    public PasswordDebugRunner(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        String rawPassword = "password123";
        String hash = passwordEncoder.encode(rawPassword);

        System.out.println("====================================");
        System.out.println("Generated BCrypt hash for password123:");
        System.out.println(hash);
        System.out.println("Matches password123: " + passwordEncoder.matches("password123", hash));
        System.out.println("====================================");
    }
}