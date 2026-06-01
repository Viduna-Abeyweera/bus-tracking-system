package com.bustracker.service.impl;

import com.bustracker.dto.request.BusStopRequest;
import com.bustracker.dto.response.BusStopResponse;
import com.bustracker.entity.BusStop;
import com.bustracker.exception.ResourceNotFoundException;
import com.bustracker.mapper.BusStopMapper;
import com.bustracker.repository.BusStopRepository;
import com.bustracker.service.BusStopService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation of {@link BusStopService}.
 *
 * <p>This service class did not exist in the original codebase — the
 * controller was directly accessing the repository, which violated both
 * the <strong>Single Responsibility Principle</strong> (controller had
 * persistence logic) and the <strong>Dependency Inversion Principle</strong>
 * (controller depended on a concrete repository).</p>
 *
 * <p>Now the controller depends only on the {@code BusStopService}
 * interface, and this implementation encapsulates all bus stop
 * business logic and data access.</p>
 */
@Service
public class BusStopServiceImpl implements BusStopService {

    private static final Logger logger = LoggerFactory.getLogger(BusStopServiceImpl.class);

    private final BusStopRepository repository;

    public BusStopServiceImpl(BusStopRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<BusStopResponse> getAllStops() {
        List<BusStop> entities = repository.findAll();
        return BusStopMapper.toResponseList(entities);
    }

    @Override
    public BusStopResponse getStopById(Long id) {
        BusStop entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BusStop", "id", id));
        return BusStopMapper.toResponse(entity);
    }

    @Override
    public BusStopResponse createStop(BusStopRequest request) {
        BusStop entity = BusStopMapper.toEntity(request);
        BusStop saved = repository.save(entity);

        logger.info("Created bus stop '{}' at ({}, {})",
                saved.getName(), saved.getLatitude(), saved.getLongitude());

        return BusStopMapper.toResponse(saved);
    }

    @Override
    public BusStopResponse updateStop(Long id, BusStopRequest request) {
        BusStop entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BusStop", "id", id));

        BusStopMapper.updateEntity(entity, request);
        BusStop updated = repository.save(entity);

        logger.info("Updated bus stop '{}' (ID: {})", updated.getName(), updated.getId());

        return BusStopMapper.toResponse(updated);
    }

    @Override
    public void deleteStop(Long id) {
        BusStop entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BusStop", "id", id));

        repository.delete(entity);
        logger.info("Deleted bus stop '{}' (ID: {})", entity.getName(), entity.getId());
    }
}
