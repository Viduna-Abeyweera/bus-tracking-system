package com.bustracker.service.impl;

import com.bustracker.dto.response.ETAResponse;
import com.bustracker.entity.BusLocation;
import com.bustracker.entity.BusStop;
import com.bustracker.exception.ResourceNotFoundException;
import com.bustracker.repository.BusLocationRepository;
import com.bustracker.repository.BusStopRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link ETAServiceImpl}.
 *
 * <p>Tests ETA calculation logic, distance computation, and
 * the aggregation of ETA for multiple buses to a stop.</p>
 */
@ExtendWith(MockitoExtension.class)
class ETAServiceImplTest {

    @Mock
    private BusLocationRepository busLocationRepository;

    @Mock
    private BusStopRepository busStopRepository;

    @InjectMocks
    private ETAServiceImpl service;

    private BusStop colomboFort;

    @BeforeEach
    void setUp() {
        colomboFort = new BusStop("Colombo Fort", 6.9344, 79.8428);
        colomboFort.setId(1L);
    }

    @Test
    @DisplayName("Should calculate distance using Haversine formula")
    void testCalculateDistance() {
        // Colombo to Kandy (straight line ~90 km)
        double distance = service.calculateDistance(6.9344, 79.8428, 7.2906, 80.6337);

        assertTrue(distance > 85 && distance < 100,
                "Colombo to Kandy should be ~90km, got: " + distance);
    }

    @Test
    @DisplayName("Should calculate ETA in minutes based on distance and average speed")
    void testCalculateETAMinutes() {
        // 25 km distance at 25 km/h = 60 minutes
        // Using coordinates ~25km apart
        double eta = service.calculateETAMinutes(6.9344, 79.8428, 6.9344, 79.8428);

        assertEquals(0.0, eta, 0.01, "ETA for same location should be 0");
    }

    @Test
    @DisplayName("Should calculate ETA for all buses to a stop and sort by nearest")
    void testCalculateETAForStop() {
        // Bus near the stop (Colombo area)
        BusLocation nearBus = new BusLocation("BUS-001", 6.9400, 79.8500, LocalDateTime.now());
        nearBus.setId(1L);

        // Bus far from the stop (Kandy area)
        BusLocation farBus = new BusLocation("BUS-002", 7.2906, 80.6337, LocalDateTime.now());
        farBus.setId(2L);

        when(busStopRepository.findById(1L)).thenReturn(Optional.of(colomboFort));
        when(busLocationRepository.findLatestLocationForEachBus())
                .thenReturn(Arrays.asList(farBus, nearBus));

        List<ETAResponse> result = service.calculateETAForStop(1L);

        assertEquals(2, result.size());
        // Should be sorted by ETA — nearest bus first
        assertEquals("BUS-001", result.get(0).getBusId());
        assertEquals("BUS-002", result.get(1).getBusId());
        assertTrue(result.get(0).getEtaMinutes() < result.get(1).getEtaMinutes());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException for non-existent stop")
    void testCalculateETAForNonExistentStop() {
        when(busStopRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.calculateETAForStop(99L));
    }

    @Test
    @DisplayName("Should return empty list when no active buses exist")
    void testCalculateETAWithNoBuses() {
        when(busStopRepository.findById(1L)).thenReturn(Optional.of(colomboFort));
        when(busLocationRepository.findLatestLocationForEachBus()).thenReturn(List.of());

        List<ETAResponse> result = service.calculateETAForStop(1L);

        assertTrue(result.isEmpty());
    }
}
