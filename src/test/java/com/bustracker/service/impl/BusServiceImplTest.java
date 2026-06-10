package com.bustracker.service.impl;

import com.bustracker.dto.request.BusRequest;
import com.bustracker.dto.response.BusResponse;
import com.bustracker.entity.Bus;
import com.bustracker.entity.Route;
import com.bustracker.entity.User;
import com.bustracker.enums.BusStatus;
import com.bustracker.exception.BadRequestException;
import com.bustracker.exception.ResourceNotFoundException;
import com.bustracker.repository.BusRepository;
import com.bustracker.repository.RouteRepository;
import com.bustracker.repository.UserRepository;
import com.bustracker.service.WebSocketBroadcastService;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link BusServiceImpl}.
 *
 * <p>Tests bus CRUD operations, status management, route/driver assignment,
 * duplicate registration validation, and WebSocket broadcast integration.</p>
 */
@ExtendWith(MockitoExtension.class)
class BusServiceImplTest {

    @Mock
    private BusRepository busRepository;

    @Mock
    private RouteRepository routeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private WebSocketBroadcastService broadcastService;

    @InjectMocks
    private BusServiceImpl busService;

    private Route testRoute;
    private Bus testBus;
    private User testDriver;

    @BeforeEach
    void setUp() {
        testRoute = new Route();
        testRoute.setId(1L);
        testRoute.setRouteNumber("1");
        testRoute.setName("Colombo Fort → Kandy");

        testDriver = new User();
        testDriver.setId(10L);
        testDriver.setName("Kamal Perera");

        testBus = new Bus("NB-1234", testRoute, 50);
        testBus.setId(1L);
        testBus.setStatus(BusStatus.IDLE);
        testBus.setDriver(testDriver);
    }

    @Test
    @DisplayName("Should create bus successfully with route and driver")
    void createBus_Success() {
        BusRequest request = new BusRequest();
        request.setRegistrationNumber("NB-5678");
        request.setRouteId(1L);
        request.setCapacity(40);
        request.setDriverId(10L);

        when(busRepository.existsByRegistrationNumber("NB-5678")).thenReturn(false);
        when(routeRepository.findById(1L)).thenReturn(Optional.of(testRoute));
        when(userRepository.findById(10L)).thenReturn(Optional.of(testDriver));
        when(busRepository.save(any(Bus.class))).thenAnswer(inv -> {
            Bus bus = inv.getArgument(0);
            bus.setId(2L);
            return bus;
        });

        BusResponse response = busService.createBus(request);

        assertNotNull(response);
        assertEquals("NB-5678", response.getRegistrationNumber());
        assertEquals("1", response.getRouteNumber());
        assertEquals("Kamal Perera", response.getDriverName());
        verify(busRepository).save(any(Bus.class));
    }

    @Test
    @DisplayName("Should throw exception for duplicate registration number")
    void createBus_DuplicateRegistration() {
        BusRequest request = new BusRequest();
        request.setRegistrationNumber("NB-1234");
        request.setRouteId(1L);
        request.setCapacity(50);

        when(busRepository.existsByRegistrationNumber("NB-1234")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> busService.createBus(request));
        verify(busRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when route not found")
    void createBus_RouteNotFound() {
        BusRequest request = new BusRequest();
        request.setRegistrationNumber("NB-9999");
        request.setRouteId(999L);
        request.setCapacity(50);

        when(busRepository.existsByRegistrationNumber("NB-9999")).thenReturn(false);
        when(routeRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> busService.createBus(request));
    }

    @Test
    @DisplayName("Should get bus by ID")
    void getBusById_Success() {
        when(busRepository.findById(1L)).thenReturn(Optional.of(testBus));

        BusResponse response = busService.getBusById(1L);

        assertNotNull(response);
        assertEquals("NB-1234", response.getRegistrationNumber());
        assertEquals("IDLE", response.getStatus());
    }

    @Test
    @DisplayName("Should throw exception when bus not found")
    void getBusById_NotFound() {
        when(busRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> busService.getBusById(999L));
    }

    @Test
    @DisplayName("Should return all buses")
    void getAllBuses() {
        Bus bus2 = new Bus("NB-5678", testRoute, 40);
        bus2.setId(2L);
        bus2.setStatus(BusStatus.ACTIVE);

        when(busRepository.findAll()).thenReturn(Arrays.asList(testBus, bus2));

        List<BusResponse> responses = busService.getAllBuses();

        assertEquals(2, responses.size());
    }

    @Test
    @DisplayName("Should get buses by route ID")
    void getBusesByRoute() {
        when(busRepository.findByRouteId(1L)).thenReturn(List.of(testBus));

        List<BusResponse> responses = busService.getBusesByRoute(1L);

        assertEquals(1, responses.size());
        assertEquals("NB-1234", responses.get(0).getRegistrationNumber());
    }

    @Test
    @DisplayName("Should update bus status and broadcast via WebSocket")
    void updateBusStatus_Success() {
        when(busRepository.findById(1L)).thenReturn(Optional.of(testBus));
        when(busRepository.save(any(Bus.class))).thenReturn(testBus);

        BusResponse response = busService.updateBusStatus(1L, "ACTIVE");

        assertEquals("ACTIVE", response.getStatus());
        verify(broadcastService).broadcastStatusChange(any());
    }

    @Test
    @DisplayName("Should throw exception for invalid status")
    void updateBusStatus_InvalidStatus() {
        when(busRepository.findById(1L)).thenReturn(Optional.of(testBus));

        assertThrows(BadRequestException.class,
                () -> busService.updateBusStatus(1L, "INVALID"));
    }

    @Test
    @DisplayName("Should update bus details including route and driver")
    void updateBus_Success() {
        BusRequest request = new BusRequest();
        request.setRegistrationNumber("NB-1234-UPDATED");
        request.setRouteId(1L);
        request.setCapacity(60);
        request.setDriverId(10L);

        when(busRepository.findById(1L)).thenReturn(Optional.of(testBus));
        when(routeRepository.findById(1L)).thenReturn(Optional.of(testRoute));
        when(userRepository.findById(10L)).thenReturn(Optional.of(testDriver));
        when(busRepository.save(any(Bus.class))).thenReturn(testBus);

        BusResponse response = busService.updateBus(1L, request);

        assertNotNull(response);
        verify(busRepository).save(any(Bus.class));
    }

    @Test
    @DisplayName("Should delete bus by ID")
    void deleteBus_Success() {
        when(busRepository.findById(1L)).thenReturn(Optional.of(testBus));

        busService.deleteBus(1L);

        verify(busRepository).delete(testBus);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent bus")
    void deleteBus_NotFound() {
        when(busRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> busService.deleteBus(999L));
    }
}
