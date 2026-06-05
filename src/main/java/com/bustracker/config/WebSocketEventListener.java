package com.bustracker.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Listens for WebSocket lifecycle events (connect, disconnect, subscribe).
 *
 * <p>Tracks the number of active WebSocket connections for monitoring.
 * This is useful for:</p>
 * <ul>
 *   <li>Logging connection activity for debugging</li>
 *   <li>Monitoring system health and load</li>
 *   <li>Understanding usage patterns</li>
 * </ul>
 *
 * <p>Demonstrates the <strong>Observer Pattern</strong> — Spring fires
 * events and this listener reacts without coupling to the WebSocket layer.</p>
 */
@Component
public class WebSocketEventListener {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);

    private final AtomicInteger activeConnections = new AtomicInteger(0);

    @EventListener
    public void handleWebSocketConnect(SessionConnectedEvent event) {
        int count = activeConnections.incrementAndGet();
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        logger.info("🔗 WebSocket connected: session={}, active connections={}", sessionId, count);
    }

    @EventListener
    public void handleWebSocketDisconnect(SessionDisconnectEvent event) {
        int count = activeConnections.decrementAndGet();
        String sessionId = event.getSessionId();
        logger.info("🔌 WebSocket disconnected: session={}, active connections={}", sessionId, count);
    }

    @EventListener
    public void handleSubscription(SessionSubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String destination = headerAccessor.getDestination();
        String sessionId = headerAccessor.getSessionId();
        logger.debug("📡 Subscription: session={} → {}", sessionId, destination);
    }

    /**
     * Returns the current number of active WebSocket connections.
     * Useful for health check endpoints and monitoring.
     */
    public int getActiveConnectionCount() {
        return activeConnections.get();
    }
}
