package com.bustracker.controller;

import com.bustracker.dto.response.RouteResponse;
import com.bustracker.service.RouteService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for {@link RouteController}.
 *
 * <p>Tests public route endpoints with MockMvc.
 * Verifies response format, status codes, and JSON structure.</p>
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RouteControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RouteService routeService;

    @Test
    @DisplayName("GET /api/routes - should return all routes")
    void getAllRoutes() throws Exception {
        RouteResponse route1 = new RouteResponse();
        route1.setId(1L);
        route1.setRouteNumber("1");
        route1.setName("Colombo Fort → Kandy");
        route1.setOrigin("Colombo Fort");
        route1.setDestination("Kandy");
        route1.setDistanceKm(115.0);

        RouteResponse route2 = new RouteResponse();
        route2.setId(2L);
        route2.setRouteNumber("2");
        route2.setName("Colombo Fort → Galle");
        route2.setOrigin("Colombo Fort");
        route2.setDestination("Galle");
        route2.setDistanceKm(130.0);

        when(routeService.getAllRoutes()).thenReturn(Arrays.asList(route1, route2));

        mockMvc.perform(get("/api/routes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].routeNumber").value("1"))
                .andExpect(jsonPath("$[0].name").value("Colombo Fort → Kandy"))
                .andExpect(jsonPath("$[1].routeNumber").value("2"));
    }

    @Test
    @DisplayName("GET /api/routes/search - should return matching routes")
    void searchRoutes() throws Exception {
        RouteResponse route = new RouteResponse();
        route.setId(1L);
        route.setRouteNumber("1");
        route.setName("Colombo Fort → Kandy");
        route.setOrigin("Colombo Fort");
        route.setDestination("Kandy");

        when(routeService.searchRoutes("kandy")).thenReturn(List.of(route));

        mockMvc.perform(get("/api/routes/search").param("query", "kandy"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].destination").value("Kandy"));
    }

    @Test
    @DisplayName("GET /api/routes - should return empty list when no routes")
    void getAllRoutes_Empty() throws Exception {
        when(routeService.getAllRoutes()).thenReturn(List.of());

        mockMvc.perform(get("/api/routes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
