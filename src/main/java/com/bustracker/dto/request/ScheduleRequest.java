package com.bustracker.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Request DTO for creating or updating a schedule.
 */
public class ScheduleRequest {

    @NotNull(message = "Bus ID is required")
    private Long busId;

    @NotNull(message = "Route ID is required")
    private Long routeId;

    /** Departure time in HH:mm format (e.g., "06:30") */
    @NotNull(message = "Departure time is required")
    private String departureTime;

    /** Day of week (1=Monday, 7=Sunday) */
    @NotNull(message = "Day of week is required")
    @Min(value = 1, message = "Day of week must be between 1 (Monday) and 7 (Sunday)")
    @Max(value = 7, message = "Day of week must be between 1 (Monday) and 7 (Sunday)")
    private Integer dayOfWeek;

    // ===== CONSTRUCTORS =====

    public ScheduleRequest() {}

    public ScheduleRequest(Long busId, Long routeId, String departureTime, Integer dayOfWeek) {
        this.busId = busId;
        this.routeId = routeId;
        this.departureTime = departureTime;
        this.dayOfWeek = dayOfWeek;
    }

    // ===== GETTERS AND SETTERS =====

    public Long getBusId() { return busId; }
    public void setBusId(Long busId) { this.busId = busId; }
    public Long getRouteId() { return routeId; }
    public void setRouteId(Long routeId) { this.routeId = routeId; }
    public String getDepartureTime() { return departureTime; }
    public void setDepartureTime(String departureTime) { this.departureTime = departureTime; }
    public Integer getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(Integer dayOfWeek) { this.dayOfWeek = dayOfWeek; }
}
