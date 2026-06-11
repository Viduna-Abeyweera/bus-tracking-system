package com.bustracker.dto.websocket;

/**
 * WebSocket message DTO for bus status change notifications.
 *
 * <p>Published to {@code /topic/bus-status} when a bus status changes
 * (e.g., ACTIVE → IDLE). Passengers can use this to know which
 * buses are currently operating.</p>
 */
public class BusStatusMessage {

    private Long busId;
    private String registrationNumber;
    private Long routeId;
    private String routeNumber;
    private String previousStatus;
    private String newStatus;
    private String timestamp;

    // ===== CONSTRUCTORS =====

    public BusStatusMessage() {}

    public BusStatusMessage(Long busId, String registrationNumber,
                            Long routeId, String routeNumber,
                            String previousStatus, String newStatus,
                            String timestamp) {
        this.busId = busId;
        this.registrationNumber = registrationNumber;
        this.routeId = routeId;
        this.routeNumber = routeNumber;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.timestamp = timestamp;
    }

    // ===== GETTERS AND SETTERS =====

    public Long getBusId() { return busId; }
    public void setBusId(Long busId) { this.busId = busId; }
    public String getRegistrationNumber() { return registrationNumber; }
    public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }
    public Long getRouteId() { return routeId; }
    public void setRouteId(Long routeId) { this.routeId = routeId; }
    public String getRouteNumber() { return routeNumber; }
    public void setRouteNumber(String routeNumber) { this.routeNumber = routeNumber; }
    public String getPreviousStatus() { return previousStatus; }
    public void setPreviousStatus(String previousStatus) { this.previousStatus = previousStatus; }
    public String getNewStatus() { return newStatus; }
    public void setNewStatus(String newStatus) { this.newStatus = newStatus; }
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}
