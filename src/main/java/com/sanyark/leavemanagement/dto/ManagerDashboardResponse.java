package com.sanyark.leavemanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ManagerDashboardResponse {
    private Long totalEmployees;
    private Long pendingApprovals;
    private Long employeesCurrentlyOnLeave;
    private Long leavesThisWeek;
    private Long leavesThisMonth;
}