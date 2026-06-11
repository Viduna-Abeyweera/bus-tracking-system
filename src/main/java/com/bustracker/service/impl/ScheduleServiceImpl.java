package com.bustracker.service.impl;

import com.bustracker.dto.request.ScheduleRequest;
import com.bustracker.dto.response.ScheduleResponse;
import com.bustracker.entity.Bus;
import com.bustracker.entity.Route;
import com.bustracker.entity.Schedule;
import com.bustracker.exception.ResourceNotFoundException;
import com.bustracker.repository.BusRepository;
import com.bustracker.repository.RouteRepository;
import com.bustracker.repository.ScheduleRepository;
import com.bustracker.service.ScheduleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of {@link ScheduleService}.
 *
 * <p>Manages bus departure schedules with day-of-week support.
 * Converts ISO day numbers to human-readable names for the API response.</p>
 */
@Service
public class ScheduleServiceImpl implements ScheduleService {

    private static final Logger logger = LoggerFactory.getLogger(ScheduleServiceImpl.class);
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    private final ScheduleRepository scheduleRepository;
    private final BusRepository busRepository;
    private final RouteRepository routeRepository;

    public ScheduleServiceImpl(ScheduleRepository scheduleRepository,
                               BusRepository busRepository,
                               RouteRepository routeRepository) {
        this.scheduleRepository = scheduleRepository;
        this.busRepository = busRepository;
        this.routeRepository = routeRepository;
    }

    @Override
    public ScheduleResponse createSchedule(ScheduleRequest request) {
        Bus bus = busRepository.findById(request.getBusId())
                .orElseThrow(() -> new ResourceNotFoundException("Bus", "id", request.getBusId()));

        Route route = routeRepository.findById(request.getRouteId())
                .orElseThrow(() -> new ResourceNotFoundException("Route", "id", request.getRouteId()));

        LocalTime departureTime = LocalTime.parse(request.getDepartureTime(), TIME_FORMAT);

        Schedule schedule = new Schedule(bus, route, departureTime, request.getDayOfWeek());
        Schedule saved = scheduleRepository.save(schedule);

        logger.info("Created schedule: Bus '{}' on route '{}' at {} ({})",
                bus.getRegistrationNumber(), route.getRouteNumber(),
                departureTime, getDayName(request.getDayOfWeek()));

        return toResponse(saved);
    }

    @Override
    public List<ScheduleResponse> getSchedulesByRoute(Long routeId) {
        return scheduleRepository.findByRouteIdAndActiveTrue(routeId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ScheduleResponse> getSchedulesByRouteAndDay(Long routeId, Integer dayOfWeek) {
        return scheduleRepository.findByRouteIdAndDayOfWeekAndActiveTrue(routeId, dayOfWeek).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ScheduleResponse> getSchedulesByBus(Long busId) {
        return scheduleRepository.findByBusId(busId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteSchedule(Long id) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule", "id", id));
        scheduleRepository.delete(schedule);
        logger.info("Deleted schedule ID: {}", id);
    }

    // ===== PRIVATE HELPERS =====

    private ScheduleResponse toResponse(Schedule schedule) {
        ScheduleResponse response = new ScheduleResponse();
        response.setId(schedule.getId());
        response.setBusId(schedule.getBus().getId());
        response.setBusRegistrationNumber(schedule.getBus().getRegistrationNumber());
        response.setRouteId(schedule.getRoute().getId());
        response.setRouteNumber(schedule.getRoute().getRouteNumber());
        response.setRouteName(schedule.getRoute().getName());
        response.setDepartureTime(schedule.getDepartureTime().format(TIME_FORMAT));
        response.setDayOfWeek(schedule.getDayOfWeek());
        response.setDayName(getDayName(schedule.getDayOfWeek()));
        response.setActive(schedule.getActive());
        return response;
    }

    /**
     * Converts ISO day-of-week number to a human-readable name.
     */
    private String getDayName(int dayOfWeek) {
        return DayOfWeek.of(dayOfWeek).name().charAt(0)
                + DayOfWeek.of(dayOfWeek).name().substring(1).toLowerCase();
    }
}
