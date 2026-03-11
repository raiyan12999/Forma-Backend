package com.forma.studio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the FORMA Studio Spring Boot application.
 * Run this class to start the server on port 8080.
 */
@SpringBootApplication
public class FormaApplication {

    public static void main(String[] args) {
        SpringApplication.run(FormaApplication.class, args);
    }
}
