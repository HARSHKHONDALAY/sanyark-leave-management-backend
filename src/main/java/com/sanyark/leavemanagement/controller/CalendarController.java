package com.sanyark.leavemanagement.controller;

import com.sanyark.leavemanagement.dto.ApiResponse;
import com.sanyark.leavemanagement.dto.LeaveCalendarResponse;
import com.sanyark.leavemanagement.security.CustomUserDetails;
import com.sanyark.leavemanagement.service.CalendarService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/calendar")
@RequiredArgsConstructor
public class CalendarController {

    private final CalendarService calendarService;

    @GetMapping("/company")
    public ApiResponse<LeaveCalendarResponse> getCompanyCalendar(
            @RequestParam Integer month,
            @RequestParam Integer year
    ) {
        return ApiResponse.<LeaveCalendarResponse>builder()
                .success(true)
                .message("Company calendar fetched successfully")
                .data(calendarService.getCompanyCalendar(month, year))
                .build();
    }

    @GetMapping("/my")
    public ApiResponse<LeaveCalendarResponse> getMyCalendar(
            @RequestParam Integer month,
            @RequestParam Integer year,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ApiResponse.<LeaveCalendarResponse>builder()
                .success(true)
                .message("My leave calendar fetched successfully")
                .data(calendarService.getMyCalendar(month, year, userDetails.getId()))
                .build();
    }

    @GetMapping("/team")
    public ApiResponse<LeaveCalendarResponse> getTeamCalendar(
            @RequestParam Integer month,
            @RequestParam Integer year
    ) {
        return ApiResponse.<LeaveCalendarResponse>builder()
                .success(true)
                .message("Team leave calendar fetched successfully")
                .data(calendarService.getTeamCalendar(month, year))
                .build();
    }
}