package com.sanyark.leavemanagement.service;

import com.sanyark.leavemanagement.dto.EmployeeDashboardResponse;
import com.sanyark.leavemanagement.dto.HolidayResponse;
import com.sanyark.leavemanagement.dto.LeaveResponse;
import com.sanyark.leavemanagement.dto.ManagerDashboardResponse;
import com.sanyark.leavemanagement.entity.Holiday;
import com.sanyark.leavemanagement.entity.LeaveBalance;
import com.sanyark.leavemanagement.entity.LeaveRequest;
import com.sanyark.leavemanagement.entity.User;
import com.sanyark.leavemanagement.enums.LeaveStatus;
import com.sanyark.leavemanagement.enums.Role;
import com.sanyark.leavemanagement.exception.ResourceNotFoundException;
import com.sanyark.leavemanagement.exception.UnauthorizedActionException;
import com.sanyark.leavemanagement.repository.HolidayRepository;
import com.sanyark.leavemanagement.repository.LeaveBalanceRepository;
import com.sanyark.leavemanagement.repository.LeaveRequestRepository;
import com.sanyark.leavemanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Year;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final UserRepository userRepository;
    private final LeaveBalanceRepository leaveBalanceRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final HolidayRepository holidayRepository;
    private final LeaveCalculationService leaveCalculationService;

    @Override
    public EmployeeDashboardResponse getEmployeeDashboard(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        LeaveBalance leaveBalance = getOrCreateLeaveBalance(user, Year.now().getValue());
        LocalDate today = LocalDate.now();

        List<HolidayResponse> upcomingHolidays = holidayRepository
                .findByDateGreaterThanEqualOrderByDateAsc(today)
                .stream()
                .limit(5)
                .map(this::mapHolidayToResponse)
                .toList();

        List<LeaveResponse> upcomingApprovedLeaves = leaveRequestRepository
                .findByUserAndStatusAndStartDateGreaterThanEqualOrderByStartDateAsc(
                        user,
                        LeaveStatus.APPROVED,
                        today
                )
                .stream()
                .limit(5)
                .map(this::mapLeaveToResponse)
                .toList();

        return EmployeeDashboardResponse.builder()
                .totalLeaves(leaveBalance.getTotalLeaves())
                .usedLeaves(leaveBalance.getUsedLeaves())
                .remainingLeaves(leaveBalance.getRemainingLeaves())
                .pendingLeaves(leaveBalance.getPendingLeaves())
                .upcomingHolidays(upcomingHolidays)
                .upcomingApprovedLeaves(upcomingApprovedLeaves)
                .build();
    }

    @Override
    public ManagerDashboardResponse getManagerDashboard(Long userId) {
        User manager = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (manager.getRole() != Role.MANAGER) {
            throw new UnauthorizedActionException("Only managers can access manager dashboard");
        }

        LocalDate today = LocalDate.now();

        LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        LocalDate startOfMonth = today.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate endOfMonth = today.with(TemporalAdjusters.lastDayOfMonth());

        long totalEmployees = userRepository.countByRole(Role.EMPLOYEE);
        long pendingApprovals = leaveRequestRepository.countByStatus(LeaveStatus.PENDING);
        long employeesCurrentlyOnLeave = leaveRequestRepository.countDistinctUsersOnApprovedLeaveBetween(today);

        long leavesThisWeek = leaveRequestRepository
                .countByStatusAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        LeaveStatus.APPROVED,
                        endOfWeek,
                        startOfWeek
                );

        long leavesThisMonth = leaveRequestRepository
                .countByStatusAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        LeaveStatus.APPROVED,
                        endOfMonth,
                        startOfMonth
                );

        return ManagerDashboardResponse.builder()
                .totalEmployees(totalEmployees)
                .pendingApprovals(pendingApprovals)
                .employeesCurrentlyOnLeave(employeesCurrentlyOnLeave)
                .leavesThisWeek(leavesThisWeek)
                .leavesThisMonth(leavesThisMonth)
                .build();
    }

    private LeaveBalance getOrCreateLeaveBalance(User user, Integer year) {
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

    private HolidayResponse mapHolidayToResponse(Holiday holiday) {
        return HolidayResponse.builder()
                .id(holiday.getId())
                .name(holiday.getName())
                .date(holiday.getDate())
                .type(holiday.getType().name())
                .description(holiday.getDescription())
                .build();
    }

    private LeaveResponse mapLeaveToResponse(LeaveRequest leaveRequest) {
        return LeaveResponse.builder()
                .id(leaveRequest.getId())
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
                .status(leaveRequest.getStatus().name())
                .managerComment(leaveRequest.getManagerComment())
                .build();
    }
}