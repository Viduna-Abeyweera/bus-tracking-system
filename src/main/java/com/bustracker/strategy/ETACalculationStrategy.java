package com.bustracker.strategy;

/**
 * Strategy interface for ETA (Estimated Time of Arrival) calculations.
 *
 * <p>This interface is the core of the <strong>Strategy Pattern</strong>
 * implementation. Different strategies can calculate ETA using different
 * algorithms:</p>
 * <ul>
 *   <li>{@link SimpleETAStrategy} — straight-line (Haversine) distance</li>
 *   <li>{@link RouteBasedETAStrategy} — follows actual route path through stops</li>
 * </ul>
 *
 * <p>The Strategy Pattern allows the ETA algorithm to be selected at runtime
 * without modifying the service that uses it. This follows the
 * <strong>Open/Closed Principle</strong>: new strategies can be added
 * without modifying existing code.</p>
 *
 * <p>Usage example:</p>
 * <pre>
 *   ETACalculationStrategy strategy = new RouteBasedETAStrategy();
 *   double eta = strategy.calculateETA(busLat, busLon, stopLat, stopLon);
 * </pre>
 */
public interface ETACalculationStrategy {

    /**
     * Calculates the estimated time of arrival in minutes.
     *
     * @param busLatitude   current latitude of the bus
     * @param busLongitude  current longitude of the bus
     * @param stopLatitude  latitude of the destination stop
     * @param stopLongitude longitude of the destination stop
     * @return estimated time in minutes
     */
    double calculateETA(double busLatitude, double busLongitude,
                        double stopLatitude, double stopLongitude);

    /**
     * Calculates the distance in kilometers between two points.
     *
     * @return distance in km
     */
    double calculateDistance(double busLatitude, double busLongitude,
                            double stopLatitude, double stopLongitude);

    /**
     * Returns the name of this strategy for logging/debugging.
     */
    String getStrategyName();
}
