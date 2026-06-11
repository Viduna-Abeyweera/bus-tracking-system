package com.bustracker.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * CORS (Cross-Origin Resource Sharing) configuration.
 *
 * <p>Updated from the Phase 1 version to:</p>
 * <ul>
 *   <li>Allow the {@code Authorization} header for JWT tokens</li>
 *   <li>Expose the {@code Authorization} header in responses</li>
 *   <li>Support configurable allowed origins for deployment flexibility</li>
 *   <li>Use {@link CorsConfigurationSource} bean (required when using
 *       Spring Security's {@code .cors()} configuration)</li>
 * </ul>
 */
@Configuration
public class CorsConfig {

    @Value("${cors.allowed-origins:http://localhost:5173}")
    private String allowedOrigins;

    /**
     * Provides a CORS configuration source bean that Spring Security
     * will use to apply CORS rules.
     *
     * <p>This replaces the previous {@code WebMvcConfigurer} approach
     * because Spring Security requires a {@code CorsConfigurationSource}
     * bean to properly integrate CORS with the security filter chain.</p>
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Split comma-separated origins for multi-origin support
        configuration.setAllowedOrigins(
                Arrays.asList(allowedOrigins.split(","))
        );

        configuration.setAllowedMethods(
                Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS")
        );

        configuration.setAllowedHeaders(
                Arrays.asList("Authorization", "Content-Type", "X-Requested-With")
        );

        // Expose Authorization header so frontend can read it
        configuration.setExposedHeaders(List.of("Authorization"));

        // Allow credentials (cookies, authorization headers)
        configuration.setAllowCredentials(true);

        // Cache pre-flight response for 1 hour
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);

        return source;
    }
}