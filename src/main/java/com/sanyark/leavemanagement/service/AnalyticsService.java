package com.sanyark.leavemanagement.service;

import com.sanyark.leavemanagement.dto.AnalyticsOverviewResponse;
import com.sanyark.leavemanagement.dto.EmployeeOnLeaveResponse;
import com.sanyark.leavemanagement.dto.MonthlyLeaveTrendResponse;
import com.sanyark.leavemanagement.dto.TopLeaveTakerResponse;

import java.util.List;

public interface AnalyticsService {
    AnalyticsOverviewResponse getOverview(Long managerId);
    List<MonthlyLeaveTrendResponse> getMonthlyTrends(Long managerId, int year);
    List<TopLeaveTakerResponse> getTopLeaveTakers(Long managerId, int year);
    List<EmployeeOnLeaveResponse> getCurrentlyOnLeave(Long managerId);
}