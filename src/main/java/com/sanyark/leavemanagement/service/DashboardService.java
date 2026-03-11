package com.sanyark.leavemanagement.service;

import com.sanyark.leavemanagement.dto.EmployeeDashboardResponse;
import com.sanyark.leavemanagement.dto.ManagerDashboardResponse;

public interface DashboardService {
    EmployeeDashboardResponse getEmployeeDashboard(Long userId);
    ManagerDashboardResponse getManagerDashboard(Long userId);
}