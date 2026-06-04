package com.bustracker.entity;

import jakarta.persistence.*;

/**
 * JPA entity representing an intermediate stop along a {@link Route}.
 *
 * <p>This is a join entity that connects a {@link Route} to a {@link BusStop}
 * with additional metadata about the stop's position in the route sequence
 * and its distance from the route origin.</p>
 *
 * <p>The {@code sequenceOrder} field determines the order in which stops
 * appear along the route (1 = first stop, 2 = second, etc.). This enables
 * the route-based ETA calculation strategy to compute distances along
 * the actual route path rather than straight-line distance.</p>
 *
 * <p>This entity demonstrates the <strong>Association Class</strong> pattern
 * in OOP — it's not just a simple many-to-many join table, but carries
 * its own meaningful attributes.</p>
 */
@Entity
@Table(name = "route_stops", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"route_id", "sequence_order"})
})
public class RouteStop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The route this stop belongs to.
     * ManyToOne: many route-stops can belong to one route.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    /**
     * The physical bus stop at this position in the route.
     * ManyToOne: a bus stop can appear in multiple routes.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bus_stop_id", nullable = false)
    private BusStop busStop;

    /** Position of this stop in the route sequence (1-indexed) */
    @Column(name = "sequence_order", nullable = false)
    private Integer sequenceOrder;

    /** Distance from the route origin to this stop (in km) */
    @Column(nullable = false)
    private Double distanceFromOriginKm;

    /** Estimated time from origin to this stop (in minutes) */
    @Column(nullable = false)
    private Integer estimatedTimeFromOriginMinutes;

    // ===== CONSTRUCTORS =====

    public RouteStop() {
    }

    public RouteStop(Route route, BusStop busStop, Integer sequenceOrder,
                     Double distanceFromOriginKm, Integer estimatedTimeFromOriginMinutes) {
        this.route = route;
        this.busStop = busStop;
        this.sequenceOrder = sequenceOrder;
        this.distanceFromOriginKm = distanceFromOriginKm;
        this.estimatedTimeFromOriginMinutes = estimatedTimeFromOriginMinutes;
    }

    // ===== GETTERS AND SETTERS =====

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Route getRoute() { return route; }
    public void setRoute(Route route) { this.route = route; }

    public BusStop getBusStop() { return busStop; }
    public void setBusStop(BusStop busStop) { this.busStop = busStop; }

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
