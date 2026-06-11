package com.bustracker.service.impl;

import com.bustracker.dto.request.RouteRequest;
import com.bustracker.dto.response.RouteResponse;
import com.bustracker.dto.response.RouteResponse.RouteStopResponse;
import com.bustracker.entity.Route;
import com.bustracker.entity.RouteStop;
import com.bustracker.exception.BadRequestException;
import com.bustracker.exception.ResourceNotFoundException;
import com.bustracker.repository.RouteRepository;
import com.bustracker.service.RouteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of {@link RouteService}.
 *
 * <p>Manages the lifecycle of bus routes including CRUD operations
 * and search. Converts entities to DTOs using private mapper methods,
 * keeping entity internals hidden from the API layer.</p>
 */
@Service
public class RouteServiceImpl implements RouteService {

    private static final Logger logger = LoggerFactory.getLogger(RouteServiceImpl.class);

    private final RouteRepository routeRepository;

    public RouteServiceImpl(RouteRepository routeRepository) {
        this.routeRepository = routeRepository;
    }

    @Override
    public RouteResponse createRoute(RouteRequest request) {
        if (routeRepository.existsByRouteNumber(request.getRouteNumber())) {
            throw new BadRequestException(
                    "Route number '" + request.getRouteNumber() + "' already exists");
        }

        Route route = new Route(
                request.getRouteNumber(),
                request.getName(),
                request.getOrigin(),
                request.getDestination(),
                request.getDistanceKm(),
                request.getEstimatedDurationMinutes()
        );

        Route saved = routeRepository.save(route);
        logger.info("Created route '{}' ({} → {})", saved.getRouteNumber(),
                saved.getOrigin(), saved.getDestination());
        return toResponse(saved);
    }

    @Override
    public RouteResponse getRouteById(Long id) {
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Route", "id", id));
        return toResponse(route);
    }

    @Override
    public RouteResponse getRouteByNumber(String routeNumber) {
        Route route = routeRepository.findByRouteNumber(routeNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Route", "routeNumber", routeNumber));
        return toResponse(route);
    }

    @Override
    public List<RouteResponse> getAllRoutes() {
        return routeRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<RouteResponse> getActiveRoutes() {
        return routeRepository.findByActiveTrue().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<RouteResponse> searchRoutes(String query) {
        return routeRepository
                .findByOriginContainingIgnoreCaseOrDestinationContainingIgnoreCase(query, query)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public RouteResponse updateRoute(Long id, RouteRequest request) {
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Route", "id", id));

        route.setRouteNumber(request.getRouteNumber());
        route.setName(request.getName());
        route.setOrigin(request.getOrigin());
        route.setDestination(request.getDestination());
        route.setDistanceKm(request.getDistanceKm());
        route.setEstimatedDurationMinutes(request.getEstimatedDurationMinutes());

        Route updated = routeRepository.save(route);
        logger.info("Updated route '{}' (ID: {})", updated.getRouteNumber(), updated.getId());
        return toResponse(updated);
    }

    @Override
    public void deleteRoute(Long id) {
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Route", "id", id));
        routeRepository.delete(route);
        logger.info("Deleted route '{}' (ID: {})", route.getRouteNumber(), route.getId());
    }

    // ===== PRIVATE MAPPER METHODS =====

    private RouteResponse toResponse(Route route) {
        List<RouteStopResponse> stopResponses = route.getRouteStops().stream()
                .map(this::toStopResponse)
                .collect(Collectors.toList());

        return new RouteResponse(
                route.getId(),
                route.getRouteNumber(),
                route.getName(),
                route.getOrigin(),
                route.getDestination(),
                route.getDistanceKm(),
                route.getEstimatedDurationMinutes(),
                route.getActive(),
                stopResponses
        );
    }

    private RouteStopResponse toStopResponse(RouteStop routeStop) {
        return new RouteStopResponse(
                routeStop.getBusStop().getId(),
                routeStop.getBusStop().getName(),
                routeStop.getBusStop().getLatitude(),
                routeStop.getBusStop().getLongitude(),
                routeStop.getSequenceOrder(),
                routeStop.getDistanceFromOriginKm(),
                routeStop.getEstimatedTimeFromOriginMinutes()
        );
    }
}
