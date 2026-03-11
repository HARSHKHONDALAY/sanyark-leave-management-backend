package com.sanyark.leavemanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopLeaveTakerResponse {
    private Long userId;
    private String employeeName;
    private String employeeCode;
    private long totalApprovedLeaves;
}