package com.bustracker.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;

/**
 * Request DTO for submitting a bus location update.
 *
 * <p>This DTO decouples the API contract from the internal {@code BusLocation}
 * entity, following the principle of encapsulation. Validation annotations
 * ensure that invalid data is rejected before reaching the service layer.</p>
 */
public class BusLocationRequest {

    @NotBlank(message = "Bus ID is required")
    private String busId;

    @NotNull(message = "Latitude is required")
    @DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90")
    @DecimalMax(value = "90.0", message = "Latitude must be between -90 and 90")
    private Double latitude;

    @NotNull(message = "Longitude is required")
    @DecimalMin(value = "-180.0", message = "Longitude must be between -180 and 180")
    @DecimalMax(value = "180.0", message = "Longitude must be between -180 and 180")
    private Double longitude;

    // ===== CONSTRUCTORS =====

    public BusLocationRequest() {
    }

    public BusLocationRequest(String busId, Double latitude, Double longitude) {
        this.busId = busId;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // ===== GETTERS AND SETTERS =====

    public String getBusId() {
        return busId;
    }

    public void setBusId(String busId) {
        this.busId = busId;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
