package com.forma.studio.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * The JSON shape for an incoming contact form submission from the website.
 */
public class ContactRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    private String email;

    // Phone is optional
    private String phone;

    // The dropdown value from the contact form, e.g. "architecture", "residential"
    private String projectType;

    @NotBlank(message = "Message is required")
    private String message;

    // ---- Getters and Setters ----

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
}
