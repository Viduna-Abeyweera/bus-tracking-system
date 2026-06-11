package com.bustracker.service.impl;

import com.bustracker.dto.response.ETAResponse;
import com.bustracker.entity.BusLocation;
import com.bustracker.entity.BusStop;
import com.bustracker.exception.ResourceNotFoundException;
import com.bustracker.repository.BusLocationRepository;
import com.bustracker.repository.BusStopRepository;
import com.bustracker.strategy.RouteBasedETAStrategy;
import com.bustracker.strategy.SimpleETAStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link ETAServiceImpl} with Strategy Pattern.
 */
@ExtendWith(MockitoExtension.class)
class ETAServiceImplTest {

    @Mock
    private BusLocationRepository busLocationRepository;

    @Mock
    private BusStopRepository busStopRepository;

    private ETAServiceImpl service;

    private BusStop colomboFort;

    @BeforeEach
    void setUp() {
        SimpleETAStrategy simpleStrategy = new SimpleETAStrategy();
        RouteBasedETAStrategy routeBasedStrategy = new RouteBasedETAStrategy();

        service = new ETAServiceImpl(
                busLocationRepository, busStopRepository, simpleStrategy, routeBasedStrategy);

        colomboFort = new BusStop("Colombo Fort", 6.9344, 79.8428);
        colomboFort.setId(1L);
    }

    @Test
    @DisplayName("Should calculate ETA for all buses to a stop and sort by nearest")
    void testCalculateETAForStop() {
        BusLocation nearBus = new BusLocation("BUS-001", 6.9400, 79.8500, LocalDateTime.now());
        nearBus.setId(1L);
        BusLocation farBus = new BusLocation("BUS-002", 7.2906, 80.6337, LocalDateTime.now());
        farBus.setId(2L);

        when(busStopRepository.findById(1L)).thenReturn(Optional.of(colomboFort));
        when(busLocationRepository.findLatestLocationForEachBus())
                .thenReturn(Arrays.asList(farBus, nearBus));

        List<ETAResponse> result = service.calculateETAForStop(1L);

        assertEquals(2, result.size());
        assertEquals("BUS-001", result.get(0).getBusId());
        assertEquals("BUS-002", result.get(1).getBusId());
        assertTrue(result.get(0).getEtaMinutes() < result.get(1).getEtaMinutes());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException for non-existent stop")
    void testCalculateETAForNonExistentStop() {
        when(busStopRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.calculateETAForStop(99L));
    }

    @Test
    @DisplayName("Should return empty list when no active buses exist")
    void testCalculateETAWithNoBuses() {
        when(busStopRepository.findById(1L)).thenReturn(Optional.of(colomboFort));
        when(busLocationRepository.findLatestLocationForEachBus()).thenReturn(List.of());

        List<ETAResponse> result = service.calculateETAForStop(1L);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should allow switching ETA strategy at runtime")
    void testStrategySwitching() {
        // Default is route-based
        double etaRouteBased = service.calculateETAMinutes(6.9344, 79.8428, 7.2906, 80.6337);

        // Switch to simple
        service.setStrategy("simple");
        double etaSimple = service.calculateETAMinutes(6.9344, 79.8428, 7.2906, 80.6337);

        // Both should produce positive values but differ
        assertTrue(etaRouteBased > 0);
        assertTrue(etaSimple > 0);
        assertNotEquals(etaRouteBased, etaSimple, 0.01);
    }

    @Test
    @DisplayName("Should calculate distance using Haversine formula")
    void testCalculateDistance() {
        double distance = service.calculateDistance(6.9344, 79.8428, 7.2906, 80.6337);
        assertTrue(distance > 80 && distance < 130,
                "Colombo to Kandy should be ~90-120km (with road factor), got: " + distance);
    }
}
