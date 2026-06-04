package com.bustracker.dto.response;

/**
 * Response DTO for a schedule entry.
 */
public class ScheduleResponse {

    private Long id;
    private Long busId;
    private String busRegistrationNumber;
    private Long routeId;
    private String routeNumber;
    private String routeName;
    private String departureTime;
    private Integer dayOfWeek;
    private String dayName;
    private Boolean active;

    // ===== CONSTRUCTORS =====

    public ScheduleResponse() {}

    // ===== GETTERS AND SETTERS =====

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getBusId() { return busId; }
    public void setBusId(Long busId) { this.busId = busId; }
    public String getBusRegistrationNumber() { return busRegistrationNumber; }
    public void setBusRegistrationNumber(String busRegistrationNumber) {
        this.busRegistrationNumber = busRegistrationNumber;
    }
    public Long getRouteId() { return routeId; }
    public void setRouteId(Long routeId) { this.routeId = routeId; }
    public String getRouteNumber() { return routeNumber; }
    public void setRouteNumber(String routeNumber) { this.routeNumber = routeNumber; }
    public String getRouteName() { return routeName; }
    public void setRouteName(String routeName) { this.routeName = routeName; }
    public String getDepartureTime() { return departureTime; }
    public void setDepartureTime(String departureTime) { this.departureTime = departureTime; }
    public Integer getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(Integer dayOfWeek) { this.dayOfWeek = dayOfWeek; }
    public String getDayName() { return dayName; }
    public void setDayName(String dayName) { this.dayName = dayName; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
