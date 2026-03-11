package com.sanyark.leavemanagement.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {
    private Long id;
    private String employeeCode;
    private String fullName;
    private String email;
    private String role;
    private String gender;
}