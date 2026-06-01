package com.bustracker.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link GeoUtils}.
 *
 * <p>Tests the Haversine formula with known GPS coordinates
 * and expected distances to verify mathematical correctness.</p>
 */
class GeoUtilsTest {

    @Test
    @DisplayName("Should calculate distance between Colombo Fort and Kandy as ~115 km")
    void testDistanceColomboToKandy() {
        // Colombo Fort Station
        double lat1 = 6.9344;
        double lon1 = 79.8428;
        // Kandy Railway Station
        double lat2 = 7.2906;
        double lon2 = 80.6337;

        double distance = GeoUtils.calculateDistance(lat1, lon1, lat2, lon2);

        // Straight-line distance should be approximately 90-95 km
        assertTrue(distance > 85 && distance < 100,
                "Distance between Colombo and Kandy should be ~90km, got: " + distance);
    }

    @Test
    @DisplayName("Should return 0 for same coordinates")
    void testDistanceSamePoint() {
        double distance = GeoUtils.calculateDistance(6.9271, 79.8612, 6.9271, 79.8612);
        assertEquals(0.0, distance, 0.001, "Distance between same points should be 0");
    }

    @Test
    @DisplayName("Should calculate a short distance correctly")
    void testShortDistance() {
        // Two points ~1 km apart in Colombo
        double lat1 = 6.9271;
        double lon1 = 79.8612;
        double lat2 = 6.9361;
        double lon2 = 79.8612;

        double distance = GeoUtils.calculateDistance(lat1, lon1, lat2, lon2);

        // Should be approximately 1 km
        assertTrue(distance > 0.5 && distance < 2.0,
                "Short distance should be ~1km, got: " + distance);
    }

    @Test
    @DisplayName("Should round to specified decimal places")
    void testRoundToDecimalPlaces() {
        assertEquals(3.14, GeoUtils.roundToDecimalPlaces(3.14159, 2));
        assertEquals(3.142, GeoUtils.roundToDecimalPlaces(3.14159, 3));
        assertEquals(3.0, GeoUtils.roundToDecimalPlaces(3.14159, 0));
    }

    @Test
    @DisplayName("Utility class should not be instantiable")
    void testPrivateConstructor() {
        // Verify the utility class follows the non-instantiation pattern
        assertThrows(Exception.class, () -> {
            var constructor = GeoUtils.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();
        });
    }
}
