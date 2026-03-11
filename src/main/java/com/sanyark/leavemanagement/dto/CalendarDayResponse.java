package com.sanyark.leavemanagement.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class CalendarDayResponse {
    private LocalDate date;
    private String title;
    private String type;
    private String description;
    private String dayOfWeek;
    private Integer month;
    private Integer year;
}