package com.bustracker.dto.websocket;

import java.time.LocalDateTime;

/**
 * WebSocket message DTO for real-time bus location updates.
 *
 * <p>Published to {@code /topic/bus-locations} when a driver sends
 * a GPS update. All subscribed passengers receive this message
 * immediately, enabling live map tracking.</p>
 *
 * <p>This DTO is intentionally lightweight to minimize WebSocket
 * bandwidth — only essential tracking data is included.</p>
 */
public class LocationUpdateMessage {

    private String busId;
    private String registrationNumber;
    private Long routeId;
    private String routeNumber;
    private Double latitude;
    private Double longitude;
    private Double speed;
    private String timestamp;

    // ===== CONSTRUCTORS =====

    public LocationUpdateMessage() {}

    public LocationUpdateMessage(String busId, String registrationNumber,
                                  Long routeId, String routeNumber,
                                  Double latitude, Double longitude,
                                  Double speed, String timestamp) {
        this.busId = busId;
        this.registrationNumber = registrationNumber;
        this.routeId = routeId;
        this.routeNumber = routeNumber;
        this.latitude = latitude;
        this.longitude = longitude;
        this.speed = speed;
        this.timestamp = timestamp;
    }

    // ===== GETTERS AND SETTERS =====

    public String getBusId() { return busId; }
    public void setBusId(String busId) { this.busId = busId; }
    public String getRegistrationNumber() { return registrationNumber; }
    public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }
    public Long getRouteId() { return routeId; }
    public void setRouteId(Long routeId) { this.routeId = routeId; }
    public String getRouteNumber() { return routeNumber; }
    public void setRouteNumber(String routeNumber) { this.routeNumber = routeNumber; }
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    public Double getSpeed() { return speed; }
    public void setSpeed(Double speed) { this.speed = speed; }
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}
