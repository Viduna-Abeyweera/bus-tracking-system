package com.bustracker.service.impl;

import com.bustracker.dto.request.BusLocationRequest;
import com.bustracker.dto.response.BusLocationResponse;
import com.bustracker.dto.websocket.LocationUpdateMessage;
import com.bustracker.entity.Bus;
import com.bustracker.entity.BusLocation;
import com.bustracker.mapper.BusLocationMapper;
import com.bustracker.repository.BusLocationRepository;
import com.bustracker.repository.BusRepository;
import com.bustracker.service.BusLocationService;
import com.bustracker.service.WebSocketBroadcastService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementation of {@link BusLocationService}.
 *
 * <p>This class demonstrates several SOLID and OOP principles:</p>
 * <ul>
 *   <li><strong>SRP</strong>: Only handles bus location persistence logic.
 *       ETA calculations are delegated to {@code ETAService}, and data
 *       transformation to {@code BusLocationMapper}.</li>
 *   <li><strong>DIP</strong>: Implements the {@code BusLocationService}
 *       interface, allowing controllers to depend on the abstraction.</li>
 *   <li><strong>Encapsulation</strong>: Internal entity objects are never
 *       exposed outside this layer — only DTOs are returned.</li>
 * </ul>
 */
@Service
public class BusLocationServiceImpl implements BusLocationService {

    private static final Logger logger = LoggerFactory.getLogger(BusLocationServiceImpl.class);

    private final BusLocationRepository repository;
    private final BusRepository busRepository;
    private final WebSocketBroadcastService broadcastService;

    /**
     * Constructor injection — Spring automatically provides dependencies.
     * Preferred over field injection for testability and immutability.
     */
    public BusLocationServiceImpl(BusLocationRepository repository,
                                  BusRepository busRepository,
                                  WebSocketBroadcastService broadcastService) {
        this.repository = repository;
        this.busRepository = busRepository;
        this.broadcastService = broadcastService;
    }

    @Override
    public BusLocationResponse saveLocation(BusLocationRequest request) {
        BusLocation entity = BusLocationMapper.toEntity(request);
        entity.setTimestamp(LocalDateTime.now());

        BusLocation saved = repository.save(entity);
        logger.info("Saved location for bus '{}' at ({}, {})",
                saved.getBusId(), saved.getLatitude(), saved.getLongitude());

        // Broadcast via WebSocket to all connected passengers
        broadcastLocationViaWebSocket(saved);

        return BusLocationMapper.toResponse(saved);
    }

    /**
     * Broadcasts a bus location update via WebSocket.
     * Looks up bus metadata (registration, route) for the broadcast message.
     */
    private void broadcastLocationViaWebSocket(BusLocation location) {
        try {
            LocationUpdateMessage message = new LocationUpdateMessage();
            message.setBusId(location.getBusId());
            message.setLatitude(location.getLatitude());
            message.setLongitude(location.getLongitude());
            message.setTimestamp(location.getTimestamp().toString());

            // Enrich with bus metadata if available
            busRepository.findByRegistrationNumber(location.getBusId())
                    .ifPresent(bus -> {
                        message.setRegistrationNumber(bus.getRegistrationNumber());
                        if (bus.getRoute() != null) {
                            message.setRouteId(bus.getRoute().getId());
                            message.setRouteNumber(bus.getRoute().getRouteNumber());
                        }
                    });

            broadcastService.broadcastLocationUpdate(message);
        } catch (Exception e) {
            // Don't let WebSocket failures affect the REST response
            logger.warn("Failed to broadcast location via WebSocket: {}", e.getMessage());
        }
    }

    @Override
    public List<BusLocationResponse> getAllLocations() {
        List<BusLocation> entities = repository.findAll();
        return BusLocationMapper.toResponseList(entities);
    }

    @Override
    public List<BusLocationResponse> getLocationsByBusId(String busId) {
        List<BusLocation> entities = repository.findByBusId(busId);
        return BusLocationMapper.toResponseList(entities);
    }

    @Override
    public List<BusLocationResponse> getLatestLocations() {
        List<BusLocation> entities = repository.findLatestLocationForEachBus();
        return BusLocationMapper.toResponseList(entities);
    }
}
