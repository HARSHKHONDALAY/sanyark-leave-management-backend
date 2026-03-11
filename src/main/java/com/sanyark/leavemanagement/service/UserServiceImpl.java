package com.sanyark.leavemanagement.service;

import com.sanyark.leavemanagement.dto.RegisterUserRequest;
import com.sanyark.leavemanagement.dto.UserResponse;
import com.sanyark.leavemanagement.entity.User;
import com.sanyark.leavemanagement.enums.Gender;
import com.sanyark.leavemanagement.enums.Role;
import com.sanyark.leavemanagement.exception.InvalidLeaveActionException;
import com.sanyark.leavemanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse registerUser(RegisterUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new InvalidLeaveActionException("Email is already in use");
        }

        if (userRepository.existsByEmployeeCode(request.getEmployeeCode())) {
            throw new InvalidLeaveActionException("Employee code is already in use");
        }

        User user = User.builder()
                .employeeCode(request.getEmployeeCode())
                .fullName(request.getFullName())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .gender(request.getGender())
                .build();

        User savedUser = userRepository.save(user);

        return mapToResponse(savedUser);
    }

    @Override
    public List<UserResponse> getUsers(Role role, Gender gender, String search) {
        List<User> users = userRepository.findAll();

        if (role != null) {
            users = users.stream()
                    .filter(user -> user.getRole() == role)
                    .toList();
        }

        if (gender != null) {
            users = users.stream()
                    .filter(user -> user.getGender() == gender)
                    .toList();
        }

        if (search != null && !search.trim().isEmpty()) {
            String searchLower = search.trim().toLowerCase();

            users = users.stream()
                    .filter(user ->
                            user.getFullName().toLowerCase().contains(searchLower)
                                    || user.getEmail().toLowerCase().contains(searchLower)
                                    || user.getEmployeeCode().toLowerCase().contains(searchLower)
                    )
                    .toList();
        }

        return users.stream()
                .map(this::mapToResponse)
                .toList();
    }

    private UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .employeeCode(user.getEmployeeCode())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .gender(user.getGender().name())
                .build();
    }
}