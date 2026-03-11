package com.forma.studio.dto;

import java.time.LocalDateTime;

/**
 * The JSON shape returned when the frontend requests team member data.
 */
public class TeamMemberResponse {

    private Long id;
    private String name;
    private String role;
    private String credentials;
    private String bio;
    private String photoLargeUrl;
    private String photoMediumUrl;
    private Boolean isPartner;
    private Integer displayOrder;
    private LocalDateTime createdAt;

    // ---- Getters and Setters ----

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getCredentials() { return credentials; }
    public void setCredentials(String credentials) { this.credentials = credentials; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getPhotoLargeUrl() { return photoLargeUrl; }
    public void setPhotoLargeUrl(String photoLargeUrl) { this.photoLargeUrl = photoLargeUrl; }

    public String getPhotoMediumUrl() { return photoMediumUrl; }
    public void setPhotoMediumUrl(String photoMediumUrl) { this.photoMediumUrl = photoMediumUrl; }

    public Boolean getIsPartner() { return isPartner; }
    public void setIsPartner(Boolean isPartner) { this.isPartner = isPartner; }

    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
