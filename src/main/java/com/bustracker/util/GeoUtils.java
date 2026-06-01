package com.bustracker.util;

/**
 * Utility class for geographical calculations.
 * Provides methods for distance computation using the Haversine formula.
 *
 * <p>This class follows the Single Responsibility Principle (SRP) by
 * encapsulating only geography-related math operations, separate from
 * any business logic.</p>
 */
public final class GeoUtils {

    /** Earth's mean radius in kilometers. */
    private static final double EARTH_RADIUS_KM = 6371.0;

    /** Private constructor to prevent instantiation of this utility class. */
    private GeoUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Calculates the great-circle distance between two GPS coordinates
     * using the Haversine formula.
     *
     * @param lat1 latitude of point 1 in degrees
     * @param lon1 longitude of point 1 in degrees
     * @param lat2 latitude of point 2 in degrees
     * @param lon2 longitude of point 2 in degrees
     * @return distance in kilometers
     */
    public static double calculateDistance(double lat1, double lon1,
                                           double lat2, double lon2) {
        double lat1Rad = Math.toRadians(lat1);
        double lat2Rad = Math.toRadians(lat2);
        double deltaLat = Math.toRadians(lat2 - lat1);
        double deltaLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2)
                + Math.cos(lat1Rad) * Math.cos(lat2Rad)
                * Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }

    /**
     * Rounds a double value to a specified number of decimal places.
     *
     * @param value  the value to round
     * @param places the number of decimal places
     * @return the rounded value
     */
    public static double roundToDecimalPlaces(double value, int places) {
        double factor = Math.pow(10, places);
        return Math.round(value * factor) / factor;
    }
}
