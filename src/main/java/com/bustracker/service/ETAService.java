package com.bustracker.service;

import com.bustracker.dto.response.ETAResponse;

import java.util.List;

/**
 * Service interface for Estimated Time of Arrival (ETA) calculations.
 *
 * <p>This interface demonstrates the <strong>Interface Segregation Principle
 * (ISP)</strong>: ETA calculation is a separate concern from bus location
 * tracking. Clients that only need ETA should not be forced to depend on
 * location-management methods.</p>
 *
 * <p>It also supports the <strong>Open/Closed Principle (OCP)</strong>:
 * new ETA calculation strategies (e.g., route-based, traffic-aware) can
 * be provided as new implementations without modifying existing code.</p>
 */
public interface ETAService {

    /**
     * Calculates the straight-line distance between two GPS coordinates.
     *
     * @param lat1 latitude of point 1
     * @param lon1 longitude of point 1
     * @param lat2 latitude of point 2
     * @param lon2 longitude of point 2
     * @return distance in kilometers
     */
    double calculateDistance(double lat1, double lon1, double lat2, double lon2);

    /**
     * Calculates the estimated time of arrival in minutes for a bus
     * to reach a specific bus stop.
     *
     * @param busLat  current latitude of the bus
     * @param busLon  current longitude of the bus
     * @param stopLat latitude of the bus stop
     * @param stopLon longitude of the bus stop
     * @return estimated time in minutes
     */
    double calculateETAMinutes(double busLat, double busLon,
                                double stopLat, double stopLon);

    /**
     * Calculates ETA for all active buses to a specific bus stop.
     *
     * @param stopId the ID of the target bus stop
     * @return list of ETA responses, sorted by nearest bus first
     */
    List<ETAResponse> calculateETAForStop(Long stopId);
}