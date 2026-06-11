package com.bustracker.service.impl;

import com.bustracker.dto.request.RouteRequest;
import com.bustracker.dto.response.RouteResponse;
import com.bustracker.entity.Route;
import com.bustracker.exception.BadRequestException;
import com.bustracker.exception.ResourceNotFoundException;
import com.bustracker.repository.RouteRepository;
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
 * Unit tests for {@link RouteServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
class RouteServiceImplTest {

    @Mock
    private RouteRepository routeRepository;

    @InjectMocks
    private RouteServiceImpl routeService;

    private Route sampleRoute;
    private RouteRequest sampleRequest;

    @BeforeEach
    void setUp() {
        sampleRequest = new RouteRequest("1", "Colombo Fort - Kandy",
                "Colombo Fort", "Kandy", 116.0, 240);

        sampleRoute = new Route("1", "Colombo Fort - Kandy",
                "Colombo Fort", "Kandy", 116.0, 240);
        sampleRoute.setId(1L);
    }

    @Test
    @DisplayName("Should create a new route")
    void testCreateRoute() {
        when(routeRepository.existsByRouteNumber("1")).thenReturn(false);
        when(routeRepository.save(any(Route.class))).thenReturn(sampleRoute);

        RouteResponse response = routeService.createRoute(sampleRequest);

        assertNotNull(response);
        assertEquals("1", response.getRouteNumber());
        assertEquals("Colombo Fort - Kandy", response.getName());
    }

    @Test
    @DisplayName("Should throw BadRequestException for duplicate route number")
    void testCreateDuplicateRoute() {
        when(routeRepository.existsByRouteNumber("1")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> routeService.createRoute(sampleRequest));
    }

    @Test
    @DisplayName("Should return route by ID")
    void testGetRouteById() {
        when(routeRepository.findById(1L)).thenReturn(Optional.of(sampleRoute));

        RouteResponse response = routeService.getRouteById(1L);

        assertEquals("Colombo Fort", response.getOrigin());
        assertEquals("Kandy", response.getDestination());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException for invalid ID")
    void testGetRouteByIdNotFound() {
        when(routeRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> routeService.getRouteById(99L));
    }

    @Test
    @DisplayName("Should return all active routes")
    void testGetActiveRoutes() {
        Route route2 = new Route("2", "Colombo Fort - Galle", "Colombo Fort", "Galle", 126.0, 210);
        route2.setId(2L);

        when(routeRepository.findByActiveTrue()).thenReturn(Arrays.asList(sampleRoute, route2));

        List<RouteResponse> result = routeService.getActiveRoutes();

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Should search routes by origin or destination")
    void testSearchRoutes() {
        when(routeRepository.findByOriginContainingIgnoreCaseOrDestinationContainingIgnoreCase(
                "Colombo", "Colombo")).thenReturn(List.of(sampleRoute));

        List<RouteResponse> result = routeService.searchRoutes("Colombo");

        assertEquals(1, result.size());
        assertEquals("Colombo Fort - Kandy", result.get(0).getName());
    }
}
