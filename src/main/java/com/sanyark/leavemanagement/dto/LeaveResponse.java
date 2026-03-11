package com.sanyark.leavemanagement.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class LeaveResponse {
    private Long id;
    private String employeeName;
    private String employeeCode;
    private String leaveType;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long days;
    private String reason;
    private String status;
    private String managerComment;
}