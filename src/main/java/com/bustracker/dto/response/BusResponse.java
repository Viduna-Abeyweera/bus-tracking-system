package com.bustracker.dto.response;

/**
 * Response DTO for a bus entity.
 */
public class BusResponse {

    private Long id;
    private String registrationNumber;
    private String routeNumber;
    private String routeName;
    private Long routeId;
    private String driverName;
    private Long driverId;
    private String status;
    private Integer capacity;

    // ===== CONSTRUCTORS =====

    public BusResponse() {}

    public BusResponse(Long id, String registrationNumber, String routeNumber, String routeName,
                       Long routeId, String driverName, Long driverId, String status, Integer capacity) {
        this.id = id;
        this.registrationNumber = registrationNumber;
        this.routeNumber = routeNumber;
        this.routeName = routeName;
        this.routeId = routeId;
        this.driverName = driverName;
        this.driverId = driverId;
        this.status = status;
        this.capacity = capacity;
    }

    // ===== GETTERS AND SETTERS =====

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getRegistrationNumber() { return registrationNumber; }
    public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }
    public String getRouteNumber() { return routeNumber; }
    public void setRouteNumber(String routeNumber) { this.routeNumber = routeNumber; }
    public String getRouteName() { return routeName; }
    public void setRouteName(String routeName) { this.routeName = routeName; }
    public Long getRouteId() { return routeId; }
    public void setRouteId(Long routeId) { this.routeId = routeId; }
    public String getDriverName() { return driverName; }
    public void setDriverName(String driverName) { this.driverName = driverName; }
    public Long getDriverId() { return driverId; }
    public void setDriverId(Long driverId) { this.driverId = driverId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }
}
