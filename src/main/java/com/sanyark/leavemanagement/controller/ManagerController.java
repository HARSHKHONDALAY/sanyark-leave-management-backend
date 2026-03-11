package com.sanyark.leavemanagement.controller;

import com.sanyark.leavemanagement.dto.ApiResponse;
import com.sanyark.leavemanagement.dto.LeaveActionRequest;
import com.sanyark.leavemanagement.dto.LeaveResponse;
import com.sanyark.leavemanagement.security.CustomUserDetails;
import com.sanyark.leavemanagement.service.LeaveService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/manager/leaves")
@RequiredArgsConstructor
public class ManagerController {

    private final LeaveService leaveService;

    @GetMapping
    public ApiResponse<List<LeaveResponse>> getAllLeaves() {
        return ApiResponse.<List<LeaveResponse>>builder()
                .success(true)
                .message("All leave requests fetched successfully")
                .data(leaveService.getAllLeaves())
                .build();
    }

    @PutMapping("/{id}/approve")
    public ApiResponse<LeaveResponse> approveLeave(@PathVariable Long id,
                                                   @Valid @RequestBody LeaveActionRequest request,
                                                   @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ApiResponse.<LeaveResponse>builder()
                .success(true)
                .message("Leave request approved successfully")
                .data(leaveService.approveLeave(id, request, userDetails.getId()))
                .build();
    }

    @PutMapping("/{id}/reject")
    public ApiResponse<LeaveResponse> rejectLeave(@PathVariable Long id,
                                                  @Valid @RequestBody LeaveActionRequest request,
                                                  @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ApiResponse.<LeaveResponse>builder()
                .success(true)
                .message("Leave request rejected successfully")
                .data(leaveService.rejectLeave(id, request, userDetails.getId()))
                .build();
    }
}