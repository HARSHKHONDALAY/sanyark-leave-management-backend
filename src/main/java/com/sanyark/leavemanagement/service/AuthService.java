package com.sanyark.leavemanagement.service;

import com.sanyark.leavemanagement.dto.LoginRequest;
import com.sanyark.leavemanagement.dto.LoginResponse;


public interface AuthService {
    LoginResponse login(LoginRequest request);
}