package com.sanyark.leavemanagement.service;

import com.sanyark.leavemanagement.dto.LeaveActionRequest;
import com.sanyark.leavemanagement.dto.LeaveCreateRequest;
import com.sanyark.leavemanagement.dto.LeaveResponse;
import com.sanyark.leavemanagement.entity.LeaveBalance;
import com.sanyark.leavemanagement.entity.LeaveRequest;
import com.sanyark.leavemanagement.entity.LeaveStatusHistory;
import com.sanyark.leavemanagement.entity.User;
import com.sanyark.leavemanagement.enums.Gender;
import com.sanyark.leavemanagement.enums.LeaveStatus;
import com.sanyark.leavemanagement.enums.LeaveType;
import com.sanyark.leavemanagement.enums.NotificationType;
import com.sanyark.leavemanagement.enums.Role;
import com.sanyark.leavemanagement.exception.InvalidLeaveActionException;
import com.sanyark.leavemanagement.exception.ResourceNotFoundException;
import com.sanyark.leavemanagement.exception.UnauthorizedActionException;
import com.sanyark.leavemanagement.repository.LeaveBalanceRepository;
import com.sanyark.leavemanagement.repository.LeaveRequestRepository;
import com.sanyark.leavemanagement.repository.LeaveStatusHistoryRepository;
import com.sanyark.leavemanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaveServiceImpl implements LeaveService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final LeaveStatusHistoryRepository leaveStatusHistoryRepository;
    private final UserRepository userRepository;
    private final LeaveBalanceRepository leaveBalanceRepository;
    private final NotificationService notificationService;
    private final LeaveCalculationService leaveCalculationService;

    @Override
    public LeaveResponse createLeave(LeaveCreateRequest request, Long userId) {
        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new InvalidLeaveActionException("Start date cannot be after end date");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        boolean overlappingLeaveExists = leaveRequestRepository
                .existsByUserAndStatusInAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        user,
                        List.of(LeaveStatus.PENDING, LeaveStatus.APPROVED),
                        request.getEndDate(),
                        request.getStartDate()
                );

        if (overlappingLeaveExists) {
            throw new InvalidLeaveActionException("You already have an overlapping leave request");
        }

        long requestedDays = leaveCalculationService.calculateWorkingLeaveDays(
                request.getStartDate(),
                request.getEndDate()
        );

        if (requestedDays <= 0) {
            throw new InvalidLeaveActionException("Selected date range does not contain any working days");
        }

        LeaveBalance leaveBalance = getOrCreateLeaveBalance(user, Year.now().getValue());

        validateLeaveTypeEligibility(user, request.getLeaveType());
        validateLeaveBalance(request.getLeaveType(), requestedDays, leaveBalance);

        incrementPendingBalance(request.getLeaveType(), requestedDays, leaveBalance);
        leaveBalanceRepository.save(leaveBalance);

        LeaveRequest leaveRequest = LeaveRequest.builder()
                .user(user)
                .leaveType(request.getLeaveType())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .reason(request.getReason())
                .status(LeaveStatus.PENDING)
                .managerComment(null)
                .build();

        LeaveRequest saved = leaveRequestRepository.save(leaveRequest);

        LeaveStatusHistory history = LeaveStatusHistory.builder()
                .leaveRequest(saved)
                .oldStatus(null)
                .newStatus(LeaveStatus.PENDING)
                .changedBy(user)
                .comment("Leave request created")
                .build();

        leaveStatusHistoryRepository.save(history);

        notificationService.notifyAllManagers(
                NotificationType.LEAVE_APPLIED,
                "New leave request",
                user.getFullName() + " (" + user.getEmployeeCode() + ") applied for "
                        + request.getLeaveType().name() + " leave from "
                        + request.getStartDate() + " to " + request.getEndDate()
        );

        return mapToResponse(saved);
    }

    @Override
    public List<LeaveResponse> getMyLeaves(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return leaveRequestRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public LeaveResponse cancelLeave(Long leaveId, Long userId) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(leaveId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found"));

        if (!leaveRequest.getUser().getId().equals(userId)) {
            throw new UnauthorizedActionException("You can only cancel your own leave request");
        }

        if (leaveRequest.getStatus() != LeaveStatus.PENDING) {
            throw new InvalidLeaveActionException("Only pending leave requests can be cancelled");
        }

        long days = leaveCalculationService.calculateWorkingLeaveDays(
                leaveRequest.getStartDate(),
                leaveRequest.getEndDate()
        );

        LeaveBalance leaveBalance = getOrCreateLeaveBalance(
                leaveRequest.getUser(),
                leaveRequest.getStartDate().getYear()
        );

        decrementPendingBalance(leaveRequest.getLeaveType(), days, leaveBalance);
        leaveBalanceRepository.save(leaveBalance);

        LeaveStatus oldStatus = leaveRequest.getStatus();
        leaveRequest.setStatus(LeaveStatus.CANCELLED);

        LeaveRequest updated = leaveRequestRepository.save(leaveRequest);

        LeaveStatusHistory history = LeaveStatusHistory.builder()
                .leaveRequest(updated)
                .oldStatus(oldStatus)
                .newStatus(LeaveStatus.CANCELLED)
                .changedBy(leaveRequest.getUser())
                .comment("Leave request cancelled by employee")
                .build();

        leaveStatusHistoryRepository.save(history);

        notificationService.notifyAllManagers(
                NotificationType.LEAVE_CANCELLED,
                "Leave request cancelled",
                leaveRequest.getUser().getFullName() + " (" + leaveRequest.getUser().getEmployeeCode()
                        + ") cancelled leave request from "
                        + leaveRequest.getStartDate() + " to " + leaveRequest.getEndDate()
        );

        return mapToResponse(updated);
    }

    @Override
    public List<LeaveResponse> getAllLeaves() {
        return leaveRequestRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public LeaveResponse approveLeave(Long leaveId, LeaveActionRequest request, Long managerId) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(leaveId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found"));

        User manager = userRepository.findById(managerId)
                .orElseThrow(() -> new ResourceNotFoundException("Manager not found"));

        if (manager.getRole() != Role.MANAGER) {
            throw new UnauthorizedActionException("Only managers can perform this action");
        }

        if (leaveRequest.getStatus() != LeaveStatus.PENDING) {
            throw new InvalidLeaveActionException("Only pending leave requests can be approved");
        }

        long days = leaveCalculationService.calculateWorkingLeaveDays(
                leaveRequest.getStartDate(),
                leaveRequest.getEndDate()
        );

        LeaveBalance leaveBalance = getOrCreateLeaveBalance(
                leaveRequest.getUser(),
                leaveRequest.getStartDate().getYear()
        );

        movePendingToUsedBalance(leaveRequest.getLeaveType(), days, leaveBalance);
        leaveBalanceRepository.save(leaveBalance);

        LeaveStatus oldStatus = leaveRequest.getStatus();
        leaveRequest.setStatus(LeaveStatus.APPROVED);
        leaveRequest.setManagerComment(request.getComment());

        LeaveRequest updated = leaveRequestRepository.save(leaveRequest);

        LeaveStatusHistory history = LeaveStatusHistory.builder()
                .leaveRequest(updated)
                .oldStatus(oldStatus)
                .newStatus(LeaveStatus.APPROVED)
                .changedBy(manager)
                .comment(request.getComment())
                .build();

        leaveStatusHistoryRepository.save(history);

        notificationService.createNotification(
                leaveRequest.getUser(),
                NotificationType.LEAVE_APPROVED,
                "Leave approved",
                "Your leave request from " + leaveRequest.getStartDate() + " to "
                        + leaveRequest.getEndDate() + " has been approved"
                        + (request.getComment() != null && !request.getComment().isBlank()
                        ? ". Comment: " + request.getComment() : "")
        );

        return mapToResponse(updated);
    }

    @Override
    public LeaveResponse rejectLeave(Long leaveId, LeaveActionRequest request, Long managerId) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(leaveId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found"));

        User manager = userRepository.findById(managerId)
                .orElseThrow(() -> new ResourceNotFoundException("Manager not found"));

        if (manager.getRole() != Role.MANAGER) {
            throw new UnauthorizedActionException("Only managers can perform this action");
        }

        if (leaveRequest.getStatus() != LeaveStatus.PENDING) {
            throw new InvalidLeaveActionException("Only pending leave requests can be rejected");
        }

        long days = leaveCalculationService.calculateWorkingLeaveDays(
                leaveRequest.getStartDate(),
                leaveRequest.getEndDate()
        );

        LeaveBalance leaveBalance = getOrCreateLeaveBalance(
                leaveRequest.getUser(),
                leaveRequest.getStartDate().getYear()
        );

        decrementPendingBalance(leaveRequest.getLeaveType(), days, leaveBalance);
        leaveBalanceRepository.save(leaveBalance);

        LeaveStatus oldStatus = leaveRequest.getStatus();
        leaveRequest.setStatus(LeaveStatus.REJECTED);
        leaveRequest.setManagerComment(request.getComment());

        LeaveRequest updated = leaveRequestRepository.save(leaveRequest);

        LeaveStatusHistory history = LeaveStatusHistory.builder()
                .leaveRequest(updated)
                .oldStatus(oldStatus)
                .newStatus(LeaveStatus.REJECTED)
                .changedBy(manager)
                .comment(request.getComment())
                .build();

        leaveStatusHistoryRepository.save(history);

        notificationService.createNotification(
                leaveRequest.getUser(),
                NotificationType.LEAVE_REJECTED,
                "Leave rejected",
                "Your leave request from " + leaveRequest.getStartDate() + " to "
                        + leaveRequest.getEndDate() + " has been rejected"
                        + (request.getComment() != null && !request.getComment().isBlank()
                        ? ". Comment: " + request.getComment() : "")
        );

        return mapToResponse(updated);
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

    private void validateLeaveTypeEligibility(User user, LeaveType leaveType) {
        if (leaveType == LeaveType.MATERNITY && user.getGender() != Gender.FEMALE) {
            throw new InvalidLeaveActionException("Maternity leave is only available for female employees");
        }
    }

    private void validateLeaveBalance(LeaveType leaveType, long days, LeaveBalance leaveBalance) {
        if (leaveType == LeaveType.MATERNITY) {
            if (leaveBalance.getMaternityRemaining() < days) {
                throw new InvalidLeaveActionException("Insufficient maternity leave balance");
            }
            return;
        }

        if (leaveBalance.getRemainingLeaves() < days) {
            throw new InvalidLeaveActionException("Insufficient leave balance");
        }
    }

    private void incrementPendingBalance(LeaveType leaveType, long days, LeaveBalance leaveBalance) {
        int requestedDays = Math.toIntExact(days);

        if (leaveType == LeaveType.MATERNITY) {
            leaveBalance.setMaternityPending(leaveBalance.getMaternityPending() + requestedDays);
            leaveBalance.setMaternityRemaining(leaveBalance.getMaternityRemaining() - requestedDays);
            return;
        }

        leaveBalance.setPendingLeaves(leaveBalance.getPendingLeaves() + requestedDays);
        leaveBalance.setRemainingLeaves(leaveBalance.getRemainingLeaves() - requestedDays);
    }

    private void decrementPendingBalance(LeaveType leaveType, long days, LeaveBalance leaveBalance) {
        int requestedDays = Math.toIntExact(days);

        if (leaveType == LeaveType.MATERNITY) {
            leaveBalance.setMaternityPending(Math.max(0, leaveBalance.getMaternityPending() - requestedDays));
            leaveBalance.setMaternityRemaining(leaveBalance.getMaternityRemaining() + requestedDays);
            return;
        }

        leaveBalance.setPendingLeaves(Math.max(0, leaveBalance.getPendingLeaves() - requestedDays));
        leaveBalance.setRemainingLeaves(leaveBalance.getRemainingLeaves() + requestedDays);
    }

    private void movePendingToUsedBalance(LeaveType leaveType, long days, LeaveBalance leaveBalance) {
        int requestedDays = Math.toIntExact(days);

        if (leaveType == LeaveType.MATERNITY) {
            leaveBalance.setMaternityPending(Math.max(0, leaveBalance.getMaternityPending() - requestedDays));
            leaveBalance.setMaternityUsed(leaveBalance.getMaternityUsed() + requestedDays);
            return;
        }

        leaveBalance.setPendingLeaves(Math.max(0, leaveBalance.getPendingLeaves() - requestedDays));
        leaveBalance.setUsedLeaves(leaveBalance.getUsedLeaves() + requestedDays);
    }

    private LeaveResponse mapToResponse(LeaveRequest leaveRequest) {
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