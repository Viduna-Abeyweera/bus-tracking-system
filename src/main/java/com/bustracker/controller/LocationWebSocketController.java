package com.bustracker.controller;

import com.bustracker.config.WebSocketEventListener;
import com.bustracker.dto.websocket.LocationUpdateMessage;
import com.bustracker.service.WebSocketBroadcastService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

/**
 * STOMP WebSocket controller for real-time bus location tracking.
 *
 * <p>This controller handles incoming STOMP messages from driver apps
 * and broadcasts them to all subscribed passengers. It provides an
 * alternative to the REST API for location updates — drivers can
 * send updates via WebSocket for lower latency.</p>
 *
 * <h3>Message Flow</h3>
 * <pre>
 *   Driver → sends to /app/bus/location
 *         → this controller receives it
 *         → broadcasts to /topic/bus-locations
 *         → all subscribed passengers receive it
 * </pre>
 *
 * <p>Note: The REST endpoint {@code POST /api/bus-locations} also
 * triggers WebSocket broadcasts via the updated
 * {@code BusLocationServiceImpl}. Drivers can use either channel.</p>
 */
@Controller
public class LocationWebSocketController {

    private static final Logger logger = LoggerFactory.getLogger(LocationWebSocketController.class);

    private final WebSocketBroadcastService broadcastService;
    private final WebSocketEventListener eventListener;

    public LocationWebSocketController(WebSocketBroadcastService broadcastService,
                                        WebSocketEventListener eventListener) {
        this.broadcastService = broadcastService;
        this.eventListener = eventListener;
    }

    /**
     * Handles bus location updates sent via STOMP.
     *
     * <p>Drivers send messages to {@code /app/bus/location} and
     * the server broadcasts to all subscribers.</p>
     *
     * @param message the location update from a driver
     */
    @MessageMapping("/bus/location")
    public void handleLocationUpdate(@Payload LocationUpdateMessage message) {
        // Set timestamp if not provided
        if (message.getTimestamp() == null) {
            message.setTimestamp(LocalDateTime.now().toString());
        }

        logger.debug("📡 Received WebSocket location: bus={} at ({}, {})",
                message.getBusId(), message.getLatitude(), message.getLongitude());

        // Broadcast to all subscribers
        broadcastService.broadcastLocationUpdate(message);
    }

    /**
     * Returns the current active WebSocket connection count.
     * Can be called via STOMP: /app/connections/count
     */
    @MessageMapping("/connections/count")
    public int getActiveConnections() {
        return eventListener.getActiveConnectionCount();
    }
}
