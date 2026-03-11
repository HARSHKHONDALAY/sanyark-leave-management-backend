package com.sanyark.leavemanagement.controller;

import com.sanyark.leavemanagement.dto.ApiResponse;
import com.sanyark.leavemanagement.dto.HolidayResponse;
import com.sanyark.leavemanagement.service.HolidayService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/holidays")
@RequiredArgsConstructor
public class HolidayController {

    private final HolidayService holidayService;

    @GetMapping
    public ApiResponse<List<HolidayResponse>> getHolidays(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year
    ) {
        return ApiResponse.<List<HolidayResponse>>builder()
                .success(true)
                .message("Holidays fetched successfully")
                .data(holidayService.getHolidays(month, year))
                .build();
    }

    @GetMapping("/upcoming")
    public ApiResponse<List<HolidayResponse>> getUpcomingHolidays() {
        return ApiResponse.<List<HolidayResponse>>builder()
                .success(true)
                .message("Upcoming holidays fetched successfully")
                .data(holidayService.getUpcomingHolidays())
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<HolidayResponse> getHolidayById(@PathVariable Long id) {
        return ApiResponse.<HolidayResponse>builder()
                .success(true)
                .message("Holiday fetched successfully")
                .data(holidayService.getHolidayById(id))
                .build();
    }
}