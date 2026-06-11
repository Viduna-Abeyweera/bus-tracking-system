package com.bustracker.service.impl;

import com.bustracker.dto.response.ETAResponse;
import com.bustracker.entity.BusLocation;
import com.bustracker.entity.BusStop;
import com.bustracker.exception.ResourceNotFoundException;
import com.bustracker.repository.BusLocationRepository;
import com.bustracker.repository.BusStopRepository;
import com.bustracker.service.ETAService;
import com.bustracker.strategy.ETACalculationStrategy;
import com.bustracker.strategy.RouteBasedETAStrategy;
import com.bustracker.strategy.SimpleETAStrategy;
import com.bustracker.util.GeoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Implementation of {@link ETAService} using the <strong>Strategy Pattern</strong>.
 *
 * <p>This class was refactored from Phase 1 to use pluggable ETA strategies
 * instead of hardcoded speed calculations. The strategy can be switched
 * at runtime based on the context (e.g., route availability).</p>
 *
 * <p>Design patterns demonstrated:</p>
 * <ul>
 *   <li><strong>Strategy Pattern</strong>: Different ETA algorithms via
 *       {@link ETACalculationStrategy} interface</li>
 *   <li><strong>DIP</strong>: Depends on strategy interface, not concrete classes</li>
 *   <li><strong>OCP</strong>: New strategies can be added without modifying this class</li>
 * </ul>
 */
@Service
public class ETAServiceImpl implements ETAService {

    private static final Logger logger = LoggerFactory.getLogger(ETAServiceImpl.class);

    private final BusLocationRepository busLocationRepository;
    private final BusStopRepository busStopRepository;
    private final SimpleETAStrategy simpleStrategy;
    private final RouteBasedETAStrategy routeBasedStrategy;

    /** The currently active strategy — defaults to route-based */
    private ETACalculationStrategy activeStrategy;

    public ETAServiceImpl(BusLocationRepository busLocationRepository,
                          BusStopRepository busStopRepository,
                          SimpleETAStrategy simpleStrategy,
                          RouteBasedETAStrategy routeBasedStrategy) {
        this.busLocationRepository = busLocationRepository;
        this.busStopRepository = busStopRepository;
        this.simpleStrategy = simpleStrategy;
        this.routeBasedStrategy = routeBasedStrategy;
        this.activeStrategy = routeBasedStrategy; // Default strategy
    }

    /**
     * Allows runtime switching of the ETA calculation strategy.
     *
     * @param strategyName "simple" or "route-based"
     */
    public void setStrategy(String strategyName) {
        if ("simple".equalsIgnoreCase(strategyName)) {
            this.activeStrategy = simpleStrategy;
        } else {
            this.activeStrategy = routeBasedStrategy;
        }
        logger.info("ETA strategy switched to: {}", activeStrategy.getStrategyName());
    }

    @Override
    public double calculateDistance(double lat1, double lon1,
                                    double lat2, double lon2) {
        return activeStrategy.calculateDistance(lat1, lon1, lat2, lon2);
    }

    @Override
    public double calculateETAMinutes(double busLat, double busLon,
                                       double stopLat, double stopLon) {
        return activeStrategy.calculateETA(busLat, busLon, stopLat, stopLon);
    }

    @Override
    public List<ETAResponse> calculateETAForStop(Long stopId) {
        // Find the bus stop or throw a proper exception
        BusStop stop = busStopRepository.findById(stopId)
                .orElseThrow(() -> new ResourceNotFoundException("BusStop", "id", stopId));

        // Get latest location for each active bus
        List<BusLocation> latestLocations = busLocationRepository.findLatestLocationForEachBus();

        // Calculate ETA for each bus using the active strategy
        List<ETAResponse> etaList = new ArrayList<>();

        for (BusLocation bus : latestLocations) {
            double distance = activeStrategy.calculateDistance(
                    bus.getLatitude(), bus.getLongitude(),
                    stop.getLatitude(), stop.getLongitude()
            );

            double etaMinutes = activeStrategy.calculateETA(
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

        logger.info("Calculated ETA for {} buses to stop '{}' using strategy '{}'",
                etaList.size(), stop.getName(), activeStrategy.getStrategyName());

        return etaList;
    }
}
