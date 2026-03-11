package com.sanyark.leavemanagement.service;

import java.time.LocalDate;

public interface LeaveCalculationService {

    long calculateWorkingLeaveDays(LocalDate startDate, LocalDate endDate);

    boolean isWorkingDay(LocalDate date);

}
