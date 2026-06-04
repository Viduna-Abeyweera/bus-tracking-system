package com.bustracker.service.impl;

import com.bustracker.dto.request.BusRequest;
import com.bustracker.dto.response.BusResponse;
import com.bustracker.entity.Bus;
import com.bustracker.entity.Route;
import com.bustracker.entity.User;
import com.bustracker.enums.BusStatus;
import com.bustracker.exception.BadRequestException;
import com.bustracker.exception.ResourceNotFoundException;
import com.bustracker.repository.BusRepository;
import com.bustracker.repository.RouteRepository;
import com.bustracker.repository.UserRepository;
import com.bustracker.service.BusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of {@link BusService}.
 *
 * <p>Manages bus entities including registration, route/driver assignment,
 * and status updates. Validates relationships (route and driver must exist)
 * before persisting.</p>
 */
@Service
public class BusServiceImpl implements BusService {

    private static final Logger logger = LoggerFactory.getLogger(BusServiceImpl.class);

    private final BusRepository busRepository;
    private final RouteRepository routeRepository;
    private final UserRepository userRepository;

    public BusServiceImpl(BusRepository busRepository, RouteRepository routeRepository,
                          UserRepository userRepository) {
        this.busRepository = busRepository;
        this.routeRepository = routeRepository;
        this.userRepository = userRepository;
    }

    @Override
    public BusResponse createBus(BusRequest request) {
        if (busRepository.existsByRegistrationNumber(request.getRegistrationNumber())) {
            throw new BadRequestException(
                    "Bus with registration '" + request.getRegistrationNumber() + "' already exists");
        }

        Route route = routeRepository.findById(request.getRouteId())
                .orElseThrow(() -> new ResourceNotFoundException("Route", "id", request.getRouteId()));

        Bus bus = new Bus(request.getRegistrationNumber(), route, request.getCapacity());

        if (request.getDriverId() != null) {
            User driver = userRepository.findById(request.getDriverId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getDriverId()));
            bus.setDriver(driver);
        }

        Bus saved = busRepository.save(bus);
        logger.info("Created bus '{}' on route '{}'",
                saved.getRegistrationNumber(), route.getRouteNumber());
        return toResponse(saved);
    }

    @Override
    public BusResponse getBusById(Long id) {
        Bus bus = busRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bus", "id", id));
        return toResponse(bus);
    }

    @Override
    public List<BusResponse> getAllBuses() {
        return busRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<BusResponse> getBusesByRoute(Long routeId) {
        return busRepository.findByRouteId(routeId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public BusResponse updateBus(Long id, BusRequest request) {
        Bus bus = busRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bus", "id", id));

        Route route = routeRepository.findById(request.getRouteId())
                .orElseThrow(() -> new ResourceNotFoundException("Route", "id", request.getRouteId()));

        bus.setRegistrationNumber(request.getRegistrationNumber());
        bus.setRoute(route);
        bus.setCapacity(request.getCapacity());

        if (request.getDriverId() != null) {
            User driver = userRepository.findById(request.getDriverId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getDriverId()));
            bus.setDriver(driver);
        } else {
            bus.setDriver(null);
        }

        Bus updated = busRepository.save(bus);
        logger.info("Updated bus '{}' (ID: {})", updated.getRegistrationNumber(), updated.getId());
        return toResponse(updated);
    }

    @Override
    public BusResponse updateBusStatus(Long id, String status) {
        Bus bus = busRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bus", "id", id));

        try {
            BusStatus newStatus = BusStatus.valueOf(status.toUpperCase());
            bus.setStatus(newStatus);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(
                    "Invalid status: '" + status + "'. Valid values: ACTIVE, IDLE, MAINTENANCE");
        }

        Bus updated = busRepository.save(bus);
        logger.info("Bus '{}' status changed to '{}'",
                updated.getRegistrationNumber(), updated.getStatus());
        return toResponse(updated);
    }

    @Override
    public void deleteBus(Long id) {
        Bus bus = busRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bus", "id", id));
        busRepository.delete(bus);
        logger.info("Deleted bus '{}' (ID: {})", bus.getRegistrationNumber(), bus.getId());
    }

    // ===== PRIVATE MAPPER =====

    private BusResponse toResponse(Bus bus) {
        return new BusResponse(
                bus.getId(),
                bus.getRegistrationNumber(),
                bus.getRoute() != null ? bus.getRoute().getRouteNumber() : null,
                bus.getRoute() != null ? bus.getRoute().getName() : null,
                bus.getRoute() != null ? bus.getRoute().getId() : null,
                bus.getDriver() != null ? bus.getDriver().getName() : null,
                bus.getDriver() != null ? bus.getDriver().getId() : null,
                bus.getStatus().name(),
                bus.getCapacity()
        );
    }
}
