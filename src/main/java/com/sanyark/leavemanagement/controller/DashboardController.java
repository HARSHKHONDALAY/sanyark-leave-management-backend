package com.sanyark.leavemanagement.controller;

import com.sanyark.leavemanagement.dto.ApiResponse;
import com.sanyark.leavemanagement.dto.EmployeeDashboardResponse;
import com.sanyark.leavemanagement.dto.ManagerDashboardResponse;
import com.sanyark.leavemanagement.security.CustomUserDetails;
import com.sanyark.leavemanagement.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/employee")
    public ApiResponse<EmployeeDashboardResponse> getEmployeeDashboard(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        return ApiResponse.<EmployeeDashboardResponse>builder()
                .success(true)
                .message("Employee dashboard fetched successfully")
                .data(dashboardService.getEmployeeDashboard(userDetails.getId()))
                .build();
    }

    @GetMapping("/manager")
    public ApiResponse<ManagerDashboardResponse> getManagerDashboard(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        return ApiResponse.<ManagerDashboardResponse>builder()
                .success(true)
                .message("Manager dashboard fetched successfully")
                .data(dashboardService.getManagerDashboard(userDetails.getId()))
                .build();
    }
}