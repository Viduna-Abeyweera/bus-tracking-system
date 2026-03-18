package com.bustracker.controller;

import com.bustracker.entity.BusLocation;
import com.bustracker.service.BusLocationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bus-locations")
public class BusLocationController {

    private final BusLocationService service;

    public BusLocationController(BusLocationService service) {
        this.service = service;
    }

    // POST → Save a new bus location
    // URL: POST http://localhost:8080/api/bus-locations
    @PostMapping
    public ResponseEntity<BusLocation> addLocation(
            @RequestBody BusLocation location) {
        BusLocation saved = service.saveLocation(location);
        return ResponseEntity.ok(saved);
    }

    // GET → Get all bus locations
    // URL: GET http://localhost:8080/api/bus-locations
    @GetMapping
    public ResponseEntity<List<BusLocation>> getAllLocations() {
        List<BusLocation> locations = service.getAllLocations();
        return ResponseEntity.ok(locations);
    }

    // GET → Get locations for a specific bus
    // URL: GET http://localhost:8080/api/bus-locations/BUS-001
    @GetMapping("/{busId}")
    public ResponseEntity<List<BusLocation>> getLocationsByBusId(
            @PathVariable String busId) {
        List<BusLocation> locations = service.getLocationsByBusId(busId);
        return ResponseEntity.ok(locations);
    }
}