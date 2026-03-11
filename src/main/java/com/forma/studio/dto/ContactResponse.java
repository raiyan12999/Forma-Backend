package com.forma.studio.dto;

import java.time.LocalDateTime;

/**
 * The JSON shape for a contact submission shown in the admin panel.
 */
public class ContactResponse {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private String projectType;
    private String message;
    private Boolean isRead;
    private LocalDateTime submittedAt;

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
