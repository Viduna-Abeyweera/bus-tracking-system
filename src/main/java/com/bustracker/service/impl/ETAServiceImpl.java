package com.bustracker.service.impl;

import com.bustracker.dto.response.ETAResponse;
import com.bustracker.entity.BusLocation;
import com.bustracker.entity.BusStop;
import com.bustracker.exception.ResourceNotFoundException;
import com.bustracker.repository.BusLocationRepository;
import com.bustracker.repository.BusStopRepository;
import com.bustracker.service.ETAService;
import com.bustracker.util.GeoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Implementation of {@link ETAService} using straight-line distance
 * calculation via the Haversine formula.
 *
 * <p>This class demonstrates several design principles:</p>
 * <ul>
 *   <li><strong>SRP</strong>: Only handles ETA calculation logic. The
 *       Haversine formula itself is delegated to {@link GeoUtils}.</li>
 *   <li><strong>OCP</strong>: A new implementation (e.g., route-based ETA)
 *       can be created by implementing {@code ETAService} without modifying
 *       this class. Spring's {@code @Primary} or {@code @Qualifier} can
 *       then select the desired strategy.</li>
 *   <li><strong>DIP</strong>: Controllers depend on the {@code ETAService}
 *       interface, not this concrete class.</li>
 * </ul>
 */
@Service
public class ETAServiceImpl implements ETAService {

    private static final Logger logger = LoggerFactory.getLogger(ETAServiceImpl.class);

    /** Average bus speed assumption in km/h for ETA estimation. */
    private static final double AVERAGE_SPEED_KMH = 25.0;

    private final BusLocationRepository busLocationRepository;
    private final BusStopRepository busStopRepository;

    public ETAServiceImpl(BusLocationRepository busLocationRepository,
                          BusStopRepository busStopRepository) {
        this.busLocationRepository = busLocationRepository;
        this.busStopRepository = busStopRepository;
    }

    @Override
    public double calculateDistance(double lat1, double lon1,
                                    double lat2, double lon2) {
        return GeoUtils.calculateDistance(lat1, lon1, lat2, lon2);
    }

    @Override
    public double calculateETAMinutes(double busLat, double busLon,
                                       double stopLat, double stopLon) {
        double distanceKm = GeoUtils.calculateDistance(busLat, busLon, stopLat, stopLon);
        double timeHours = distanceKm / AVERAGE_SPEED_KMH;
        return timeHours * 60;
    }

    @Override
    public List<ETAResponse> calculateETAForStop(Long stopId) {
        // Find the bus stop or throw a proper exception
        BusStop stop = busStopRepository.findById(stopId)
                .orElseThrow(() -> new ResourceNotFoundException("BusStop", "id", stopId));

        // Get latest location for each active bus
        List<BusLocation> latestLocations = busLocationRepository.findLatestLocationForEachBus();

        // Calculate ETA for each bus
        List<ETAResponse> etaList = new ArrayList<>();

        for (BusLocation bus : latestLocations) {
            double distance = GeoUtils.calculateDistance(
                    bus.getLatitude(), bus.getLongitude(),
                    stop.getLatitude(), stop.getLongitude()
            );

            double etaMinutes = calculateETAMinutes(
                    bus.getLatitude(), bus.getLongitude(),
                    stop.getLatitude(), stop.getLongitude()
            );

            ETAResponse eta = new ETAResponse(
                    bus.getBusId(),
                    stop.getName(),
                    GeoUtils.roundToDecimalPlaces(distance, 2),
                    (int) Math.ceil(etaMinutes),
                    bus.getLatitude(),
                    bus.getLongitude(),
                    stop.getLatitude(),
                    stop.getLongitude()
            );

            etaList.add(eta);
        }

        // Sort by ETA (nearest bus first)
        etaList.sort(Comparator.comparingInt(ETAResponse::getEtaMinutes));

        logger.info("Calculated ETA for {} buses to stop '{}'",
                etaList.size(), stop.getName());

        return etaList;
    }
}
