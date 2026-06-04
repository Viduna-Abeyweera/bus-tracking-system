package com.bustracker.strategy;

import com.bustracker.util.GeoUtils;
import org.springframework.stereotype.Component;

/**
 * Simple ETA calculation strategy using straight-line (Haversine) distance.
 *
 * <p>This is the default strategy that calculates ETA based on the
 * direct distance between the bus and the stop, assuming an average
 * speed typical of Sri Lankan urban/inter-city buses.</p>
 *
 * <p>Assumptions:</p>
 * <ul>
 *   <li>Average speed: 25 km/h (accounts for traffic, stops, and urban conditions)</li>
 *   <li>Road factor: 1.3 (roads are ~30% longer than straight-line distance)</li>
 * </ul>
 *
 * <p>This strategy is suitable when:</p>
 * <ul>
 *   <li>The bus is not assigned to a specific route</li>
 *   <li>A quick estimate is needed without route data</li>
 *   <li>The bus is very close to the destination stop</li>
 * </ul>
 */
@Component
public class SimpleETAStrategy implements ETACalculationStrategy {

    private static final double AVERAGE_SPEED_KMH = 25.0;
    private static final double ROAD_FACTOR = 1.3;

    @Override
    public double calculateETA(double busLatitude, double busLongitude,
                                double stopLatitude, double stopLongitude) {
        double distance = calculateDistance(busLatitude, busLongitude, stopLatitude, stopLongitude);
        double roadDistance = distance * ROAD_FACTOR;
        return (roadDistance / AVERAGE_SPEED_KMH) * 60; // Convert hours to minutes
    }

    @Override
    public double calculateDistance(double busLatitude, double busLongitude,
                                     double stopLatitude, double stopLongitude) {
        return GeoUtils.calculateDistance(busLatitude, busLongitude, stopLatitude, stopLongitude);
    }

    @Override
    public String getStrategyName() {
        return "Simple (Straight-line)";
    }
}
