package com.bustracker.service;

import com.bustracker.dto.request.BusRequest;
import com.bustracker.dto.response.BusResponse;

import java.util.List;

/**
 * Service interface for bus management operations.
 */
public interface BusService {

    BusResponse createBus(BusRequest request);

    BusResponse getBusById(Long id);

    List<BusResponse> getAllBuses();

    List<BusResponse> getBusesByRoute(Long routeId);

    BusResponse updateBus(Long id, BusRequest request);

    BusResponse updateBusStatus(Long id, String status);

    void deleteBus(Long id);
}
