package com.sanyark.leavemanagement.repository;

import com.sanyark.leavemanagement.entity.LeaveStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeaveStatusHistoryRepository extends JpaRepository<LeaveStatusHistory, Long> {
}