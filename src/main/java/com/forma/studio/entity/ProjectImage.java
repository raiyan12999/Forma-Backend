package com.forma.studio.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Represents one uploaded image linked to a project.
 * Each upload automatically produces THREE resized versions stored on disk.
 *
 * WHY three versions:
 *   - Thumbnail (400x300): used in project grids and cards — very small file ~25KB
 *   - Medium (800px wide): used in carousels and team photos — ~80KB
 *   - Large (1920px wide): used in detail pages and lightbox — ~200KB
 *
 * This means a 12-card grid loads ~300KB instead of potentially 96MB of raw photos.
 * The original uploaded file is NEVER saved — only these three processed versions.
 *
 * Maps to the "project_images" table in the database.
 */
@Entity
@Table(name = "project_images")
public class ProjectImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Which project this image belongs to
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    // UUID-based filename (no extension) used as the base for all three file versions.
    // Example: "a1b2c3d4-e5f6-7890-abcd-ef1234567890"
    // We store the filename separately so we can delete all three files without parsing URLs.
    @Column(nullable = false)
    private String filename;

    // Full URL to the large version (max 1920px wide, 85% quality)
    // Used on: project detail page, lightbox
    @Column(nullable = false)
    private String largeUrl;

    // Full URL to the medium version (max 800px wide, 80% quality)
    // Used on: about page carousel
    @Column(nullable = false)
    private String mediumUrl;

    // Full URL to the thumbnail version (exactly 400x300px cropped, 75% quality)
    // Used on: project grid cards, homepage featured section
    @Column(nullable = false)
    private String thumbnailUrl;

    // The name of the file the user uploaded (for admin reference only, never exposed publicly)
    private String originalFilename;

    // Controls display order within a project's gallery (lower number = shown first)
    @Column(nullable = false)
    private Integer displayOrder = 0;

    // Whether this is the main/cover image for the project.
    // The hero image thumbnail appears in the project card on the grid.
    // Only one image per project should have isHero = true.
    @Column(nullable = false)
    private Boolean isHero = false;

    // When this image was uploaded - set automatically before insert
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // ---- Getters and Setters ----

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Project getProject() { return project; }
    public void setProject(Project project) { this.project = project; }

    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }

    public String getLargeUrl() { return largeUrl; }
    public void setLargeUrl(String largeUrl) { this.largeUrl = largeUrl; }

    public String getMediumUrl() { return mediumUrl; }
    public void setMediumUrl(String mediumUrl) { this.mediumUrl = mediumUrl; }

    public String getThumbnailUrl() { return thumbnailUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }

    public String getOriginalFilename() { return originalFilename; }
    public void setOriginalFilename(String originalFilename) { this.originalFilename = originalFilename; }

    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }

    public Boolean getIsHero() { return isHero; }
    public void setIsHero(Boolean isHero) { this.isHero = isHero; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
