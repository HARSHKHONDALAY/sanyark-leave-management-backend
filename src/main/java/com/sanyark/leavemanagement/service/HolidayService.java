package com.sanyark.leavemanagement.service;

import com.sanyark.leavemanagement.dto.HolidayRequest;
import com.sanyark.leavemanagement.dto.HolidayResponse;

import java.util.List;

public interface HolidayService {
    HolidayResponse createHoliday(HolidayRequest request);
    HolidayResponse updateHoliday(Long holidayId, HolidayRequest request);
    void deleteHoliday(Long holidayId);
    HolidayResponse getHolidayById(Long holidayId);
    List<HolidayResponse> getHolidays(Integer month, Integer year);
    List<HolidayResponse> getUpcomingHolidays();
}