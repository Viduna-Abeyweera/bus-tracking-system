package com.bustracker.service;

import com.bustracker.dto.websocket.BusStatusMessage;
import com.bustracker.dto.websocket.LocationUpdateMessage;

/**
 * Service interface for broadcasting real-time updates via WebSocket.
 *
 * <p>This service acts as a <strong>Facade</strong> over the STOMP
 * messaging system, providing a clean API for other services to
 * broadcast messages without knowing WebSocket internals.</p>
 *
 * <p>Topic architecture:</p>
 * <ul>
 *   <li>{@code /topic/bus-locations} — all bus location updates</li>
 *   <li>{@code /topic/bus-locations/{routeId}} — route-specific updates</li>
 *   <li>{@code /topic/bus-status} — bus status changes</li>
 * </ul>
 */
public interface WebSocketBroadcastService {

    /**
     * Broadcasts a bus location update to all subscribers.
     * Also sends to the route-specific topic if routeId is available.
     */
    void broadcastLocationUpdate(LocationUpdateMessage message);

    /**
     * Broadcasts a bus status change to all subscribers.
     */
    void broadcastStatusChange(BusStatusMessage message);
}
