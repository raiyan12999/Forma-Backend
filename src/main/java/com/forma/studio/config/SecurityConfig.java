package com.forma.studio.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configures which API endpoints require authentication and which are open.
 *
 * The rules are:
 *   - /api/admin/**  →  requires username + password (HTTP Basic Auth)
 *   - everything else  →  open, no auth needed
 *
 * We use HTTP Basic Auth (username/password sent in the Authorization header).
 * This is simple and works well with the admin panel's fetch() calls.
 * Later you can upgrade to JWT tokens by replacing this config.
 *
 * The admin username and password are stored in application.properties.
 * For production, move these to environment variables or a secrets manager.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Read credentials from application.properties
    @Value("${app.admin.username}")
    private String adminUsername;

    @Value("${app.admin.password}")
    private String adminPassword;

    /**
     * Defines the security rules for all HTTP requests.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF because our API is stateless (no browser session cookies).
            // CSRF protection is for traditional form-based apps, not REST APIs.
            .csrf(AbstractHttpConfigurer::disable)

            // Allow requests from the frontend development server (handled by CorsConfig)
            .cors(cors -> {})

            // Define which paths need auth
            .authorizeHttpRequests(auth -> auth
                // Admin API requires authentication
                .requestMatchers("/api/admin/**").authenticated()

                // Public API endpoints — no auth needed
                .requestMatchers(HttpMethod.GET, "/api/projects/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/team/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/contact").permitAll()

                // Static files (uploaded images served at /uploads/**)
                .requestMatchers("/uploads/**").permitAll()

                // Admin HTML pages — served as static files, no auth check here
                // (The HTML pages themselves check sessionStorage and redirect to login)
                .requestMatchers("/admin/**").permitAll()

                // Allow everything else (including the public HTML pages)
                .anyRequest().permitAll()
            )

            // Use HTTP Basic Auth for the admin API
            // The admin panel's fetch() calls include: Authorization: Basic base64(username:password)
            .httpBasic(basic -> {})

            // Stateless sessions — we don't use server-side sessions
            // Each API request must include the Authorization header
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );

        return http.build();
    }

    /**
     * Creates an in-memory user with the admin credentials from application.properties.
     * This is fine for a single-admin system. If you need multiple users, switch to
     * a database-backed UserDetailsService.
     */
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails adminUser = User.builder()
            .username(adminUsername)
            .password(passwordEncoder.encode(adminPassword))
            .roles("ADMIN")
            .build();

        return new InMemoryUserDetailsManager(adminUser);
    }

    /**
     * BCrypt is the standard password hashing algorithm.
     * Even though we store the password in application.properties (cleartext),
     * Spring Security requires it to be encoded in memory.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
