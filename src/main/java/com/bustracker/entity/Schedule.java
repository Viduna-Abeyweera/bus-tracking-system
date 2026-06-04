package com.bustracker.entity;

import jakarta.persistence.*;
import java.time.LocalTime;

/**
 * JPA entity representing a scheduled departure time for a bus on a route.
 *
 * <p>Each schedule entry defines when a specific {@link Bus} departs
 * from the origin of its assigned {@link Route}. For example:</p>
 * <pre>
 *   Bus NB-1234 | Route 1 (Colombo→Kandy) | Departs at 06:30 | Monday-Friday
 * </pre>
 *
 * <p>The {@code dayOfWeek} field uses ISO-8601 standard (1=Monday, 7=Sunday)
 * to support different schedules for weekdays vs weekends.</p>
 */
@Entity
@Table(name = "schedules")
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The bus this schedule applies to */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bus_id", nullable = false)
    private Bus bus;

    /** The route this schedule is for */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    /** Departure time from the route origin */
    @Column(nullable = false)
    private LocalTime departureTime;

    /** Day of week (1=Monday, 7=Sunday, ISO-8601) */
    @Column(nullable = false)
    private Integer dayOfWeek;

    /** Whether this schedule is currently active */
    @Column(nullable = false)
    private Boolean active = true;

    // ===== CONSTRUCTORS =====

    public Schedule() {
    }

    public Schedule(Bus bus, Route route, LocalTime departureTime, Integer dayOfWeek) {
        this.bus = bus;
        this.route = route;
        this.departureTime = departureTime;
        this.dayOfWeek = dayOfWeek;
        this.active = true;
    }

    // ===== GETTERS AND SETTERS =====

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Bus getBus() { return bus; }
    public void setBus(Bus bus) { this.bus = bus; }

    public Route getRoute() { return route; }
    public void setRoute(Route route) { this.route = route; }

    public LocalTime getDepartureTime() { return departureTime; }
    public void setDepartureTime(LocalTime departureTime) { this.departureTime = departureTime; }

    public Integer getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(Integer dayOfWeek) { this.dayOfWeek = dayOfWeek; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
