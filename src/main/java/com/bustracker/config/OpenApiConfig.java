package com.bustracker.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI (Swagger) configuration for the Bus Tracking API.
 *
 * <p>Provides interactive API documentation at {@code /swagger-ui.html}
 * and raw OpenAPI spec at {@code /api-docs}.</p>
 *
 * <p>JWT Bearer token authentication is configured so endpoints
 * can be tested directly from the Swagger UI.</p>
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI busTrackerOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Bus Tracker SL — REST API")
                        .description("""
                                Real-time bus tracking system for Sri Lanka.
                                
                                **Authentication:** Most write endpoints require a JWT token.
                                Use `/api/auth/login` to get a token, then click "Authorize" 
                                and paste: `Bearer <your-token>`
                                
                                **Roles:**
                                - PASSENGER — Read access to routes, buses, stops, locations
                                - DRIVER — Can share GPS location + change bus status
                                - ADMIN — Full CRUD on routes, buses, stops, schedules
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Bus Tracker Team")
                                .email("admin@bustracker.lk"))
                        .license(new License()
                                .name("MIT License")))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Enter your JWT token (without 'Bearer ' prefix)")));
    }
}
