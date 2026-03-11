package com.sanyark.leavemanagement.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {
    private boolean success;
    private String message;
    private String token;
    private Long userId;
    private String fullName;
    private String email;
    private String role;
}