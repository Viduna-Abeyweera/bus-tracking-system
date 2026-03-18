package com.bustracker.service;

import com.bustracker.entity.BusLocation;
import com.bustracker.repository.BusLocationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BusLocationService {

    private final BusLocationRepository repository;

    // Constructor Injection (Spring automatically provides the repository)
    public BusLocationService(BusLocationRepository repository) {
        this.repository = repository;
    }

    // Save a new bus location
    public BusLocation saveLocation(BusLocation location) {
        location.setTimestamp(LocalDateTime.now());
        return repository.save(location);
    }

    // Get all bus locations
    public List<BusLocation> getAllLocations() {
        return repository.findAll();
    }

    // Get locations for a specific bus
    public List<BusLocation> getLocationsByBusId(String busId) {
        return repository.findByBusId(busId);
    }
}