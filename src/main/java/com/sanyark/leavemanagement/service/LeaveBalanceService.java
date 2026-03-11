package com.sanyark.leavemanagement.service;

import com.sanyark.leavemanagement.dto.LeaveBalanceResponse;

public interface LeaveBalanceService {
    LeaveBalanceResponse getMyLeaveBalance(Long userId);
    LeaveBalanceResponse getEmployeeLeaveBalance(Long userId);
}