package com.bustracker.dto.response;

import java.util.List;

/**
 * Response DTO for a route including its ordered stops.
 */
public class RouteResponse {

    private Long id;
    private String routeNumber;
    private String name;
    private String origin;
    private String destination;
    private Double distanceKm;
    private Integer estimatedDurationMinutes;
    private Boolean active;
    private List<RouteStopResponse> stops;

    // ===== CONSTRUCTORS =====

    public RouteResponse() {}

    public RouteResponse(Long id, String routeNumber, String name, String origin,
                         String destination, Double distanceKm, Integer estimatedDurationMinutes,
                         Boolean active, List<RouteStopResponse> stops) {
        this.id = id;
        this.routeNumber = routeNumber;
        this.name = name;
        this.origin = origin;
        this.destination = destination;
        this.distanceKm = distanceKm;
        this.estimatedDurationMinutes = estimatedDurationMinutes;
        this.active = active;
        this.stops = stops;
    }

    // ===== GETTERS AND SETTERS =====

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
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
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
    public List<RouteStopResponse> getStops() { return stops; }
    public void setStops(List<RouteStopResponse> stops) { this.stops = stops; }

    /**
     * Nested DTO for a stop within a route response.
     */
    public static class RouteStopResponse {
        private Long stopId;
        private String stopName;
        private Double latitude;
        private Double longitude;
        private Integer sequenceOrder;
        private Double distanceFromOriginKm;
        private Integer estimatedTimeFromOriginMinutes;

        public RouteStopResponse() {}

        public RouteStopResponse(Long stopId, String stopName, Double latitude, Double longitude,
                                 Integer sequenceOrder, Double distanceFromOriginKm,
                                 Integer estimatedTimeFromOriginMinutes) {
            this.stopId = stopId;
            this.stopName = stopName;
            this.latitude = latitude;
            this.longitude = longitude;
            this.sequenceOrder = sequenceOrder;
            this.distanceFromOriginKm = distanceFromOriginKm;
            this.estimatedTimeFromOriginMinutes = estimatedTimeFromOriginMinutes;
        }

        public Long getStopId() { return stopId; }
        public void setStopId(Long stopId) { this.stopId = stopId; }
        public String getStopName() { return stopName; }
        public void setStopName(String stopName) { this.stopName = stopName; }
        public Double getLatitude() { return latitude; }
        public void setLatitude(Double latitude) { this.latitude = latitude; }
        public Double getLongitude() { return longitude; }
        public void setLongitude(Double longitude) { this.longitude = longitude; }
        public Integer getSequenceOrder() { return sequenceOrder; }
        public void setSequenceOrder(Integer sequenceOrder) { this.sequenceOrder = sequenceOrder; }
        public Double getDistanceFromOriginKm() { return distanceFromOriginKm; }
        public void setDistanceFromOriginKm(Double distanceFromOriginKm) {
            this.distanceFromOriginKm = distanceFromOriginKm;
        }
        public Integer getEstimatedTimeFromOriginMinutes() { return estimatedTimeFromOriginMinutes; }
        public void setEstimatedTimeFromOriginMinutes(Integer estimatedTimeFromOriginMinutes) {
            this.estimatedTimeFromOriginMinutes = estimatedTimeFromOriginMinutes;
        }
    }
}
