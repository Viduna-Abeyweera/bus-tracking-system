package com.bustracker.controller;

import com.bustracker.dto.request.RouteRequest;
import com.bustracker.dto.response.RouteResponse;
import com.bustracker.service.RouteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for bus route management.
 *
 * <p>Provides endpoints for CRUD operations on routes and searching
 * routes by origin/destination. Write operations are restricted to
 * ADMIN role via {@code SecurityConfig}.</p>
 */
@RestController
@RequestMapping("/api/routes")
@Tag(name = "Routes", description = "Endpoints for managing bus routes")
public class RouteController {

    private final RouteService routeService;

    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    @GetMapping
    @Operation(summary = "Get all routes", description = "Retrieve all bus routes with their stops")
    public ResponseEntity<List<RouteResponse>> getAllRoutes() {
        return ResponseEntity.ok(routeService.getAllRoutes());
    }

    @GetMapping("/active")
    @Operation(summary = "Get active routes", description = "Retrieve only currently active routes")
    public ResponseEntity<List<RouteResponse>> getActiveRoutes() {
        return ResponseEntity.ok(routeService.getActiveRoutes());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get route by ID")
    public ResponseEntity<RouteResponse> getRouteById(@PathVariable Long id) {
        return ResponseEntity.ok(routeService.getRouteById(id));
    }

    @GetMapping("/number/{routeNumber}")
    @Operation(summary = "Get route by number", description = "Find a route by its Sri Lankan route number")
    public ResponseEntity<RouteResponse> getRouteByNumber(@PathVariable String routeNumber) {
        return ResponseEntity.ok(routeService.getRouteByNumber(routeNumber));
    }

    @GetMapping("/search")
    @Operation(summary = "Search routes", description = "Search routes by origin or destination name")
    public ResponseEntity<List<RouteResponse>> searchRoutes(@RequestParam String query) {
        return ResponseEntity.ok(routeService.searchRoutes(query));
    }

    @PostMapping
    @Operation(summary = "Create route", description = "Create a new bus route (ADMIN only)")
    public ResponseEntity<RouteResponse> createRoute(@Valid @RequestBody RouteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(routeService.createRoute(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update route", description = "Update an existing route (ADMIN only)")
    public ResponseEntity<RouteResponse> updateRoute(@PathVariable Long id,
                                                      @Valid @RequestBody RouteRequest request) {
        return ResponseEntity.ok(routeService.updateRoute(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete route", description = "Delete a route (ADMIN only)")
    public ResponseEntity<Void> deleteRoute(@PathVariable Long id) {
        routeService.deleteRoute(id);
        return ResponseEntity.noContent().build();
    }
}
