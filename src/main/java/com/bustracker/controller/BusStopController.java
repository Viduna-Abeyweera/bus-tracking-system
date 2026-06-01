package com.bustracker.controller;

import com.bustracker.dto.request.BusStopRequest;
import com.bustracker.dto.response.BusStopResponse;
import com.bustracker.dto.response.ETAResponse;
import com.bustracker.service.BusStopService;
import com.bustracker.service.ETAService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for bus stop management and ETA calculations.
 *
 * <p>This controller was significantly refactored from the original version:</p>
 * <ul>
 *   <li><strong>Before</strong>: Directly accessed {@code BusStopRepository},
 *       contained 40+ lines of ETA business logic, returned raw entities,
 *       used {@code orElse(null)} for error handling.</li>
 *   <li><strong>After</strong>: Delegates to {@code BusStopService} and
 *       {@code ETAService} interfaces, returns DTOs, uses {@code @Valid}
 *       for input validation, proper HTTP status codes.</li>
 * </ul>
 *
 * <p>The ETA calculation logic that was previously in this controller
 * has been moved to {@link com.bustracker.service.impl.ETAServiceImpl},
 * following the <strong>Single Responsibility Principle</strong>.</p>
 */
@RestController
@RequestMapping("/api/bus-stops")
@Tag(name = "Bus Stops", description = "Endpoints for managing bus stops and calculating ETAs")
public class BusStopController {

    private final BusStopService busStopService;
    private final ETAService etaService;

    /**
     * Constructor injection with interface types — demonstrates DIP.
     * No direct repository access in the controller.
     */
    public BusStopController(BusStopService busStopService, ETAService etaService) {
        this.busStopService = busStopService;
        this.etaService = etaService;
    }

    /**
     * Retrieves all bus stops.
     */
    @GetMapping
    @Operation(summary = "Get all bus stops", description = "Retrieve all registered bus stops")
    public ResponseEntity<List<BusStopResponse>> getAllStops() {
        return ResponseEntity.ok(busStopService.getAllStops());
    }

    /**
     * Retrieves a single bus stop by ID.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get bus stop by ID", description = "Retrieve a specific bus stop by its ID")
    public ResponseEntity<BusStopResponse> getStopById(@PathVariable Long id) {
        return ResponseEntity.ok(busStopService.getStopById(id));
    }

    /**
     * Creates a new bus stop.
     * Returns HTTP 201 (Created) on success.
     */
    @PostMapping
    @Operation(summary = "Create bus stop", description = "Register a new bus stop with GPS coordinates")
    public ResponseEntity<BusStopResponse> createStop(
            @Valid @RequestBody BusStopRequest request) {
        BusStopResponse created = busStopService.createStop(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Updates an existing bus stop.
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update bus stop", description = "Update an existing bus stop's name or coordinates")
    public ResponseEntity<BusStopResponse> updateStop(
            @PathVariable Long id,
            @Valid @RequestBody BusStopRequest request) {
        return ResponseEntity.ok(busStopService.updateStop(id, request));
    }

    /**
     * Deletes a bus stop.
     * Returns HTTP 204 (No Content) on success.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete bus stop", description = "Remove a bus stop from the system")
    public ResponseEntity<Void> deleteStop(@PathVariable Long id) {
        busStopService.deleteStop(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Calculates ETA for all active buses to a specific bus stop.
     *
     * <p>This endpoint previously contained all the ETA business logic
     * directly in the controller (40+ lines). Now it's a single line
     * delegation to {@code ETAService}, following SRP.</p>
     */
    @GetMapping("/{stopId}/eta")
    @Operation(summary = "Get ETA for stop", description = "Calculate estimated arrival time for all active buses to a specific stop")
    public ResponseEntity<List<ETAResponse>> getETAForStop(@PathVariable Long stopId) {
        return ResponseEntity.ok(etaService.calculateETAForStop(stopId));
    }
}