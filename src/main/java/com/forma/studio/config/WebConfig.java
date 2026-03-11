package com.forma.studio.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

/**
 * Configures Spring MVC to serve uploaded image files as static resources.
 *
 * WHY this is needed:
 * When we save an image to ./uploads/large/abc.jpg on disk, the browser needs
 * to be able to download it at a URL like http://localhost:8080/uploads/large/abc.jpg.
 * By default, Spring Boot only serves static files from /resources/static/.
 * This config adds the external uploads folder as an additional static file location.
 *
 * In production, consider serving the uploads folder through Nginx instead,
 * which is much faster for serving files than Spring Boot.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.upload.dir}")
    private String uploadDir;

    /**
     * Maps URL path /uploads/** to the ./uploads/ directory on disk.
     * So a file at ./uploads/large/abc.jpg is accessible at /uploads/large/abc.jpg
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Get the absolute path to the uploads directory
        String absoluteUploadPath = Paths.get(uploadDir).toAbsolutePath().normalize().toString();

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + absoluteUploadPath + "/");
    }
}
