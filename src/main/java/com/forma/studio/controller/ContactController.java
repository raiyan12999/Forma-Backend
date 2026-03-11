package com.forma.studio.controller;

import com.forma.studio.dto.ContactRequest;
import com.forma.studio.dto.ContactResponse;
import com.forma.studio.service.ContactService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Handles the public contact form submission.
 * Only one public endpoint here — all other contact routes are in AdminController.
 *
 * Base path: /api/contact
 */
@RestController
@RequestMapping("/api/contact")
public class ContactController {

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    /**
     * POST /api/contact
     * Saves a contact form submission from the website.
     * The @Valid annotation triggers validation rules defined in ContactRequest.
     * Returns 201 Created with the saved submission (useful for the frontend to confirm).
     *
     * Frontend: contact.html form submit
     */
    @PostMapping
    public ResponseEntity<ContactResponse> submitContactForm(@Valid @RequestBody ContactRequest request) {
        ContactResponse response = contactService.saveSubmission(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
