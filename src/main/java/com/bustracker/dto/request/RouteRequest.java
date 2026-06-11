package com.bustracker.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for creating or updating a bus route.
 */
public class RouteRequest {

    @NotBlank(message = "Route number is required")
    @Size(max = 20, message = "Route number must not exceed 20 characters")
    private String routeNumber;

    @NotBlank(message = "Route name is required")
    @Size(max = 200, message = "Route name must not exceed 200 characters")
    private String name;

    @NotBlank(message = "Origin is required")
    private String origin;

    @NotBlank(message = "Destination is required")
    private String destination;

    @NotNull(message = "Distance is required")
    @Positive(message = "Distance must be positive")
    private Double distanceKm;

    @NotNull(message = "Estimated duration is required")
    @Positive(message = "Duration must be positive")
    private Integer estimatedDurationMinutes;

    // ===== CONSTRUCTORS =====

    public RouteRequest() {}

    public RouteRequest(String routeNumber, String name, String origin,
                        String destination, Double distanceKm, Integer estimatedDurationMinutes) {
        this.routeNumber = routeNumber;
        this.name = name;
        this.origin = origin;
        this.destination = destination;
        this.distanceKm = distanceKm;
        this.estimatedDurationMinutes = estimatedDurationMinutes;
    }

    // ===== GETTERS AND SETTERS =====

    public String getRouteNumber() { return routeNumber; }
    public void setRouteNumber(String routeNumber) { this.routeNumber = routeNumber; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getOrigin() { return origin; }
    public void setOrigin(String origin) { this.origin = origin; }
    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }
    public Double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(Double distanceKm) { this.distanceKm = distanceKm; }
    public Integer getEstimatedDurationMinutes() { return estimatedDurationMinutes; }
    public void setEstimatedDurationMinutes(Integer estimatedDurationMinutes) {
        this.estimatedDurationMinutes = estimatedDurationMinutes;
    }
}
