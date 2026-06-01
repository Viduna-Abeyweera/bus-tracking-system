package com.bustracker.service;

import com.bustracker.dto.request.BusLocationRequest;
import com.bustracker.dto.response.BusLocationResponse;

import java.util.List;

/**
 * Service interface for bus location operations.
 *
 * <p>This interface is a key demonstration of the <strong>Dependency Inversion
 * Principle (DIP)</strong>: controllers depend on this abstraction rather than
 * a concrete service class. This means the implementation can be swapped
 * (e.g., for testing with a mock or switching to a different data source)
 * without changing any controller code.</p>
 *
 * <p>It also follows the <strong>Interface Segregation Principle (ISP)</strong>
 * by exposing only bus-location-related methods. ETA calculations and
 * cleanup operations have their own separate interfaces.</p>
 */
public interface BusLocationService {

    /**
     * Saves a new bus location update.
     *
     * @param request the validated location request DTO
     * @return the saved location as a response DTO
     */
    BusLocationResponse saveLocation(BusLocationRequest request);

    /**
     * Retrieves all bus location records.
     *
     * @return list of all bus location response DTOs
     */
    List<BusLocationResponse> getAllLocations();

    /**
     * Retrieves all location records for a specific bus.
     *
     * @param busId the unique identifier of the bus
     * @return list of location response DTOs for the given bus
     */
    List<BusLocationResponse> getLocationsByBusId(String busId);

    /**
     * Retrieves only the most recent location for each active bus.
     *
     * @return list of the latest location response DTOs (one per bus)
     */
    List<BusLocationResponse> getLatestLocations();
}