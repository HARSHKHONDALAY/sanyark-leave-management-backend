package com.sanyark.leavemanagement.service;

import com.sanyark.leavemanagement.dto.LoginRequest;
import com.sanyark.leavemanagement.dto.LoginResponse;
import com.sanyark.leavemanagement.entity.User;
import com.sanyark.leavemanagement.exception.UnauthorizedActionException;
import com.sanyark.leavemanagement.repository.UserRepository;
import com.sanyark.leavemanagement.security.CustomUserDetails;
import com.sanyark.leavemanagement.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Override
    public LoginResponse login(LoginRequest request) {

        // find user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedActionException("Invalid credentials"));

        // DEBUG LOGS (temporary)
        System.out.println("====================================");
        System.out.println("Login attempt");
        System.out.println("Email from request: " + request.getEmail());
        System.out.println("Raw password from request: " + request.getPassword());
        System.out.println("Stored password hash: " + user.getPasswordHash());

        boolean passwordMatches =
                passwordEncoder.matches(request.getPassword(), user.getPasswordHash());

        System.out.println("Password matches result: " + passwordMatches);
        System.out.println("====================================");

        // password validation
        if (!passwordMatches) {
            throw new UnauthorizedActionException("Invalid credentials");
        }

        // create JWT token
        CustomUserDetails userDetails = new CustomUserDetails(user);
        String token = jwtUtil.generateToken(userDetails);

        // return login response
        return LoginResponse.builder()
                .success(true)
                .message("Login successful")
                .token(token)
                .userId(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }
}