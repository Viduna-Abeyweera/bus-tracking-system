package com.bustracker.controller;

import com.bustracker.dto.request.BusLocationRequest;
import com.bustracker.dto.response.BusLocationResponse;
import com.bustracker.service.BusLocationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for bus location operations.
 *
 * <p>This controller is intentionally <strong>thin</strong>: it handles
 * HTTP concerns (request mapping, validation, status codes) and delegates
 * all business logic to the {@link BusLocationService} interface.</p>
 *
 * <p>Key SOLID principles demonstrated:</p>
 * <ul>
 *   <li><strong>SRP</strong>: Only handles HTTP request/response mapping</li>
 *   <li><strong>DIP</strong>: Depends on {@code BusLocationService} interface,
 *       not the concrete implementation</li>
 *   <li><strong>Encapsulation</strong>: Accepts request DTOs, returns response
 *       DTOs — entities are never exposed to the API</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/bus-locations")
@Tag(name = "Bus Locations", description = "Endpoints for managing real-time bus GPS locations")
public class BusLocationController {

    private final BusLocationService busLocationService;

    /**
     * Constructor injection with interface type — demonstrates DIP.
     * Spring auto-wires the concrete implementation (BusLocationServiceImpl).
     */
    public BusLocationController(BusLocationService busLocationService) {
        this.busLocationService = busLocationService;
    }

    /**
     * Saves a new bus location update from a driver.
     * Returns HTTP 201 (Created) on success.
     */
    @PostMapping
    @Operation(summary = "Submit bus location", description = "Drivers call this endpoint to share their current GPS location")
    public ResponseEntity<BusLocationResponse> addLocation(
            @Valid @RequestBody BusLocationRequest request) {
        BusLocationResponse saved = busLocationService.saveLocation(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    /**
     * Retrieves all bus location records.
     */
    @GetMapping
    @Operation(summary = "Get all locations", description = "Retrieve the complete history of all bus locations")
    public ResponseEntity<List<BusLocationResponse>> getAllLocations() {
        List<BusLocationResponse> locations = busLocationService.getAllLocations();
        return ResponseEntity.ok(locations);
    }

    /**
     * Retrieves all location records for a specific bus.
     */
    @GetMapping("/{busId}")
    @Operation(summary = "Get locations by bus ID", description = "Retrieve all location history for a specific bus")
    public ResponseEntity<List<BusLocationResponse>> getLocationsByBusId(
            @PathVariable String busId) {
        List<BusLocationResponse> locations = busLocationService.getLocationsByBusId(busId);
        return ResponseEntity.ok(locations);
    }

    /**
     * Retrieves only the latest location for each active bus.
     * This is the primary endpoint used by the passenger map view.
     */
    @GetMapping("/latest")
    @Operation(summary = "Get latest locations", description = "Get the most recent location for each active bus (used by passenger map)")
    public ResponseEntity<List<BusLocationResponse>> getLatestLocations() {
        List<BusLocationResponse> locations = busLocationService.getLatestLocations();
        return ResponseEntity.ok(locations);
    }
}