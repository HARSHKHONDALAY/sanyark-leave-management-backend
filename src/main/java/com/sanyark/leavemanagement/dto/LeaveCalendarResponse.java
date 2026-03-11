package com.sanyark.leavemanagement.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class LeaveCalendarResponse {
    private Integer month;
    private Integer year;
    private List<CalendarEntryResponse> entries;
}