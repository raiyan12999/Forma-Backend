package com.forma.studio.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * The JSON shape for sending/receiving team member data.
 * Photos are handled separately via multipart upload — not in this DTO.
 */
public class TeamMemberRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Role is required")
    private String role;

    private String credentials;
    private String bio;
    private Boolean isPartner;
    private Integer displayOrder;

    // ---- Getters and Setters ----

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getCredentials() { return credentials; }
    public void setCredentials(String credentials) { this.credentials = credentials; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public Boolean getIsPartner() { return isPartner; }
    public void setIsPartner(Boolean isPartner) { this.isPartner = isPartner; }

    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }
}
