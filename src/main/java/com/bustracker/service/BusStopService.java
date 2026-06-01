package com.bustracker.service;

import com.bustracker.dto.request.BusStopRequest;
import com.bustracker.dto.response.BusStopResponse;

import java.util.List;

/**
 * Service interface for bus stop management operations.
 *
 * <p>This interface did not exist in the original codebase — the
 * {@code BusStopController} was directly calling {@code BusStopRepository},
 * violating the <strong>Single Responsibility Principle (SRP)</strong> and
 * <strong>Dependency Inversion Principle (DIP)</strong>.</p>
 *
 * <p>Now the controller depends on this abstraction, and the repository
 * access is encapsulated within the service implementation.</p>
 */
public interface BusStopService {

    /**
     * Retrieves all bus stops.
     *
     * @return list of all bus stop response DTOs
     */
    List<BusStopResponse> getAllStops();

    /**
     * Retrieves a single bus stop by its ID.
     *
     * @param id the bus stop ID
     * @return the bus stop response DTO
     * @throws com.bustracker.exception.ResourceNotFoundException if not found
     */
    BusStopResponse getStopById(Long id);

    /**
     * Creates a new bus stop.
     *
     * @param request the validated bus stop request DTO
     * @return the created bus stop as a response DTO
     */
    BusStopResponse createStop(BusStopRequest request);

    /**
     * Updates an existing bus stop.
     *
     * @param id      the ID of the bus stop to update
     * @param request the updated bus stop data
     * @return the updated bus stop as a response DTO
     * @throws com.bustracker.exception.ResourceNotFoundException if not found
     */
    BusStopResponse updateStop(Long id, BusStopRequest request);

    /**
     * Deletes a bus stop by its ID.
     *
     * @param id the ID of the bus stop to delete
     * @throws com.bustracker.exception.ResourceNotFoundException if not found
     */
    void deleteStop(Long id);
}
