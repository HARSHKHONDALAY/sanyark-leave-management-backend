package com.sanyark.leavemanagement.controller;

import com.sanyark.leavemanagement.dto.ApiResponse;
import com.sanyark.leavemanagement.dto.HolidayRequest;
import com.sanyark.leavemanagement.dto.HolidayResponse;
import com.sanyark.leavemanagement.service.HolidayService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/manager/holidays")
@RequiredArgsConstructor
public class ManagerHolidayController {

    private final HolidayService holidayService;

    @PostMapping
    public ApiResponse<HolidayResponse> createHoliday(@Valid @RequestBody HolidayRequest request) {
        return ApiResponse.<HolidayResponse>builder()
                .success(true)
                .message("Holiday created successfully")
                .data(holidayService.createHoliday(request))
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<HolidayResponse> updateHoliday(
            @PathVariable Long id,
            @Valid @RequestBody HolidayRequest request
    ) {
        return ApiResponse.<HolidayResponse>builder()
                .success(true)
                .message("Holiday updated successfully")
                .data(holidayService.updateHoliday(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteHoliday(@PathVariable Long id) {
        holidayService.deleteHoliday(id);

        return ApiResponse.<Void>builder()
                .success(true)
                .message("Holiday deleted successfully")
                .data(null)
                .build();
    }
}