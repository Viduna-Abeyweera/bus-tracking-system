package com.bustracker.config;

import com.bustracker.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security configuration for the Bus Tracking System.
 *
 * <p>This class configures:</p>
 * <ul>
 *   <li>Stateless session management (JWT-based, no server-side sessions)</li>
 *   <li>Role-based endpoint protection (see access rules below)</li>
 *   <li>BCrypt password encoding for secure password storage</li>
 *   <li>JWT filter integration into the Spring Security filter chain</li>
 * </ul>
 *
 * <h3>Access Rules</h3>
 * <table>
 *   <tr><th>Endpoint</th><th>Access</th></tr>
 *   <tr><td>POST /api/auth/**</td><td>Public (registration, login)</td></tr>
 *   <tr><td>GET /api/bus-locations/**</td><td>Any authenticated user</td></tr>
 *   <tr><td>POST /api/bus-locations</td><td>DRIVER or ADMIN</td></tr>
 *   <tr><td>GET /api/bus-stops/**</td><td>Any authenticated user</td></tr>
 *   <tr><td>POST/PUT/DELETE /api/bus-stops/**</td><td>ADMIN only</td></tr>
 *   <tr><td>Swagger UI</td><td>Public</td></tr>
 * </table>
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    /**
     * Configures the security filter chain.
     *
     * <p>CSRF is disabled because we use stateless JWT tokens (not cookies).
     * Sessions are set to STATELESS to ensure no server-side state.</p>
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF — not needed for stateless JWT auth
                .csrf(csrf -> csrf.disable())

                // Configure CORS — handled by CorsConfig
                .cors(cors -> {})

                // Stateless session — no server-side sessions
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Endpoint authorization rules
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/api-docs/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/error").permitAll()
                        // WebSocket endpoint — auth is handled by WebSocketAuthInterceptor
                        .requestMatchers("/ws/**").permitAll()

                        // Bus locations: anyone authenticated can read, only DRIVER/ADMIN can write
                        .requestMatchers(HttpMethod.GET, "/api/bus-locations/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/bus-locations").hasAnyRole("DRIVER", "ADMIN")

                        // Bus stops: anyone authenticated can read, only ADMIN can write
                        .requestMatchers(HttpMethod.GET, "/api/bus-stops/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/bus-stops/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/bus-stops/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/bus-stops/**").hasRole("ADMIN")

                        // Routes: anyone authenticated can read, only ADMIN can write
                        .requestMatchers(HttpMethod.GET, "/api/routes/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/routes/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/routes/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/routes/**").hasRole("ADMIN")

                        // Buses: anyone authenticated can read, ADMIN can manage
                        .requestMatchers(HttpMethod.GET, "/api/buses/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/buses/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/buses/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/buses/**").hasAnyRole("DRIVER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/buses/**").hasRole("ADMIN")

                        // Schedules: anyone authenticated can read, only ADMIN can write
                        .requestMatchers(HttpMethod.GET, "/api/schedules/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/schedules/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/schedules/**").hasRole("ADMIN")

                        // All other endpoints require authentication
                        .anyRequest().authenticated()
                )

                // Add JWT filter before the default UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * BCrypt password encoder bean — used to hash passwords during
     * registration and verify them during login.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Authentication manager bean — required by the auth service
     * to authenticate user credentials against the database.
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
