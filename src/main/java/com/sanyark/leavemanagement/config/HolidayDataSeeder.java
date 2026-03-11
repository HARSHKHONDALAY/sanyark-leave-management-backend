package com.sanyark.leavemanagement.config;

import com.sanyark.leavemanagement.entity.Holiday;
import com.sanyark.leavemanagement.enums.HolidayType;
import com.sanyark.leavemanagement.repository.HolidayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class HolidayDataSeeder implements CommandLineRunner {

    private final HolidayRepository holidayRepository;

    @Override
    public void run(String... args) {
        seed2026Holidays();
    }

    private void seed2026Holidays() {
        List<Holiday> defaultHolidays = List.of(
                Holiday.builder()
                        .name("Republic Day")
                        .date(LocalDate.of(2026, 1, 26))
                        .type(HolidayType.PUBLIC)
                        .description("National holiday")
                        .build(),

                Holiday.builder()
                        .name("Holi")
                        .date(LocalDate.of(2026, 3, 4))
                        .type(HolidayType.PUBLIC)
                        .description("Festival of colours")
                        .build(),

                Holiday.builder()
                        .name("Id-ul-Fitr")
                        .date(LocalDate.of(2026, 3, 21))
                        .type(HolidayType.PUBLIC)
                        .description("Festival holiday")
                        .build(),

                Holiday.builder()
                        .name("Ram Navami")
                        .date(LocalDate.of(2026, 3, 26))
                        .type(HolidayType.PUBLIC)
                        .description("Festival holiday")
                        .build(),

                Holiday.builder()
                        .name("Mahavir Jayanti")
                        .date(LocalDate.of(2026, 3, 31))
                        .type(HolidayType.PUBLIC)
                        .description("Festival holiday")
                        .build(),

                Holiday.builder()
                        .name("Good Friday")
                        .date(LocalDate.of(2026, 4, 3))
                        .type(HolidayType.PUBLIC)
                        .description("Christian observance")
                        .build(),

                Holiday.builder()
                        .name("Buddha Purnima")
                        .date(LocalDate.of(2026, 5, 1))
                        .type(HolidayType.PUBLIC)
                        .description("Festival holiday")
                        .build(),

                Holiday.builder()
                        .name("Independence Day")
                        .date(LocalDate.of(2026, 8, 15))
                        .type(HolidayType.PUBLIC)
                        .description("National holiday")
                        .build(),

                Holiday.builder()
                        .name("Mahatma Gandhi's Birthday")
                        .date(LocalDate.of(2026, 10, 2))
                        .type(HolidayType.PUBLIC)
                        .description("National holiday")
                        .build(),

                Holiday.builder()
                        .name("Dussehra")
                        .date(LocalDate.of(2026, 10, 20))
                        .type(HolidayType.PUBLIC)
                        .description("Festival holiday")
                        .build(),

                Holiday.builder()
                        .name("Diwali")
                        .date(LocalDate.of(2026, 11, 8))
                        .type(HolidayType.PUBLIC)
                        .description("Festival holiday")
                        .build(),

                Holiday.builder()
                        .name("Christmas Day")
                        .date(LocalDate.of(2026, 12, 25))
                        .type(HolidayType.PUBLIC)
                        .description("Christmas holiday")
                        .build()
        );

        for (Holiday holiday : defaultHolidays) {
            if (!holidayRepository.existsByDate(holiday.getDate())) {
                holidayRepository.save(holiday);
            }
        }
    }
}