package com.sanyark.leavemanagement.service;

import com.sanyark.leavemanagement.dto.AnalyticsOverviewResponse;
import com.sanyark.leavemanagement.dto.EmployeeOnLeaveResponse;
import com.sanyark.leavemanagement.dto.MonthlyLeaveTrendResponse;
import com.sanyark.leavemanagement.dto.TopLeaveTakerResponse;
import com.sanyark.leavemanagement.entity.LeaveRequest;
import com.sanyark.leavemanagement.entity.User;
import com.sanyark.leavemanagement.enums.LeaveStatus;
import com.sanyark.leavemanagement.enums.Role;
import com.sanyark.leavemanagement.exception.ResourceNotFoundException;
import com.sanyark.leavemanagement.exception.UnauthorizedActionException;
import com.sanyark.leavemanagement.repository.LeaveRequestRepository;
import com.sanyark.leavemanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {

    private final UserRepository userRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final LeaveCalculationService leaveCalculationService;

    @Override
    public AnalyticsOverviewResponse getOverview(Long managerId) {
        validateManager(managerId);

        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        LocalDate startOfMonth = today.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate endOfMonth = today.with(TemporalAdjusters.lastDayOfMonth());
        int currentYear = today.getYear();

        long totalEmployees = userRepository.countByRole(Role.EMPLOYEE);
        long pendingApprovals = leaveRequestRepository.countByStatus(LeaveStatus.PENDING);
        long employeesCurrentlyOnLeave = leaveRequestRepository.countDistinctUsersOnApprovedLeaveBetween(today);
        long leavesThisWeek = leaveRequestRepository.countByStatusAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                LeaveStatus.APPROVED,
                endOfWeek,
                startOfWeek
        );
        long leavesThisMonth = leaveRequestRepository.countByStatusAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                LeaveStatus.APPROVED,
                endOfMonth,
                startOfMonth
        );
        long approvedLeavesThisYear = leaveRequestRepository.countByStatusAndYear(LeaveStatus.APPROVED, currentYear);
        long rejectedLeavesThisYear = leaveRequestRepository.countByStatusAndYear(LeaveStatus.REJECTED, currentYear);

        return AnalyticsOverviewResponse.builder()
                .totalEmployees(totalEmployees)
                .pendingApprovals(pendingApprovals)
                .employeesCurrentlyOnLeave(employeesCurrentlyOnLeave)
                .leavesThisWeek(leavesThisWeek)
                .leavesThisMonth(leavesThisMonth)
                .approvedLeavesThisYear(approvedLeavesThisYear)
                .rejectedLeavesThisYear(rejectedLeavesThisYear)
                .build();
    }

    @Override
    public List<MonthlyLeaveTrendResponse> getMonthlyTrends(Long managerId, int year) {
        validateManager(managerId);

        List<LeaveRequest> approvedLeaves = leaveRequestRepository.findApprovedLeavesByYear(year);

        Map<Integer, Long> monthlyCounts = new LinkedHashMap<>();
        for (int month = 1; month <= 12; month++) {
            monthlyCounts.put(month, 0L);
        }

        for (LeaveRequest leaveRequest : approvedLeaves) {
            int month = leaveRequest.getStartDate().getMonthValue();
            monthlyCounts.put(month, monthlyCounts.get(month) + 1);
        }

        List<MonthlyLeaveTrendResponse> response = new ArrayList<>();
        for (int month = 1; month <= 12; month++) {
            response.add(MonthlyLeaveTrendResponse.builder()
                    .month(month)
                    .monthName(Month.of(month).name())
                    .totalApprovedLeaves(monthlyCounts.get(month))
                    .build());
        }

        return response;
    }

    @Override
    public List<TopLeaveTakerResponse> getTopLeaveTakers(Long managerId, int year) {
        validateManager(managerId);

        List<LeaveRequest> approvedLeaves = leaveRequestRepository.findApprovedLeavesByYear(year);

        Map<Long, TopLeaveTakerResponse> aggregated = new LinkedHashMap<>();

        for (LeaveRequest leaveRequest : approvedLeaves) {
            User user = leaveRequest.getUser();
            long leaveDays = leaveCalculationService.calculateWorkingLeaveDays(
                    leaveRequest.getStartDate(),
                    leaveRequest.getEndDate()
            );

            TopLeaveTakerResponse existing = aggregated.get(user.getId());

            if (existing == null) {
                aggregated.put(user.getId(), TopLeaveTakerResponse.builder()
                        .userId(user.getId())
                        .employeeName(user.getFullName())
                        .employeeCode(user.getEmployeeCode())
                        .totalApprovedLeaves(leaveDays)
                        .build());
            } else {
                existing.setTotalApprovedLeaves(existing.getTotalApprovedLeaves() + leaveDays);
            }
        }

        return aggregated.values()
                .stream()
                .sorted(Comparator.comparingLong(TopLeaveTakerResponse::getTotalApprovedLeaves).reversed())
                .limit(10)
                .toList();
    }

    @Override
    public List<EmployeeOnLeaveResponse> getCurrentlyOnLeave(Long managerId) {
        validateManager(managerId);

        LocalDate today = LocalDate.now();

        return leaveRequestRepository.findApprovedLeavesActiveOnDate(today)
                .stream()
                .map(leaveRequest -> EmployeeOnLeaveResponse.builder()
                        .leaveId(leaveRequest.getId())
                        .userId(leaveRequest.getUser().getId())
                        .employeeName(leaveRequest.getUser().getFullName())
                        .employeeCode(leaveRequest.getUser().getEmployeeCode())
                        .leaveType(leaveRequest.getLeaveType().name())
                        .startDate(leaveRequest.getStartDate())
                        .endDate(leaveRequest.getEndDate())
                        .days(leaveCalculationService.calculateWorkingLeaveDays(
                                leaveRequest.getStartDate(),
                                leaveRequest.getEndDate()
                        ))
                        .reason(leaveRequest.getReason())
                        .build())
                .toList();
    }

    private User validateManager(Long managerId) {
        User manager = userRepository.findById(managerId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (manager.getRole() != Role.MANAGER) {
            throw new UnauthorizedActionException("Only managers can access analytics");
        }

        return manager;
    }
}