package com.sanyark.leavemanagement.service;

import com.sanyark.leavemanagement.dto.LeaveCalendarResponse;

public interface CalendarService {
    LeaveCalendarResponse getCompanyCalendar(Integer month, Integer year);
    LeaveCalendarResponse getMyCalendar(Integer month, Integer year, Long userId);
    LeaveCalendarResponse getTeamCalendar(Integer month, Integer year);
}