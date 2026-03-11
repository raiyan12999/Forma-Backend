package com.forma.studio.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Represents a team member shown on the Team page.
 * Partners (isPartner = true) are shown in the main bio section with larger cards.
 * Non-partners appear in the studio team grid below.
 *
 * Maps to the "team_members" table in the database.
 */
@Entity
@Table(name = "team_members")
public class TeamMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Full name, e.g. "Khandaker Ashifuzzaman"
    @Column(nullable = false)
    private String name;

    // Job title, e.g. "Managing Director & Principal Architect"
    @Column(nullable = false)
    private String role;

    // Academic and professional credentials, e.g. "M.Arch (UCL Bartlett) · B.Arch (BUET)"
    @Column(columnDefinition = "TEXT")
    private String credentials;

    // Long-form biography shown on the team page
    @Column(columnDefinition = "TEXT")
    private String bio;

    // URL to the large version of the team member's photo (max 800px wide)
    // Used for the big partner bio cards
    private String photoLargeUrl;

    // URL to the medium version of the team member's photo (max 400px wide)
    // Used for the team grid cards
    private String photoMediumUrl;

    // Filename (UUID) used to locate and delete photo files from disk
    private String photoFilename;

    // Partners are displayed prominently at the top of the Team page with full bios
    @Column(nullable = false)
    private Boolean isPartner = false;

    // Controls the order team members appear (lower number = shown first)
    @Column(nullable = false)
    private Integer displayOrder = 0;

    // When this record was created - set automatically before insert
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

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

    public String getPhotoFilename() { return photoFilename; }
    public void setPhotoFilename(String photoFilename) { this.photoFilename = photoFilename; }

    public Boolean getIsPartner() { return isPartner; }
    public void setIsPartner(Boolean isPartner) { this.isPartner = isPartner; }

    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
