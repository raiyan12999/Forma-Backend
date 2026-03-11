package com.forma.studio.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Catches exceptions thrown anywhere in the application and converts them
 * into clean, consistent JSON error responses.
 *
 * Without this, Spring would return HTML error pages or ugly stack traces.
 * With this, every error returns: { "error": "...", "message": "...", "timestamp": "..." }
 *
 * The frontend admin panel reads the "message" field to show error alerts to the user.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles 404 Not Found and other ResponseStatusExceptions thrown by the services.
     * Example: when a project ID doesn't exist.
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatusException(ResponseStatusException ex) {
        return buildErrorResponse(ex.getStatusCode().value(), ex.getReason());
    }

    /**
     * Handles validation failures from @Valid annotations on request bodies.
     * Returns a 400 Bad Request with details about which fields failed.
     * Example: "Name is required", "Please provide a valid email address"
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException ex) {
        // Collect all field errors into a readable message
        StringBuilder messageBuilder = new StringBuilder("Validation failed: ");
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            messageBuilder.append(fieldError.getField())
                         .append(" — ")
                         .append(fieldError.getDefaultMessage())
                         .append("; ");
        }
        return buildErrorResponse(HttpStatus.BAD_REQUEST.value(), messageBuilder.toString());
    }

    /**
     * Handles file upload errors (wrong type, too large, etc.)
     * from the @IllegalArgumentException throws in ImageService.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
    }

    /**
     * Handles file read/write errors during image processing.
     */
    @ExceptionHandler(java.io.IOException.class)
    public ResponseEntity<Map<String, Object>> handleIOException(java.io.IOException ex) {
        logger.error("File operation failed", ex);
        return buildErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "File processing failed. Please try again."
        );
    }

    /**
     * Catch-all for any unexpected exceptions.
     * We log the full error but return a generic message to the client
     * to avoid leaking internal implementation details.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        logger.error("Unexpected error", ex);
        return buildErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "An unexpected error occurred. Please try again."
        );
    }

    /**
     * Builds the standard error response body used by all handlers above.
     */
    private ResponseEntity<Map<String, Object>> buildErrorResponse(int status, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", status);
        body.put("message", message);
        body.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.status(status).body(body);
    }
}
