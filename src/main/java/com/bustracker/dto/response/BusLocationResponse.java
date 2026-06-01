package com.bustracker.dto.response;

import java.time.LocalDateTime;

/**
 * Response DTO for bus location data sent to the frontend.
 *
 * <p>This class decouples the REST API response from the internal
 * {@code BusLocation} JPA entity. By using a dedicated response DTO,
 * we ensure that:</p>
 * <ul>
 *   <li>Internal database fields (like auto-generated IDs) are only
 *       exposed when explicitly intended</li>
 *   <li>The API contract can evolve independently of the entity schema</li>
 *   <li>Sensitive or unnecessary fields are never accidentally leaked</li>
 * </ul>
 */
public class BusLocationResponse {

    private Long id;
    private String busId;
    private Double latitude;
    private Double longitude;
    private LocalDateTime timestamp;

    // ===== CONSTRUCTORS =====

    public BusLocationResponse() {
    }

    public BusLocationResponse(Long id, String busId, Double latitude,
                                Double longitude, LocalDateTime timestamp) {
        this.id = id;
        this.busId = busId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
    }

    // ===== GETTERS AND SETTERS =====

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
