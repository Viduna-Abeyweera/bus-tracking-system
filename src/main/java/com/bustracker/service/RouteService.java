package com.bustracker.service;

import com.bustracker.dto.request.RouteRequest;
import com.bustracker.dto.response.RouteResponse;

import java.util.List;

/**
 * Service interface for route management operations.
 */
public interface RouteService {

    RouteResponse createRoute(RouteRequest request);

    RouteResponse getRouteById(Long id);

    RouteResponse getRouteByNumber(String routeNumber);

    List<RouteResponse> getAllRoutes();

    List<RouteResponse> getActiveRoutes();

    List<RouteResponse> searchRoutes(String query);

    RouteResponse updateRoute(Long id, RouteRequest request);

    void deleteRoute(Long id);
}
