package com.bustracker.strategy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the ETA Strategy Pattern implementations.
 *
 * <p>Tests both {@link SimpleETAStrategy} and {@link RouteBasedETAStrategy}
 * to verify they produce valid but different results, demonstrating
 * the Strategy Pattern's flexibility.</p>
 */
class ETAStrategyTest {

    private final SimpleETAStrategy simpleStrategy = new SimpleETAStrategy();
    private final RouteBasedETAStrategy routeBasedStrategy = new RouteBasedETAStrategy();

    // Colombo Fort GPS
    private static final double COLOMBO_LAT = 6.9344;
    private static final double COLOMBO_LON = 79.8428;
    // Kandy GPS
    private static final double KANDY_LAT = 7.2906;
    private static final double KANDY_LON = 80.6337;
    // Borella GPS (close to Colombo)
    private static final double BORELLA_LAT = 6.9147;
    private static final double BORELLA_LON = 79.8779;

    @Test
    @DisplayName("Simple strategy should return correct strategy name")
    void testSimpleStrategyName() {
        assertEquals("Simple (Straight-line)", simpleStrategy.getStrategyName());
    }

    @Test
    @DisplayName("Route-based strategy should return correct strategy name")
    void testRouteBasedStrategyName() {
        assertEquals("Route-Based (Adaptive Speed)", routeBasedStrategy.getStrategyName());
    }

    @Test
    @DisplayName("Both strategies should produce positive ETA for long distance")
    void testBothStrategiesPositiveETA() {
        double simpleETA = simpleStrategy.calculateETA(COLOMBO_LAT, COLOMBO_LON, KANDY_LAT, KANDY_LON);
        double routeETA = routeBasedStrategy.calculateETA(COLOMBO_LAT, COLOMBO_LON, KANDY_LAT, KANDY_LON);

        assertTrue(simpleETA > 0, "Simple ETA should be positive");
        assertTrue(routeETA > 0, "Route-based ETA should be positive");
    }

    @Test
    @DisplayName("Route-based strategy should use highway speed for long distances (lower ETA)")
    void testRouteBasedUsesHighwaySpeedForLongDistance() {
        double simpleETA = simpleStrategy.calculateETA(COLOMBO_LAT, COLOMBO_LON, KANDY_LAT, KANDY_LON);
        double routeETA = routeBasedStrategy.calculateETA(COLOMBO_LAT, COLOMBO_LON, KANDY_LAT, KANDY_LON);

        // For long distances, route-based uses highway speed (40 km/h) vs simple (25 km/h)
        // So route-based ETA should be less than simple ETA
        assertTrue(routeETA < simpleETA,
                "Route-based ETA (" + routeETA + " min) should be less than Simple ETA ("
                        + simpleETA + " min) for long distances");
    }

    @Test
    @DisplayName("Route-based strategy should use urban speed for short distances (higher ETA)")
    void testRouteBasedUsesUrbanSpeedForShortDistance() {
        // Very close distance: Colombo Fort to Pettah (~0.8km straight line)
        // With road factor both strategies stay under 5km
        double pettahLat = 6.9358;
        double pettahLon = 79.8500;
        double simpleETA = simpleStrategy.calculateETA(COLOMBO_LAT, COLOMBO_LON, pettahLat, pettahLon);
        double routeETA = routeBasedStrategy.calculateETA(COLOMBO_LAT, COLOMBO_LON, pettahLat, pettahLon);

        // For very short distances (<5km road), route-based uses urban speed (15 km/h)
        // vs simple (25 km/h), so route-based ETA should be higher
        assertTrue(routeETA > simpleETA,
                "Route-based ETA (" + routeETA + " min) should be more than Simple ETA ("
                        + simpleETA + " min) for short urban distances");
    }

    @Test
    @DisplayName("Both strategies should return 0 ETA for same location")
    void testSameLocationZeroETA() {
        double simpleETA = simpleStrategy.calculateETA(COLOMBO_LAT, COLOMBO_LON, COLOMBO_LAT, COLOMBO_LON);
        double routeETA = routeBasedStrategy.calculateETA(COLOMBO_LAT, COLOMBO_LON, COLOMBO_LAT, COLOMBO_LON);

        assertEquals(0.0, simpleETA, 0.01);
        assertEquals(0.0, routeETA, 0.01);
    }

    @Test
    @DisplayName("Route-based distance should be greater than simple (road factor)")
    void testRouteBasedDistanceIncludesRoadFactor() {
        double simpleDistance = simpleStrategy.calculateDistance(
                COLOMBO_LAT, COLOMBO_LON, KANDY_LAT, KANDY_LON);
        double routeDistance = routeBasedStrategy.calculateDistance(
                COLOMBO_LAT, COLOMBO_LON, KANDY_LAT, KANDY_LON);

        // Route-based uses road factor 1.25 vs simple's straight-line
        assertTrue(routeDistance > simpleDistance,
                "Route-based distance should be greater due to road factor");
    }
}
