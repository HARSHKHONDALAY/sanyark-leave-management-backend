package com.sanyark.leavemanagement.controller;

import com.sanyark.leavemanagement.dto.ApiResponse;
import com.sanyark.leavemanagement.dto.LeaveCreateRequest;
import com.sanyark.leavemanagement.dto.LeaveResponse;
import com.sanyark.leavemanagement.security.CustomUserDetails;
import com.sanyark.leavemanagement.service.LeaveService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leaves")
@RequiredArgsConstructor
public class LeaveController {

    private final LeaveService leaveService;

    @PostMapping
    public ApiResponse<LeaveResponse> createLeave(@Valid @RequestBody LeaveCreateRequest request,
                                                  @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ApiResponse.<LeaveResponse>builder()
                .success(true)
                .message("Leave request created successfully")
                .data(leaveService.createLeave(request, userDetails.getId()))
                .build();
    }

    @GetMapping("/my")
    public ApiResponse<List<LeaveResponse>> getMyLeaves(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ApiResponse.<List<LeaveResponse>>builder()
                .success(true)
                .message("Leave history fetched successfully")
                .data(leaveService.getMyLeaves(userDetails.getId()))
                .build();
    }

    @PutMapping("/{id}/cancel")
    public ApiResponse<LeaveResponse> cancelLeave(@PathVariable Long id,
                                                  @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ApiResponse.<LeaveResponse>builder()
                .success(true)
                .message("Leave request cancelled successfully")
                .data(leaveService.cancelLeave(id, userDetails.getId()))
                .build();
    }
}