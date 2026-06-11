package com.bustracker.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket configuration using STOMP protocol over SockJS.
 *
 * <p>This configuration sets up a message broker architecture for
 * real-time bus location tracking. The system uses:</p>
 * <ul>
 *   <li><strong>STOMP</strong> — Simple Text Oriented Messaging Protocol,
 *       providing pub/sub messaging semantics over WebSocket</li>
 *   <li><strong>SockJS</strong> — fallback transport for browsers that
 *       don't support WebSocket (long-polling, etc.)</li>
 * </ul>
 *
 * <h3>Channel Architecture</h3>
 * <pre>
 *   Driver App → /app/bus/location     → Server processes → /topic/bus-locations   → All Passengers
 *   Driver App → /app/bus/status       → Server processes → /topic/bus-status      → All Passengers
 *   Frontend   → subscribes to /topic/bus-locations/{routeId} → Route-specific updates
 * </pre>
 *
 * <p>This demonstrates the <strong>Observer Pattern</strong>: passengers
 * subscribe to topics and automatically receive updates when bus
 * locations change, without polling.</p>
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final WebSocketAuthInterceptor authInterceptor;

    public WebSocketConfig(WebSocketAuthInterceptor authInterceptor) {
        this.authInterceptor = authInterceptor;
    }

    /**
     * Configures the message broker with topic-based destinations.
     *
     * <p>{@code /topic} — broadcast destinations (one-to-many)</p>
     * <p>{@code /queue} — point-to-point destinations (one-to-one)</p>
     * <p>{@code /app} — prefix for messages routed to @MessageMapping methods</p>
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Enable a simple in-memory broker for /topic and /queue destinations
        config.enableSimpleBroker("/topic", "/queue");
        // Prefix for messages from clients to server-side @MessageMapping methods
        config.setApplicationDestinationPrefixes("/app");
    }

    /**
     * Registers the STOMP WebSocket endpoint with SockJS fallback.
     *
     * <p>Clients connect via:</p>
     * <ul>
     *   <li>WebSocket: {@code ws://host:8080/ws}</li>
     *   <li>SockJS fallback: {@code http://host:8080/ws/info}</li>
     * </ul>
     *
     * <p>The {@link WebSocketAuthInterceptor} validates JWT tokens
     * from the query parameter during the handshake.</p>
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .addInterceptors(authInterceptor)
                .withSockJS();
    }
}
