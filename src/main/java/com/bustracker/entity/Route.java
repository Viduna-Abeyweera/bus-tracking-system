package com.bustracker.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * JPA entity representing a bus route in the system.
 *
 * <p>A route defines a path between two endpoints (e.g., Colombo Fort → Kandy).
 * Each route has an ordered list of intermediate {@link RouteStop}s and can
 * have multiple {@link Bus}es and {@link Schedule}s assigned to it.</p>
 *
 * <p>The {@code routeNumber} follows the real Sri Lankan bus numbering system
 * (e.g., "1" for Colombo–Kandy, "2" for Colombo–Galle).</p>
 *
 * <p>Relationships demonstrate OOP composition:</p>
 * <ul>
 *   <li>{@code Route} "has many" {@code RouteStop}s (ordered sequence)</li>
 *   <li>{@code Route} "has many" {@code Bus}es (assigned vehicles)</li>
 *   <li>{@code Route} "has many" {@code Schedule}s (departure times)</li>
 * </ul>
 */
@Entity
@Table(name = "routes", uniqueConstraints = {
        @UniqueConstraint(columnNames = "routeNumber")
})
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Sri Lankan bus route number (e.g., "1", "2", "32") */
    @Column(nullable = false, unique = true, length = 20)
    private String routeNumber;

    /** Human-readable name (e.g., "Colombo Fort - Kandy") */
    @Column(nullable = false, length = 200)
    private String name;

    /** Starting point of the route */
    @Column(nullable = false, length = 100)
    private String origin;

    /** Ending point of the route */
    @Column(nullable = false, length = 100)
    private String destination;

    /** Total distance in kilometers */
    @Column(nullable = false)
    private Double distanceKm;

    /** Estimated total journey time in minutes */
    @Column(nullable = false)
    private Integer estimatedDurationMinutes;

    /** Whether the route is currently active */
    @Column(nullable = false)
    private Boolean active = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Ordered list of stops along this route.
     * CascadeType.ALL ensures stops are persisted/deleted with the route.
     * orphanRemoval ensures stops removed from the list are deleted from DB.
     */
    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sequenceOrder ASC")
    private List<RouteStop> routeStops = new ArrayList<>();

    // ===== CONSTRUCTORS =====

    public Route() {
        this.createdAt = LocalDateTime.now();
    }

    public Route(String routeNumber, String name, String origin, String destination,
                 Double distanceKm, Integer estimatedDurationMinutes) {
        this.routeNumber = routeNumber;
        this.name = name;
        this.origin = origin;
        this.destination = destination;
        this.distanceKm = distanceKm;
        this.estimatedDurationMinutes = estimatedDurationMinutes;
        this.active = true;
        this.createdAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    // ===== HELPER METHODS =====

    /**
     * Adds a stop to this route with proper bidirectional relationship management.
     */
    public void addRouteStop(RouteStop routeStop) {
        routeStops.add(routeStop);
        routeStop.setRoute(this);
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

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<RouteStop> getRouteStops() { return routeStops; }
    public void setRouteStops(List<RouteStop> routeStops) { this.routeStops = routeStops; }
}
