package com.sanyark.leavemanagement.repository;

import com.sanyark.leavemanagement.entity.LeaveRequest;
import com.sanyark.leavemanagement.entity.User;
import com.sanyark.leavemanagement.enums.LeaveStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

    List<LeaveRequest> findByUserOrderByCreatedAtDesc(User user);

    List<LeaveRequest> findAllByOrderByCreatedAtDesc();

    boolean existsByUserAndStatusInAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            User user,
            List<LeaveStatus> statuses,
            LocalDate endDate,
            LocalDate startDate
    );

    List<LeaveRequest> findByUserAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByStartDateAsc(
            User user,
            LocalDate endDate,
            LocalDate startDate
    );

    List<LeaveRequest> findByStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByStartDateAsc(
            LocalDate endDate,
            LocalDate startDate
    );

    List<LeaveRequest> findByUserAndStatusAndStartDateGreaterThanEqualOrderByStartDateAsc(
            User user,
            LeaveStatus status,
            LocalDate startDate
    );

    long countByStatus(LeaveStatus status);

    long countByStatusAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            LeaveStatus status,
            LocalDate endDate,
            LocalDate startDate
    );

    @Query("""
            SELECT COUNT(DISTINCT lr.user.id)
            FROM LeaveRequest lr
            WHERE lr.status = com.sanyark.leavemanagement.enums.LeaveStatus.APPROVED
              AND lr.startDate <= :date
              AND lr.endDate >= :date
            """)
    long countDistinctUsersOnApprovedLeaveBetween(@Param("date") LocalDate date);

    @Query("""
            SELECT COUNT(lr)
            FROM LeaveRequest lr
            WHERE lr.status = :status
              AND YEAR(lr.startDate) = :year
            """)
    long countByStatusAndYear(@Param("status") LeaveStatus status, @Param("year") int year);

    @Query("""
            SELECT lr
            FROM LeaveRequest lr
            WHERE lr.status = com.sanyark.leavemanagement.enums.LeaveStatus.APPROVED
              AND YEAR(lr.startDate) = :year
            ORDER BY lr.startDate ASC
            """)
    List<LeaveRequest> findApprovedLeavesByYear(@Param("year") int year);

    @Query("""
            SELECT lr
            FROM LeaveRequest lr
            WHERE lr.status = com.sanyark.leavemanagement.enums.LeaveStatus.APPROVED
              AND lr.startDate <= :date
              AND lr.endDate >= :date
            ORDER BY lr.startDate ASC
            """)
    List<LeaveRequest> findApprovedLeavesActiveOnDate(@Param("date") LocalDate date);
}