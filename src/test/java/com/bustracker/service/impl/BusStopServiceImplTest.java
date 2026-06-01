package com.bustracker.service.impl;

import com.bustracker.dto.request.BusStopRequest;
import com.bustracker.dto.response.BusStopResponse;
import com.bustracker.entity.BusStop;
import com.bustracker.exception.ResourceNotFoundException;
import com.bustracker.repository.BusStopRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link BusStopServiceImpl}.
 *
 * <p>Tests the full CRUD lifecycle including the exception handling
 * for non-existent resources.</p>
 */
@ExtendWith(MockitoExtension.class)
class BusStopServiceImplTest {

    @Mock
    private BusStopRepository repository;

    @InjectMocks
    private BusStopServiceImpl service;

    private BusStop sampleStop;
    private BusStopRequest sampleRequest;

    @BeforeEach
    void setUp() {
        sampleRequest = new BusStopRequest("Colombo Fort", 6.9344, 79.8428);

        sampleStop = new BusStop("Colombo Fort", 6.9344, 79.8428);
        sampleStop.setId(1L);
    }

    @Test
    @DisplayName("Should return all bus stops as DTOs")
    void testGetAllStops() {
        BusStop stop2 = new BusStop("Kandy Station", 7.2906, 80.6337);
        stop2.setId(2L);

        when(repository.findAll()).thenReturn(Arrays.asList(sampleStop, stop2));

        List<BusStopResponse> result = service.getAllStops();

        assertEquals(2, result.size());
        assertEquals("Colombo Fort", result.get(0).getName());
        assertEquals("Kandy Station", result.get(1).getName());
    }

    @Test
    @DisplayName("Should return a single bus stop by ID")
    void testGetStopById() {
        when(repository.findById(1L)).thenReturn(Optional.of(sampleStop));

        BusStopResponse result = service.getStopById(1L);

        assertNotNull(result);
        assertEquals("Colombo Fort", result.getName());
        assertEquals(6.9344, result.getLatitude());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException for invalid ID")
    void testGetStopByIdNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.getStopById(99L)
        );

        assertTrue(exception.getMessage().contains("BusStop"));
        assertTrue(exception.getMessage().contains("99"));
    }

    @Test
    @DisplayName("Should create a new bus stop and return response DTO")
    void testCreateStop() {
        when(repository.save(any(BusStop.class))).thenReturn(sampleStop);

        BusStopResponse result = service.createStop(sampleRequest);

        assertNotNull(result);
        assertEquals("Colombo Fort", result.getName());
        verify(repository, times(1)).save(any(BusStop.class));
    }

    @Test
    @DisplayName("Should update an existing bus stop")
    void testUpdateStop() {
        BusStopRequest updateRequest = new BusStopRequest("Colombo Fort Updated", 6.9350, 79.8430);

        when(repository.findById(1L)).thenReturn(Optional.of(sampleStop));
        when(repository.save(any(BusStop.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BusStopResponse result = service.updateStop(1L, updateRequest);

        assertEquals("Colombo Fort Updated", result.getName());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when updating non-existent stop")
    void testUpdateStopNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.updateStop(99L, sampleRequest));
    }

    @Test
    @DisplayName("Should delete a bus stop by ID")
    void testDeleteStop() {
        when(repository.findById(1L)).thenReturn(Optional.of(sampleStop));

        assertDoesNotThrow(() -> service.deleteStop(1L));
        verify(repository, times(1)).delete(sampleStop);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when deleting non-existent stop")
    void testDeleteStopNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.deleteStop(99L));
    }
}
