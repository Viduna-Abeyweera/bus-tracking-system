package com.bustracker.controller;

import com.bustracker.dto.ETAResponse;
import com.bustracker.entity.BusLocation;
import com.bustracker.entity.BusStop;
import com.bustracker.repository.BusStopRepository;
import com.bustracker.service.BusLocationService;
import com.bustracker.service.ETAService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/bus-stops")
public class BusStopController {

    private final BusStopRepository busStopRepository;
    private final BusLocationService busLocationService;
    private final ETAService etaService;

    public BusStopController(BusStopRepository busStopRepository,
                             BusLocationService busLocationService,
                             ETAService etaService) {
        this.busStopRepository = busStopRepository;
        this.busLocationService = busLocationService;
        this.etaService = etaService;
    }

    // GET all bus stops
    @GetMapping
    public ResponseEntity<List<BusStop>> getAllStops() {
        return ResponseEntity.ok(busStopRepository.findAll());
    }

    // POST a new bus stop
    @PostMapping
    public ResponseEntity<BusStop> addStop(@RequestBody BusStop stop) {
        return ResponseEntity.ok(busStopRepository.save(stop));
    }

    // GET ETA for all buses to a specific stop
    @GetMapping("/{stopId}/eta")
    public ResponseEntity<List<ETAResponse>> getETAForStop(@PathVariable Long stopId) {

        // Find the stop
        BusStop stop = busStopRepository.findById(stopId).orElse(null);
        if (stop == null) {
            return ResponseEntity.notFound().build();
        }

        // Get latest location for each bus
        List<BusLocation> latestLocations = busLocationService.getLatestLocations();

        // Calculate ETA for each bus
        List<ETAResponse> etaList = new ArrayList<>();

        for (BusLocation bus : latestLocations) {
            double distance = etaService.calculateDistance(
                    bus.getLatitude(), bus.getLongitude(),
                    stop.getLatitude(), stop.getLongitude()
            );

            double etaMinutes = etaService.calculateETAMinutes(bus, stop);

            ETAResponse eta = new ETAResponse(
                    bus.getBusId(),
                    stop.getName(),
                    Math.round(distance * 100.0) / 100.0,
                    (int) Math.ceil(etaMinutes),
                    bus.getLatitude(),
                    bus.getLongitude(),
                    stop.getLatitude(),
                    stop.getLongitude()
            );

            etaList.add(eta);
        }

        // Sort by ETA (nearest bus first)
        etaList.sort((a, b) -> Integer.compare(a.getEtaMinutes(), b.getEtaMinutes()));

        return ResponseEntity.ok(etaList);
    }
}