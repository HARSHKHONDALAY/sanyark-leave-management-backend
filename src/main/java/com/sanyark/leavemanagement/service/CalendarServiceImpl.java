package com.sanyark.leavemanagement.service;

import com.sanyark.leavemanagement.dto.CalendarEntryResponse;
import com.sanyark.leavemanagement.dto.LeaveCalendarResponse;
import com.sanyark.leavemanagement.entity.Holiday;
import com.sanyark.leavemanagement.entity.LeaveRequest;
import com.sanyark.leavemanagement.entity.User;
import com.sanyark.leavemanagement.exception.ResourceNotFoundException;
import com.sanyark.leavemanagement.repository.HolidayRepository;
import com.sanyark.leavemanagement.repository.LeaveRequestRepository;
import com.sanyark.leavemanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CalendarServiceImpl implements CalendarService {

    private final HolidayRepository holidayRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final UserRepository userRepository;

    @Override
    public LeaveCalendarResponse getCompanyCalendar(Integer month, Integer year) {

        YearMonth ym = validateAndBuildYearMonth(month, year);
        LocalDate startDate = ym.atDay(1);
        LocalDate endDate = ym.atEndOfMonth();

        List<CalendarEntryResponse> entries = new ArrayList<>();

        addHolidayAndWeeklyOffEntries(entries, startDate, endDate);

        sortEntries(entries);

        return LeaveCalendarResponse.builder()
                .month(month)
                .year(year)
                .entries(entries)
                .build();
    }

    @Override
    public LeaveCalendarResponse getMyCalendar(Integer month, Integer year, Long userId) {

        YearMonth ym = validateAndBuildYearMonth(month, year);
        LocalDate startDate = ym.atDay(1);
        LocalDate endDate = ym.atEndOfMonth();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<CalendarEntryResponse> entries = new ArrayList<>();

        addHolidayAndWeeklyOffEntries(entries, startDate, endDate);
        addUserLeaveEntries(entries, user, startDate, endDate);

        sortEntries(entries);

        return LeaveCalendarResponse.builder()
                .month(month)
                .year(year)
                .entries(entries)
                .build();
    }

    @Override
    public LeaveCalendarResponse getTeamCalendar(Integer month, Integer year) {

        YearMonth ym = validateAndBuildYearMonth(month, year);
        LocalDate startDate = ym.atDay(1);
        LocalDate endDate = ym.atEndOfMonth();

        List<CalendarEntryResponse> entries = new ArrayList<>();

        addHolidayAndWeeklyOffEntries(entries, startDate, endDate);
        addTeamLeaveEntries(entries, startDate, endDate);

        sortEntries(entries);

        return LeaveCalendarResponse.builder()
                .month(month)
                .year(year)
                .entries(entries)
                .build();
    }

    private YearMonth validateAndBuildYearMonth(Integer month, Integer year) {

        if (month == null || year == null) {
            throw new IllegalArgumentException("Month and year are required");
        }

        return YearMonth.of(year, month);
    }

    private void addHolidayAndWeeklyOffEntries(List<CalendarEntryResponse> entries,
                                               LocalDate startDate,
                                               LocalDate endDate) {

        List<Holiday> holidays = holidayRepository
                .findByDateBetweenOrderByDateAsc(startDate, endDate);

        for (Holiday holiday : holidays) {

            entries.add(
                    CalendarEntryResponse.builder()
                            .date(holiday.getDate())
                            .title(holiday.getName())
                            .type("HOLIDAY")
                            .status(holiday.getType().name())
                            .employeeName(null)
                            .employeeCode(null)
                            .description(holiday.getDescription())
                            .dayOfWeek(holiday.getDate().getDayOfWeek().name())
                            .build()
            );
        }

        LocalDate current = startDate;

        while (!current.isAfter(endDate)) {

            final LocalDate loopDate = current;

            if (loopDate.getDayOfWeek() == DayOfWeek.SATURDAY ||
                    loopDate.getDayOfWeek() == DayOfWeek.SUNDAY) {

                boolean holidayAlreadyExists = holidays.stream()
                        .anyMatch(h -> h.getDate().equals(loopDate));

                if (!holidayAlreadyExists) {

                    entries.add(
                            CalendarEntryResponse.builder()
                                    .date(loopDate)
                                    .title(loopDate.getDayOfWeek() == DayOfWeek.SATURDAY
                                            ? "Saturday Off"
                                            : "Sunday Off")
                                    .type("WEEKLY_OFF")
                                    .status("OFF")
                                    .employeeName(null)
                                    .employeeCode(null)
                                    .description("Weekly off")
                                    .dayOfWeek(loopDate.getDayOfWeek().name())
                                    .build()
                    );
                }
            }

            current = current.plusDays(1);
        }
    }

    private void addUserLeaveEntries(List<CalendarEntryResponse> entries,
                                     User user,
                                     LocalDate startDate,
                                     LocalDate endDate) {

        List<LeaveRequest> leaveRequests =
                leaveRequestRepository
                        .findByUserAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByStartDateAsc(
                                user, endDate, startDate
                        );

        for (LeaveRequest leave : leaveRequests) {

            LocalDate current = leave.getStartDate().isBefore(startDate)
                    ? startDate
                    : leave.getStartDate();

            LocalDate leaveEnd = leave.getEndDate().isAfter(endDate)
                    ? endDate
                    : leave.getEndDate();

            while (!current.isAfter(leaveEnd)) {

                entries.add(
                        CalendarEntryResponse.builder()
                                .date(current)
                                .title(leave.getLeaveType().name() + " Leave")
                                .type("MY_LEAVE")
                                .status(leave.getStatus().name())
                                .employeeName(user.getFullName())
                                .employeeCode(user.getEmployeeCode())
                                .description(leave.getReason())
                                .dayOfWeek(current.getDayOfWeek().name())
                                .build()
                );

                current = current.plusDays(1);
            }
        }
    }

    private void addTeamLeaveEntries(List<CalendarEntryResponse> entries,
                                     LocalDate startDate,
                                     LocalDate endDate) {

        List<LeaveRequest> leaveRequests =
                leaveRequestRepository
                        .findByStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByStartDateAsc(
                                endDate, startDate
                        );

        for (LeaveRequest leave : leaveRequests) {

            User user = leave.getUser();

            LocalDate current = leave.getStartDate().isBefore(startDate)
                    ? startDate
                    : leave.getStartDate();

            LocalDate leaveEnd = leave.getEndDate().isAfter(endDate)
                    ? endDate
                    : leave.getEndDate();

            while (!current.isAfter(leaveEnd)) {

                entries.add(
                        CalendarEntryResponse.builder()
                                .date(current)
                                .title(leave.getLeaveType().name() + " Leave")
                                .type("TEAM_LEAVE")
                                .status(leave.getStatus().name())
                                .employeeName(user.getFullName())
                                .employeeCode(user.getEmployeeCode())
                                .description(leave.getReason())
                                .dayOfWeek(current.getDayOfWeek().name())
                                .build()
                );

                current = current.plusDays(1);
            }
        }
    }

    private void sortEntries(List<CalendarEntryResponse> entries) {

        entries.sort(
                Comparator.comparing(CalendarEntryResponse::getDate)
                        .thenComparing(CalendarEntryResponse::getType)
                        .thenComparing(e -> e.getEmployeeName() == null ? "" : e.getEmployeeName())
        );
    }
}