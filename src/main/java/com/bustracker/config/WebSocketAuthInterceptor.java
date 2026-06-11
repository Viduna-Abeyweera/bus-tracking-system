package com.bustracker.config;

import com.bustracker.security.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

/**
 * WebSocket handshake interceptor that validates JWT tokens during
 * the initial HTTP upgrade request.
 *
 * <p>Since WebSocket connections start as HTTP requests, we can
 * validate the JWT token from the query parameter during the
 * handshake phase. This is necessary because:</p>
 * <ul>
 *   <li>Browser WebSocket API doesn't support custom headers</li>
 *   <li>The JWT filter only works for HTTP requests, not WebSocket frames</li>
 *   <li>SockJS transport also uses the initial HTTP request</li>
 * </ul>
 *
 * <p>Usage: Client connects with {@code ws://host:8080/ws?token=JWT_TOKEN}</p>
 */
@Component
public class WebSocketAuthInterceptor implements HandshakeInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketAuthInterceptor.class);

    private final JwtTokenProvider jwtTokenProvider;

    public WebSocketAuthInterceptor(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * Validates JWT token before allowing the WebSocket handshake.
     *
     * <p>Extracts the token from the "token" query parameter and
     * validates it. If valid, stores the user email in the WebSocket
     * session attributes for later use.</p>
     *
     * @return true if the handshake should proceed, false to reject
     */
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                    WebSocketHandler wsHandler, Map<String, Object> attributes) {
        if (request instanceof ServletServerHttpRequest servletRequest) {
            String token = servletRequest.getServletRequest().getParameter("token");

            if (token != null && jwtTokenProvider.validateToken(token)) {
                String email = jwtTokenProvider.getEmailFromToken(token);
                String role = jwtTokenProvider.getRoleFromToken(token);
                attributes.put("email", email);
                attributes.put("role", role);
                logger.debug("WebSocket handshake authenticated for: {}", email);
                return true;
            }
        }

        // Allow unauthenticated connections for public tracking view
        // The STOMP layer can enforce auth for specific destinations
        logger.debug("WebSocket handshake without JWT — allowing for public view");
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                WebSocketHandler wsHandler, Exception exception) {
        // No post-handshake processing needed
    }
}
