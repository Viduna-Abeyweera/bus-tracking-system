package com.bustracker.controller;

import com.bustracker.dto.request.BusRequest;
import com.bustracker.dto.response.BusResponse;
import com.bustracker.service.BusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for bus management.
 */
@RestController
@RequestMapping("/api/buses")
@Tag(name = "Buses", description = "Endpoints for managing physical buses")
public class BusController {

    private final BusService busService;

    public BusController(BusService busService) {
        this.busService = busService;
    }

    @GetMapping
    @Operation(summary = "Get all buses")
    public ResponseEntity<List<BusResponse>> getAllBuses() {
        return ResponseEntity.ok(busService.getAllBuses());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get bus by ID")
    public ResponseEntity<BusResponse> getBusById(@PathVariable Long id) {
        return ResponseEntity.ok(busService.getBusById(id));
    }

    @GetMapping("/route/{routeId}")
    @Operation(summary = "Get buses by route", description = "Get all buses assigned to a specific route")
    public ResponseEntity<List<BusResponse>> getBusesByRoute(@PathVariable Long routeId) {
        return ResponseEntity.ok(busService.getBusesByRoute(routeId));
    }

    @PostMapping
    @Operation(summary = "Register bus", description = "Register a new bus (ADMIN only)")
    public ResponseEntity<BusResponse> createBus(@Valid @RequestBody BusRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(busService.createBus(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update bus", description = "Update bus details (ADMIN only)")
    public ResponseEntity<BusResponse> updateBus(@PathVariable Long id,
                                                  @Valid @RequestBody BusRequest request) {
        return ResponseEntity.ok(busService.updateBus(id, request));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update bus status", description = "Change bus status: ACTIVE, IDLE, MAINTENANCE")
    public ResponseEntity<BusResponse> updateStatus(@PathVariable Long id,
                                                     @RequestParam String status) {
        return ResponseEntity.ok(busService.updateBusStatus(id, status));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete bus", description = "Remove a bus (ADMIN only)")
    public ResponseEntity<Void> deleteBus(@PathVariable Long id) {
        busService.deleteBus(id);
        return ResponseEntity.noContent().build();
    }
}
