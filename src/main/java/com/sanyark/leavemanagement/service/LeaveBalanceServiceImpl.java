package com.sanyark.leavemanagement.service;

import com.sanyark.leavemanagement.dto.LeaveBalanceResponse;
import com.sanyark.leavemanagement.entity.LeaveBalance;
import com.sanyark.leavemanagement.entity.User;
import com.sanyark.leavemanagement.exception.ResourceNotFoundException;
import com.sanyark.leavemanagement.repository.LeaveBalanceRepository;
import com.sanyark.leavemanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Year;

@Service
@RequiredArgsConstructor
public class LeaveBalanceServiceImpl implements LeaveBalanceService {

    private final LeaveBalanceRepository leaveBalanceRepository;
    private final UserRepository userRepository;

    @Override
    public LeaveBalanceResponse getMyLeaveBalance(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        LeaveBalance leaveBalance = getOrCreateLeaveBalance(user, Year.now().getValue());
        return mapToResponse(leaveBalance);
    }

    @Override
    public LeaveBalanceResponse getEmployeeLeaveBalance(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        LeaveBalance leaveBalance = getOrCreateLeaveBalance(user, Year.now().getValue());
        return mapToResponse(leaveBalance);
    }

    public LeaveBalance getOrCreateLeaveBalance(User user, Integer year) {
        return leaveBalanceRepository.findByUserAndYear(user, year)
                .orElseGet(() -> leaveBalanceRepository.save(
                        LeaveBalance.builder()
                                .user(user)
                                .year(year)
                                .totalLeaves(24)
                                .usedLeaves(0)
                                .pendingLeaves(0)
                                .remainingLeaves(24)
                                .maternityTotal(180)
                                .maternityUsed(0)
                                .maternityPending(0)
                                .maternityRemaining(180)
                                .build()
                ));
    }

    private LeaveBalanceResponse mapToResponse(LeaveBalance leaveBalance) {
        return LeaveBalanceResponse.builder()
                .employeeName(leaveBalance.getUser().getFullName())
                .employeeCode(leaveBalance.getUser().getEmployeeCode())
                .year(leaveBalance.getYear())
                .totalLeaves(leaveBalance.getTotalLeaves())
                .usedLeaves(leaveBalance.getUsedLeaves())
                .pendingLeaves(leaveBalance.getPendingLeaves())
                .remainingLeaves(leaveBalance.getRemainingLeaves())
                .maternityTotal(leaveBalance.getMaternityTotal())
                .maternityUsed(leaveBalance.getMaternityUsed())
                .maternityPending(leaveBalance.getMaternityPending())
                .maternityRemaining(leaveBalance.getMaternityRemaining())
                .build();
    }
}