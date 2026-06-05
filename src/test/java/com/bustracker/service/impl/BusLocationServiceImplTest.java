package com.bustracker.service.impl;

import com.bustracker.dto.request.BusLocationRequest;
import com.bustracker.dto.response.BusLocationResponse;
import com.bustracker.entity.BusLocation;
import com.bustracker.repository.BusLocationRepository;
import com.bustracker.repository.BusRepository;
import com.bustracker.service.WebSocketBroadcastService;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link BusLocationServiceImpl}.
 *
 * <p>Uses Mockito to mock the repository layer, testing only the
 * service logic in isolation. This demonstrates proper unit testing
 * where dependencies are mocked.</p>
 */
@ExtendWith(MockitoExtension.class)
class BusLocationServiceImplTest {

    @Mock
    private BusLocationRepository repository;

    @Mock
    private BusRepository busRepository;

    @Mock
    private WebSocketBroadcastService broadcastService;

    @InjectMocks
    private BusLocationServiceImpl service;

    private BusLocation sampleEntity;
    private BusLocationRequest sampleRequest;

    @BeforeEach
    void setUp() {
        sampleRequest = new BusLocationRequest("BUS-001", 6.9271, 79.8612);

        sampleEntity = new BusLocation("BUS-001", 6.9271, 79.8612, LocalDateTime.now());
        sampleEntity.setId(1L);
    }

    @Test
    @DisplayName("Should save location and return response DTO")
    void testSaveLocation() {
        when(repository.save(any(BusLocation.class))).thenReturn(sampleEntity);

        BusLocationResponse response = service.saveLocation(sampleRequest);

        assertNotNull(response);
        assertEquals("BUS-001", response.getBusId());
        assertEquals(6.9271, response.getLatitude());
        assertEquals(79.8612, response.getLongitude());
        verify(repository, times(1)).save(any(BusLocation.class));
    }

    @Test
    @DisplayName("Should return all locations as DTOs")
    void testGetAllLocations() {
        BusLocation entity2 = new BusLocation("BUS-002", 7.2906, 80.6337, LocalDateTime.now());
        entity2.setId(2L);

        when(repository.findAll()).thenReturn(Arrays.asList(sampleEntity, entity2));

        List<BusLocationResponse> result = service.getAllLocations();

        assertEquals(2, result.size());
        assertEquals("BUS-001", result.get(0).getBusId());
        assertEquals("BUS-002", result.get(1).getBusId());
    }

    @Test
    @DisplayName("Should return locations filtered by bus ID")
    void testGetLocationsByBusId() {
        when(repository.findByBusId("BUS-001")).thenReturn(List.of(sampleEntity));

        List<BusLocationResponse> result = service.getLocationsByBusId("BUS-001");

        assertEquals(1, result.size());
        assertEquals("BUS-001", result.get(0).getBusId());
    }

    @Test
    @DisplayName("Should return latest location for each bus")
    void testGetLatestLocations() {
        when(repository.findLatestLocationForEachBus()).thenReturn(List.of(sampleEntity));

        List<BusLocationResponse> result = service.getLatestLocations();

        assertEquals(1, result.size());
        assertNotNull(result.get(0).getTimestamp());
    }

    @Test
    @DisplayName("Should return empty list when no locations exist")
    void testGetAllLocationsEmpty() {
        when(repository.findAll()).thenReturn(List.of());

        List<BusLocationResponse> result = service.getAllLocations();

        assertTrue(result.isEmpty());
    }
}
