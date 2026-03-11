package com.sanyark.leavemanagement.repository;

import com.sanyark.leavemanagement.entity.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface HolidayRepository extends JpaRepository<Holiday, Long> {

    Optional<Holiday> findByDate(LocalDate date);

    boolean existsByDate(LocalDate date);

    List<Holiday> findAllByOrderByDateAsc();

    List<Holiday> findByDateBetweenOrderByDateAsc(LocalDate startDate, LocalDate endDate);

    List<Holiday> findByDateGreaterThanEqualOrderByDateAsc(LocalDate date);

    long countByDateBetween(LocalDate startDate, LocalDate endDate);
}