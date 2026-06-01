package com.bustracker.service.impl;

import com.bustracker.dto.request.BusLocationRequest;
import com.bustracker.dto.response.BusLocationResponse;
import com.bustracker.entity.BusLocation;
import com.bustracker.mapper.BusLocationMapper;
import com.bustracker.repository.BusLocationRepository;
import com.bustracker.service.BusLocationService;
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

    /**
     * Constructor injection — Spring automatically provides the repository.
     * Preferred over field injection for testability and immutability.
     *
     * @param repository the bus location JPA repository
     */
    public BusLocationServiceImpl(BusLocationRepository repository) {
        this.repository = repository;
    }

    @Override
    public BusLocationResponse saveLocation(BusLocationRequest request) {
        BusLocation entity = BusLocationMapper.toEntity(request);
        entity.setTimestamp(LocalDateTime.now());

        BusLocation saved = repository.save(entity);
        logger.info("Saved location for bus '{}' at ({}, {})",
                saved.getBusId(), saved.getLatitude(), saved.getLongitude());

        return BusLocationMapper.toResponse(saved);
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
