package com.sanyark.leavemanagement.dto;

import com.sanyark.leavemanagement.enums.HolidayType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class HolidayRequest {

    @NotBlank(message = "Holiday name is required")
    private String name;

    @NotNull(message = "Holiday date is required")
    private LocalDate date;

    @NotNull(message = "Holiday type is required")
    private HolidayType type;

    private String description;
}