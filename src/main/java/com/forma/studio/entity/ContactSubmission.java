package com.forma.studio.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Stores a message submitted through the Contact page form.
 * Admin can view and mark messages as read in the admin panel.
 *
 * Maps to the "contact_submissions" table in the database.
 */
@Entity
@Table(name = "contact_submissions")
public class ContactSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The sender's full name
    @Column(nullable = false)
    private String name;

    // The sender's email address
    @Column(nullable = false)
    private String email;

    // Optional phone number
    private String phone;

    // The selected project type from the dropdown, e.g. "architecture", "residential"
    private String projectType;

    // The full message text
    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    // Whether the admin has viewed this submission. New messages start as unread (false).
    // The dashboard shows a count of unread messages to prompt attention.
    @Column(nullable = false)
    private Boolean isRead = false;

    // When the form was submitted - set automatically before insert
    @Column(nullable = false, updatable = false)
    private LocalDateTime submittedAt;

    @PrePersist
    protected void onSubmit() {
        submittedAt = LocalDateTime.now();
    }

    // ---- Getters and Setters ----

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getProjectType() { return projectType; }
    public void setProjectType(String projectType) { this.projectType = projectType; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Boolean getIsRead() { return isRead; }
    public void setIsRead(Boolean isRead) { this.isRead = isRead; }

    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }
}
