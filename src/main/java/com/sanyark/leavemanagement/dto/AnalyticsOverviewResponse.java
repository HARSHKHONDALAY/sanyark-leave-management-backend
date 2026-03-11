package com.sanyark.leavemanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsOverviewResponse {
    private long totalEmployees;
    private long pendingApprovals;
    private long employeesCurrentlyOnLeave;
    private long leavesThisWeek;
    private long leavesThisMonth;
    private long approvedLeavesThisYear;
    private long rejectedLeavesThisYear;
}