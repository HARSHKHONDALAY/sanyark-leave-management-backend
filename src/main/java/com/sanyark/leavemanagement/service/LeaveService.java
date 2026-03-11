package com.sanyark.leavemanagement.service;

import com.sanyark.leavemanagement.dto.LeaveActionRequest;
import com.sanyark.leavemanagement.dto.LeaveCreateRequest;
import com.sanyark.leavemanagement.dto.LeaveResponse;

import java.util.List;

public interface LeaveService {
    LeaveResponse createLeave(LeaveCreateRequest request, Long userId);
    List<LeaveResponse> getMyLeaves(Long userId);
    LeaveResponse cancelLeave(Long leaveId, Long userId);
    List<LeaveResponse> getAllLeaves();
    LeaveResponse approveLeave(Long leaveId, LeaveActionRequest request, Long managerId);
    LeaveResponse rejectLeave(Long leaveId, LeaveActionRequest request, Long managerId);
}