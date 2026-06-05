package com.bustracker.service.impl;

import com.bustracker.dto.websocket.BusStatusMessage;
import com.bustracker.dto.websocket.LocationUpdateMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link WebSocketBroadcastServiceImpl}.
 *
 * <p>Tests verify that messages are broadcast to the correct STOMP
 * topics and that route-specific topics are used when available.</p>
 */
@ExtendWith(MockitoExtension.class)
class WebSocketBroadcastServiceImplTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    private WebSocketBroadcastServiceImpl broadcastService;

    @BeforeEach
    void setUp() {
        broadcastService = new WebSocketBroadcastServiceImpl(messagingTemplate);
    }

    @Test
    @DisplayName("Should broadcast location to global topic")
    void testBroadcastLocationToGlobalTopic() {
        LocationUpdateMessage message = new LocationUpdateMessage(
                "BUS-001", "NB-1234", null, null,
                6.9344, 79.8428, 30.0, "2026-06-04T10:00:00"
        );

        broadcastService.broadcastLocationUpdate(message);

        verify(messagingTemplate).convertAndSend(
                eq("/topic/bus-locations"), eq(message));
    }

    @Test
    @DisplayName("Should broadcast location to both global and route-specific topics")
    void testBroadcastLocationToRouteSpecificTopic() {
        LocationUpdateMessage message = new LocationUpdateMessage(
                "BUS-001", "NB-1234", 1L, "1",
                6.9344, 79.8428, 30.0, "2026-06-04T10:00:00"
        );

        broadcastService.broadcastLocationUpdate(message);

        // Should send to global topic
        verify(messagingTemplate).convertAndSend(
                eq("/topic/bus-locations"), eq(message));
        // Should also send to route-specific topic
        verify(messagingTemplate).convertAndSend(
                eq("/topic/bus-locations/1"), eq(message));
    }

    @Test
    @DisplayName("Should NOT broadcast to route topic when routeId is null")
    void testBroadcastLocationWithoutRouteId() {
        LocationUpdateMessage message = new LocationUpdateMessage(
                "BUS-001", "NB-1234", null, null,
                6.9344, 79.8428, 30.0, "2026-06-04T10:00:00"
        );

        broadcastService.broadcastLocationUpdate(message);

        // Should only send to global topic (1 invocation total)
        verify(messagingTemplate, times(1)).convertAndSend(anyString(), any(LocationUpdateMessage.class));
    }

    @Test
    @DisplayName("Should broadcast bus status change to status topic")
    void testBroadcastStatusChange() {
        BusStatusMessage message = new BusStatusMessage(
                1L, "NB-1234", 1L, "1",
                "IDLE", "ACTIVE", "2026-06-04T10:00:00"
        );

        broadcastService.broadcastStatusChange(message);

        verify(messagingTemplate).convertAndSend(
                eq("/topic/bus-status"), eq(message));
    }

    @Test
    @DisplayName("Should include all fields in location update message")
    void testLocationMessageFields() {
        LocationUpdateMessage message = new LocationUpdateMessage(
                "BUS-001", "NB-1234", 1L, "1",
                6.9344, 79.8428, 25.5, "2026-06-04T10:00:00"
        );

        assertEquals("BUS-001", message.getBusId());
        assertEquals("NB-1234", message.getRegistrationNumber());
        assertEquals(1L, message.getRouteId());
        assertEquals("1", message.getRouteNumber());
        assertEquals(6.9344, message.getLatitude());
        assertEquals(79.8428, message.getLongitude());
        assertEquals(25.5, message.getSpeed());
        assertEquals("2026-06-04T10:00:00", message.getTimestamp());
    }

    @Test
    @DisplayName("Should include all fields in status message")
    void testStatusMessageFields() {
        BusStatusMessage message = new BusStatusMessage(
                1L, "NB-1234", 1L, "1",
                "IDLE", "ACTIVE", "2026-06-04T10:00:00"
        );

        assertEquals(1L, message.getBusId());
        assertEquals("NB-1234", message.getRegistrationNumber());
        assertEquals("IDLE", message.getPreviousStatus());
        assertEquals("ACTIVE", message.getNewStatus());
    }
}
