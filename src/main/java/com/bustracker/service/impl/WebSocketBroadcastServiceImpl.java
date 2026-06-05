package com.bustracker.service.impl;

import com.bustracker.dto.websocket.BusStatusMessage;
import com.bustracker.dto.websocket.LocationUpdateMessage;
import com.bustracker.service.WebSocketBroadcastService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link WebSocketBroadcastService} using Spring's
 * {@link SimpMessagingTemplate} for STOMP message delivery.
 *
 * <p>This service is the bridge between the REST/service layer and
 * the WebSocket layer. When a bus location is updated via REST API,
 * this service broadcasts the update to all connected passengers.</p>
 *
 * <p>Design principles:</p>
 * <ul>
 *   <li><strong>SRP</strong>: Only handles WebSocket broadcasting</li>
 *   <li><strong>DIP</strong>: Other services depend on the
 *       {@link WebSocketBroadcastService} interface, not this class</li>
 *   <li><strong>Facade</strong>: Simplifies STOMP messaging to two methods</li>
 * </ul>
 */
@Service
public class WebSocketBroadcastServiceImpl implements WebSocketBroadcastService {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketBroadcastServiceImpl.class);

    private static final String TOPIC_ALL_LOCATIONS = "/topic/bus-locations";
    private static final String TOPIC_ROUTE_LOCATIONS = "/topic/bus-locations/";
    private static final String TOPIC_BUS_STATUS = "/topic/bus-status";

    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketBroadcastServiceImpl(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public void broadcastLocationUpdate(LocationUpdateMessage message) {
        // Broadcast to all-locations topic
        messagingTemplate.convertAndSend(TOPIC_ALL_LOCATIONS, message);

        // Also broadcast to route-specific topic if route is known
        if (message.getRouteId() != null) {
            String routeTopic = TOPIC_ROUTE_LOCATIONS + message.getRouteId();
            messagingTemplate.convertAndSend(routeTopic, message);
            logger.debug("📍 Broadcast location: bus={} → {}, route topic={}",
                    message.getBusId(), TOPIC_ALL_LOCATIONS, routeTopic);
        } else {
            logger.debug("📍 Broadcast location: bus={} → {}",
                    message.getBusId(), TOPIC_ALL_LOCATIONS);
        }
    }

    @Override
    public void broadcastStatusChange(BusStatusMessage message) {
        messagingTemplate.convertAndSend(TOPIC_BUS_STATUS, message);
        logger.info("🔄 Broadcast status change: bus={} {} → {}",
                message.getRegistrationNumber(), message.getPreviousStatus(), message.getNewStatus());
    }
}
