package com.sanyark.leavemanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeOnLeaveResponse {
    private Long leaveId;
    private Long userId;
    private String employeeName;
    private String employeeCode;
    private String leaveType;
    private LocalDate startDate;
    private LocalDate endDate;
    private long days;
    private String reason;
}