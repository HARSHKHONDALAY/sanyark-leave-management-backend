package com.sanyark.leavemanagement.service;

import com.sanyark.leavemanagement.repository.HolidayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class LeaveCalculationServiceImpl implements LeaveCalculationService {

    private final HolidayRepository holidayRepository;

    @Override
    public long calculateWorkingLeaveDays(LocalDate startDate, LocalDate endDate) {

        long workingDays = 0;
        LocalDate current = startDate;

        while (!current.isAfter(endDate)) {

            if (isWorkingDay(current)) {
                workingDays++;
            }

            current = current.plusDays(1);
        }

        return workingDays;
    }

    @Override
    public boolean isWorkingDay(LocalDate date) {

        if (date.getDayOfWeek() == DayOfWeek.SATURDAY
                || date.getDayOfWeek() == DayOfWeek.SUNDAY) {
            return false;
        }

        return !holidayRepository.existsByDate(date);
    }
}