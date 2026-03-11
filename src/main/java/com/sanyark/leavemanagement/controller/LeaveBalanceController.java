package com.sanyark.leavemanagement.controller;

import com.sanyark.leavemanagement.dto.ApiResponse;
import com.sanyark.leavemanagement.dto.LeaveBalanceResponse;
import com.sanyark.leavemanagement.security.CustomUserDetails;
import com.sanyark.leavemanagement.service.LeaveBalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/leave-balances")
@RequiredArgsConstructor
public class LeaveBalanceController {

    private final LeaveBalanceService leaveBalanceService;

    @GetMapping("/my")
    public ApiResponse<LeaveBalanceResponse> getMyLeaveBalance(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ApiResponse.<LeaveBalanceResponse>builder()
                .success(true)
                .message("Leave balance fetched successfully")
                .data(leaveBalanceService.getMyLeaveBalance(userDetails.getId()))
                .build();
    }

    @GetMapping("/employee/{userId}")
    public ApiResponse<LeaveBalanceResponse> getEmployeeLeaveBalance(@PathVariable Long userId) {
        return ApiResponse.<LeaveBalanceResponse>builder()
                .success(true)
                .message("Employee leave balance fetched successfully")
                .data(leaveBalanceService.getEmployeeLeaveBalance(userId))
                .build();
    }
}