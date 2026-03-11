package com.sanyark.leavemanagement.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class CalendarEntryResponse {
    private LocalDate date;
    private String title;
    private String type;
    private String status;
    private String employeeName;
    private String employeeCode;
    private String description;
    private String dayOfWeek;
}