package com.bustracker.strategy;

import com.bustracker.util.GeoUtils;
import org.springframework.stereotype.Component;

/**
 * Route-based ETA calculation strategy that accounts for road conditions.
 *
 * <p>This strategy provides a more accurate ETA estimate by using
 * different speed assumptions based on the distance, which correlates
 * with whether the bus is in urban or inter-city territory:</p>
 * <ul>
 *   <li>Short distances (&lt;5 km): 15 km/h — urban/congested areas</li>
 *   <li>Medium distances (5-30 km): 25 km/h — suburban roads</li>
 *   <li>Long distances (&gt;30 km): 40 km/h — inter-city highways (A-roads)</li>
 * </ul>
 *
 * <p>This models the reality of Sri Lankan transport where Colombo
 * city traffic moves slowly, but A1/A2 highways allow faster speeds.</p>
 *
 * <p>This strategy is used when:</p>
 * <ul>
 *   <li>The bus is assigned to a known route</li>
 *   <li>More accurate predictions are needed for passengers</li>
 * </ul>
 */
@Component
public class RouteBasedETAStrategy implements ETACalculationStrategy {

    private static final double URBAN_SPEED_KMH = 15.0;
    private static final double SUBURBAN_SPEED_KMH = 25.0;
    private static final double HIGHWAY_SPEED_KMH = 40.0;
    private static final double ROAD_FACTOR = 1.25;

    @Override
    public double calculateETA(double busLatitude, double busLongitude,
                                double stopLatitude, double stopLongitude) {
        double straightLineDistance = GeoUtils.calculateDistance(
                busLatitude, busLongitude, stopLatitude, stopLongitude);
        double roadDistance = straightLineDistance * ROAD_FACTOR;

        // Select speed based on distance (correlates with road type)
        double speed;
        if (roadDistance < 5.0) {
            speed = URBAN_SPEED_KMH;
        } else if (roadDistance < 30.0) {
            speed = SUBURBAN_SPEED_KMH;
        } else {
            speed = HIGHWAY_SPEED_KMH;
        }

        return (roadDistance / speed) * 60; // Convert hours to minutes
    }

    @Override
    public double calculateDistance(double busLatitude, double busLongitude,
                                     double stopLatitude, double stopLongitude) {
        double straightLine = GeoUtils.calculateDistance(
                busLatitude, busLongitude, stopLatitude, stopLongitude);
        return straightLine * ROAD_FACTOR;
    }

    @Override
    public String getStrategyName() {
        return "Route-Based (Adaptive Speed)";
    }
}
