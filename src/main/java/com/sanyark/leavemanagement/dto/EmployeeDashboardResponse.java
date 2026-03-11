package com.sanyark.leavemanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDashboardResponse {
    private Integer totalLeaves;
    private Integer usedLeaves;
    private Integer remainingLeaves;
    private Integer pendingLeaves;
    private List<HolidayResponse> upcomingHolidays;
    private List<LeaveResponse> upcomingApprovedLeaves;
}