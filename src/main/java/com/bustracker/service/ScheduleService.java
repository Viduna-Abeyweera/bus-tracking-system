package com.bustracker.service;

import com.bustracker.dto.request.ScheduleRequest;
import com.bustracker.dto.response.ScheduleResponse;

import java.util.List;

/**
 * Service interface for schedule management operations.
 */
public interface ScheduleService {

    ScheduleResponse createSchedule(ScheduleRequest request);

    List<ScheduleResponse> getSchedulesByRoute(Long routeId);

    List<ScheduleResponse> getSchedulesByRouteAndDay(Long routeId, Integer dayOfWeek);

    List<ScheduleResponse> getSchedulesByBus(Long busId);

    void deleteSchedule(Long id);
}
