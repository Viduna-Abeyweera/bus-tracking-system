package com.bustracker.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Request DTO for creating or updating a bus.
 */
public class BusRequest {

    @NotBlank(message = "Registration number is required")
    private String registrationNumber;

    @NotNull(message = "Route ID is required")
    private Long routeId;

    private Long driverId;

    @NotNull(message = "Capacity is required")
    @Positive(message = "Capacity must be positive")
    private Integer capacity;

    // ===== CONSTRUCTORS =====

    public BusRequest() {}

    public BusRequest(String registrationNumber, Long routeId, Long driverId, Integer capacity) {
        this.registrationNumber = registrationNumber;
        this.routeId = routeId;
        this.driverId = driverId;
        this.capacity = capacity;
    }

    // ===== GETTERS AND SETTERS =====

    public String getRegistrationNumber() { return registrationNumber; }
    public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }
    public Long getRouteId() { return routeId; }
    public void setRouteId(Long routeId) { this.routeId = routeId; }
    public Long getDriverId() { return driverId; }
    public void setDriverId(Long driverId) { this.driverId = driverId; }
    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }
}
