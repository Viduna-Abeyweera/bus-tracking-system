package com.bustracker.service;

import com.bustracker.entity.BusLocation;
import com.bustracker.entity.BusStop;
import org.springframework.stereotype.Service;

@Service
public class ETAService {

    // Average bus speed in km/h
    private static final double AVERAGE_SPEED_KMH = 25.0;

    // Earth's radius in kilometers
    private static final double EARTH_RADIUS_KM = 6371.0;

    /**
     * Calculate distance between two GPS points using Haversine formula
     * Returns distance in kilometers
     */
    public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {

        // Convert degrees to radians
        double lat1Rad = Math.toRadians(lat1);
        double lat2Rad = Math.toRadians(lat2);
        double deltaLat = Math.toRadians(lat2 - lat1);
        double deltaLon = Math.toRadians(lon2 - lon1);

        // Haversine formula
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2)
                + Math.cos(lat1Rad) * Math.cos(lat2Rad)
                * Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }

    /**
     * Calculate ETA in minutes from a bus to a stop
     */
    public double calculateETAMinutes(BusLocation bus, BusStop stop) {
        double distanceKm = calculateDistance(
                bus.getLatitude(), bus.getLongitude(),
                stop.getLatitude(), stop.getLongitude()
        );

        // time = distance / speed (in hours), then convert to minutes
        double timeHours = distanceKm / AVERAGE_SPEED_KMH;
        return timeHours * 60;
    }
}