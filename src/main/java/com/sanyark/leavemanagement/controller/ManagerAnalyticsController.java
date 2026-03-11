package com.sanyark.leavemanagement.controller;

import com.sanyark.leavemanagement.dto.AnalyticsOverviewResponse;
import com.sanyark.leavemanagement.dto.ApiResponse;
import com.sanyark.leavemanagement.dto.EmployeeOnLeaveResponse;
import com.sanyark.leavemanagement.dto.MonthlyLeaveTrendResponse;
import com.sanyark.leavemanagement.dto.TopLeaveTakerResponse;
import com.sanyark.leavemanagement.security.CustomUserDetails;
import com.sanyark.leavemanagement.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Year;
import java.util.List;

@RestController
@RequestMapping("/api/manager/analytics")
@RequiredArgsConstructor
public class ManagerAnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/overview")
    public ApiResponse<AnalyticsOverviewResponse> getOverview(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        return ApiResponse.<AnalyticsOverviewResponse>builder()
                .success(true)
                .message("Analytics overview fetched successfully")
                .data(analyticsService.getOverview(userDetails.getId()))
                .build();
    }

    @GetMapping("/monthly-trends")
    public ApiResponse<List<MonthlyLeaveTrendResponse>> getMonthlyTrends(
            @RequestParam(required = false) Integer year,
            Authentication authentication
    ) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        int selectedYear = year != null ? year : Year.now().getValue();

        return ApiResponse.<List<MonthlyLeaveTrendResponse>>builder()
                .success(true)
                .message("Monthly leave trends fetched successfully")
                .data(analyticsService.getMonthlyTrends(userDetails.getId(), selectedYear))
                .build();
    }

    @GetMapping("/top-leave-takers")
    public ApiResponse<List<TopLeaveTakerResponse>> getTopLeaveTakers(
            @RequestParam(required = false) Integer year,
            Authentication authentication
    ) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        int selectedYear = year != null ? year : Year.now().getValue();

        return ApiResponse.<List<TopLeaveTakerResponse>>builder()
                .success(true)
                .message("Top leave takers fetched successfully")
                .data(analyticsService.getTopLeaveTakers(userDetails.getId(), selectedYear))
                .build();
    }

    @GetMapping("/currently-on-leave")
    public ApiResponse<List<EmployeeOnLeaveResponse>> getCurrentlyOnLeave(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        return ApiResponse.<List<EmployeeOnLeaveResponse>>builder()
                .success(true)
                .message("Employees currently on leave fetched successfully")
                .data(analyticsService.getCurrentlyOnLeave(userDetails.getId()))
                .build();
    }
}