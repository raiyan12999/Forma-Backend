package com.forma.studio.service;

import com.forma.studio.dto.ContactRequest;
import com.forma.studio.dto.ContactResponse;
import com.forma.studio.entity.ContactSubmission;
import com.forma.studio.repository.ContactRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles contact form submissions.
 * Saves incoming messages to the database and provides admin read access.
 */
@Service
public class ContactService {

    private final ContactRepository contactRepository;

    public ContactService(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    /**
     * Saves a new contact form submission to the database.
     * Called when someone submits the contact form on the website.
     * New submissions start as unread (isRead = false).
     */
    public ContactResponse saveSubmission(ContactRequest request) {
        ContactSubmission submission = new ContactSubmission();
        submission.setName(request.getName());
        submission.setEmail(request.getEmail());
        submission.setPhone(request.getPhone());
        submission.setProjectType(request.getProjectType());
        submission.setMessage(request.getMessage());
        // isRead defaults to false — set in the entity

        submission = contactRepository.save(submission);
        return toResponse(submission);
    }

    /**
     * Returns all contact submissions, newest first.
     * Used in the admin messages table.
     */
    public List<ContactResponse> getAllSubmissions() {
        return contactRepository.findAllByOrderBySubmittedAtDesc()
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    /**
     * Marks a message as read in the admin panel.
     * The unread count on the dashboard decreases as messages are read.
     */
    public ContactResponse markAsRead(Long submissionId) {
        ContactSubmission submission = contactRepository.findById(submissionId)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Message not found with id: " + submissionId
            ));

        submission.setIsRead(true);
        submission = contactRepository.save(submission);
        return toResponse(submission);
    }

    /**
     * Returns the count of unread messages.
     * Used by the admin dashboard to show a notification badge.
     */
    public long getUnreadCount() {
        return contactRepository.countByIsReadFalse();
    }

    /**
     * Converts a ContactSubmission entity to a ContactResponse DTO.
     */
    private ContactResponse toResponse(ContactSubmission submission) {
        ContactResponse response = new ContactResponse();
        response.setId(submission.getId());
        response.setName(submission.getName());
        response.setEmail(submission.getEmail());
        response.setPhone(submission.getPhone());
        response.setProjectType(submission.getProjectType());
        response.setMessage(submission.getMessage());
        response.setIsRead(submission.getIsRead());
        response.setSubmittedAt(submission.getSubmittedAt());
        return response;
    }
}
