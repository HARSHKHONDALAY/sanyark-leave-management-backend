package com.sanyark.leavemanagement.service;

import com.sanyark.leavemanagement.dto.HolidayRequest;
import com.sanyark.leavemanagement.dto.HolidayResponse;
import com.sanyark.leavemanagement.entity.Holiday;
import com.sanyark.leavemanagement.exception.InvalidLeaveActionException;
import com.sanyark.leavemanagement.exception.ResourceNotFoundException;
import com.sanyark.leavemanagement.repository.HolidayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HolidayServiceImpl implements HolidayService {

    private final HolidayRepository holidayRepository;

    @Override
    public HolidayResponse createHoliday(HolidayRequest request) {
        if (holidayRepository.existsByDate(request.getDate())) {
            throw new InvalidLeaveActionException("A holiday already exists on this date");
        }

        Holiday holiday = Holiday.builder()
                .name(request.getName())
                .date(request.getDate())
                .type(request.getType())
                .description(request.getDescription())
                .build();

        Holiday savedHoliday = holidayRepository.save(holiday);
        return mapToResponse(savedHoliday);
    }

    @Override
    public HolidayResponse updateHoliday(Long holidayId, HolidayRequest request) {
        Holiday holiday = holidayRepository.findById(holidayId)
                .orElseThrow(() -> new ResourceNotFoundException("Holiday not found"));

        holidayRepository.findByDate(request.getDate())
                .ifPresent(existingHoliday -> {
                    if (!existingHoliday.getId().equals(holidayId)) {
                        throw new InvalidLeaveActionException("Another holiday already exists on this date");
                    }
                });

        holiday.setName(request.getName());
        holiday.setDate(request.getDate());
        holiday.setType(request.getType());
        holiday.setDescription(request.getDescription());

        Holiday updatedHoliday = holidayRepository.save(holiday);
        return mapToResponse(updatedHoliday);
    }

    @Override
    public void deleteHoliday(Long holidayId) {
        Holiday holiday = holidayRepository.findById(holidayId)
                .orElseThrow(() -> new ResourceNotFoundException("Holiday not found"));

        holidayRepository.delete(holiday);
    }

    @Override
    public HolidayResponse getHolidayById(Long holidayId) {
        Holiday holiday = holidayRepository.findById(holidayId)
                .orElseThrow(() -> new ResourceNotFoundException("Holiday not found"));

        return mapToResponse(holiday);
    }

    @Override
    public List<HolidayResponse> getHolidays(Integer month, Integer year) {
        List<Holiday> holidays;

        if (month != null && year != null) {
            YearMonth yearMonth = YearMonth.of(year, month);
            LocalDate startDate = yearMonth.atDay(1);
            LocalDate endDate = yearMonth.atEndOfMonth();

            holidays = holidayRepository.findByDateBetweenOrderByDateAsc(startDate, endDate);
        } else if (year != null) {
            LocalDate startDate = LocalDate.of(year, 1, 1);
            LocalDate endDate = LocalDate.of(year, 12, 31);

            holidays = holidayRepository.findByDateBetweenOrderByDateAsc(startDate, endDate);
        } else {
            holidays = holidayRepository.findAllByOrderByDateAsc();
        }

        return holidays.stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<HolidayResponse> getUpcomingHolidays() {
        LocalDate today = LocalDate.now();

        return holidayRepository.findByDateGreaterThanEqualOrderByDateAsc(today)
                .stream()
                .limit(10)
                .map(this::mapToResponse)
                .toList();
    }

    private HolidayResponse mapToResponse(Holiday holiday) {
        return HolidayResponse.builder()
                .id(holiday.getId())
                .name(holiday.getName())
                .date(holiday.getDate())
                .type(holiday.getType().name())
                .description(holiday.getDescription())
                .dayOfWeek(holiday.getDate().getDayOfWeek().name())
                .month(holiday.getDate().getMonthValue())
                .year(holiday.getDate().getYear())
                .build();
    }
}