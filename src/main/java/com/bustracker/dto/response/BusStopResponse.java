package com.bustracker.dto.response;

/**
 * Response DTO for bus stop data sent to the frontend.
 *
 * <p>Decouples the API output from the internal {@code BusStop} JPA entity,
 * allowing the response structure to change independently of the database
 * schema.</p>
 */
public class BusStopResponse {

    private Long id;
    private String name;
    private Double latitude;
    private Double longitude;

    // ===== CONSTRUCTORS =====

    public BusStopResponse() {
    }

    public BusStopResponse(Long id, String name, Double latitude, Double longitude) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // ===== GETTERS AND SETTERS =====

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
