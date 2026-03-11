package com.sanyark.leavemanagement.repository;

import com.sanyark.leavemanagement.entity.LeaveBalance;
import com.sanyark.leavemanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LeaveBalanceRepository extends JpaRepository<LeaveBalance, Long> {
    Optional<LeaveBalance> findByUserAndYear(User user, Integer year);
}