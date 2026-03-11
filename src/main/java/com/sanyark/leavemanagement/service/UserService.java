package com.sanyark.leavemanagement.service;

import com.sanyark.leavemanagement.dto.RegisterUserRequest;
import com.sanyark.leavemanagement.dto.UserResponse;
import com.sanyark.leavemanagement.enums.Gender;
import com.sanyark.leavemanagement.enums.Role;

import java.util.List;

public interface UserService {
    UserResponse registerUser(RegisterUserRequest request);
    List<UserResponse> getUsers(Role role, Gender gender, String search);
}