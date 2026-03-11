package com.sanyark.leavemanagement.controller;

import com.sanyark.leavemanagement.dto.ApiResponse;
import com.sanyark.leavemanagement.dto.NotificationResponse;
import com.sanyark.leavemanagement.security.CustomUserDetails;
import com.sanyark.leavemanagement.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ApiResponse<List<NotificationResponse>> getMyNotifications(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        return ApiResponse.<List<NotificationResponse>>builder()
                .success(true)
                .message("Notifications fetched successfully")
                .data(notificationService.getMyNotifications(userDetails.getId()))
                .build();
    }

    @GetMapping("/unread-count")
    public ApiResponse<Long> getUnreadCount(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        return ApiResponse.<Long>builder()
                .success(true)
                .message("Unread notification count fetched successfully")
                .data(notificationService.getUnreadCount(userDetails.getId()))
                .build();
    }

    @PutMapping("/{id}/read")
    public ApiResponse<NotificationResponse> markAsRead(@PathVariable Long id, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        return ApiResponse.<NotificationResponse>builder()
                .success(true)
                .message("Notification marked as read successfully")
                .data(notificationService.markAsRead(id, userDetails.getId()))
                .build();
    }

    @PutMapping("/read-all")
    public ApiResponse<String> markAllAsRead(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        notificationService.markAllAsRead(userDetails.getId());

        return ApiResponse.<String>builder()
                .success(true)
                .message("All notifications marked as read successfully")
                .data("OK")
                .build();
    }
}