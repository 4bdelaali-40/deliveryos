package com.deliveryos.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration Swagger / OpenAPI 3.
 * Accessible à http://localhost:8080/swagger-ui.html
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "DeliveryOS API",
                version = "1.0.0",
                description = "Enterprise Delivery Management Platform — REST API",
                contact = @Contact(
                        name = "DeliveryOS Team",
                        email = "dev@deliveryos.com"
                )
        ),
        servers = {
                @Server(url = "http://localhost:8080", description = "Local development"),
        }
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "JWT token — obtenu via POST /api/auth/login"
)
public class OpenApiConfig {
    // La configuration est entièrement portée par les annotations
}