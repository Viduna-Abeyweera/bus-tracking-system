package com.bustracker.entity;

import com.bustracker.enums.BusStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * JPA entity representing a physical bus in the system.
 *
 * <p>A bus has a unique registration number (license plate), is assigned
 * to a specific {@link Route}, and optionally linked to a driver
 * ({@link User} with DRIVER role).</p>
 *
 * <p>The {@code status} field tracks whether the bus is currently
 * operating, idle, or under maintenance.</p>
 *
 * <p>This entity connects the physical world (bus vehicles) to the
 * digital tracking system, enabling the frontend to display which
 * specific bus is on which route.</p>
 */
@Entity
@Table(name = "buses", uniqueConstraints = {
        @UniqueConstraint(columnNames = "registrationNumber")
})
public class Bus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Sri Lankan bus registration number (e.g., "NB-1234", "WP-KA-5678") */
    @Column(nullable = false, unique = true, length = 20)
    private String registrationNumber;

    /** The route this bus is assigned to */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "route_id")
    private Route route;

    /** The driver assigned to this bus (User with DRIVER role) */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "driver_id")
    private User driver;

    /** Current operational status of the bus */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BusStatus status = BusStatus.IDLE;

    /** Total passenger capacity */
    @Column(nullable = false)
    private Integer capacity;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // ===== CONSTRUCTORS =====

    public Bus() {
        this.createdAt = LocalDateTime.now();
    }

    public Bus(String registrationNumber, Route route, Integer capacity) {
        this.registrationNumber = registrationNumber;
        this.route = route;
        this.capacity = capacity;
        this.status = BusStatus.IDLE;
        this.createdAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    // ===== GETTERS AND SETTERS =====

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRegistrationNumber() { return registrationNumber; }
    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public Route getRoute() { return route; }
    public void setRoute(Route route) { this.route = route; }

    public User getDriver() { return driver; }
    public void setDriver(User driver) { this.driver = driver; }

    public BusStatus getStatus() { return status; }
    public void setStatus(BusStatus status) { this.status = status; }

    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
