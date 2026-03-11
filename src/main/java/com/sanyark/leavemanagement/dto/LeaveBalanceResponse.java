package com.sanyark.leavemanagement.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LeaveBalanceResponse {
    private String employeeName;
    private String employeeCode;
    private Integer year;

    private Integer totalLeaves;
    private Integer usedLeaves;
    private Integer pendingLeaves;
    private Integer remainingLeaves;

    private Integer maternityTotal;
    private Integer maternityUsed;
    private Integer maternityPending;
    private Integer maternityRemaining;
}