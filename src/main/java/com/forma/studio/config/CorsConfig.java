package com.forma.studio.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

/**
 * Configures Cross-Origin Resource Sharing (CORS).
 *
 * WHY we need this:
 * The browser blocks JavaScript on http://localhost:5500 (your HTML files)
 * from making fetch() calls to http://localhost:8080 (our Spring Boot server)
 * because they are on different ports = different "origins".
 *
 * This config tells the browser: "It's okay for these origins to call our API."
 *
 * The allowed origins come from application.properties so you can add your
 * production domain there without touching this file.
 */
@Configuration
public class CorsConfig {

    // Read allowed origins from application.properties
    // Example value: "http://localhost:5500,http://127.0.0.1:5500"
    @Value("${app.cors.allowed-origins}")
    private String allowedOriginsStr;

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // Split the comma-separated origins string into a list
        List<String> allowedOrigins = Arrays.asList(allowedOriginsStr.split(","));
        config.setAllowedOrigins(allowedOrigins);

        // Allow all standard HTTP methods
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Allow the Content-Type and Authorization headers
        // Authorization is needed for the admin panel's Basic Auth calls
        config.setAllowedHeaders(Arrays.asList("Content-Type", "Authorization", "Accept"));

        // Allow credentials (needed for Basic Auth)
        config.setAllowCredentials(true);

        // Apply this CORS config to all API routes
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);

        return new CorsFilter(source);
    }
}
