package com.sanyark.leavemanagement.controller;

import com.sanyark.leavemanagement.dto.ApiResponse;
import com.sanyark.leavemanagement.dto.RegisterUserRequest;
import com.sanyark.leavemanagement.dto.UserResponse;
import com.sanyark.leavemanagement.enums.Gender;
import com.sanyark.leavemanagement.enums.Role;
import com.sanyark.leavemanagement.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/manager/users")
@RequiredArgsConstructor
public class ManagerUserController {

    private final UserService userService;

    @PostMapping
    public ApiResponse<UserResponse> registerUser(@Valid @RequestBody RegisterUserRequest request) {
        UserResponse response = userService.registerUser(request);

        return ApiResponse.<UserResponse>builder()
                .success(true)
                .message("User registered successfully")
                .data(response)
                .build();
    }

    @GetMapping
    public ApiResponse<List<UserResponse>> getUsers(
            @RequestParam(required = false) Role role,
            @RequestParam(required = false) Gender gender,
            @RequestParam(required = false) String search
    ) {
        return ApiResponse.<List<UserResponse>>builder()
                .success(true)
                .message("Users fetched successfully")
                .data(userService.getUsers(role, gender, search))
                .build();
    }
}