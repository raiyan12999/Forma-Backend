package com.forma.studio.dto;

import com.forma.studio.entity.ProjectCategory;
import java.time.LocalDateTime;
import java.util.List;

/**
 * The JSON shape returned when the frontend requests project data.
 * We use a separate DTO (data transfer object) instead of returning the entity directly
 * because the entity has JPA relationships (lazy loading) that can cause problems when
 * serializing to JSON. The DTO is a plain, safe object with exactly what the frontend needs.
 */
public class ProjectResponse {

    private Long id;
    private String title;
    private ProjectCategory category;
    private String description;
    private Integer year;
    private String status;
    private String client;
    private String location;
    private String landArea;
    private String builtArea;
    private String principalArchitects;
    private String projectType;
    private Boolean isFeatured;
    private Integer displayOrder;
    private LocalDateTime createdAt;

    // The hero image thumbnail URL - included in all list responses so the project card can show a photo
    // without needing to load all the images for every project
    private String heroThumbnailUrl;

    // Only included in the single-project detail response (not in list responses)
    // to avoid sending all image data for every project in a grid
    private List<ProjectImageResponse> images;

    // ---- Getters and Setters ----

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public ProjectCategory getCategory() { return category; }
    public void setCategory(ProjectCategory category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getClient() { return client; }
    public void setClient(String client) { this.client = client; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getLandArea() { return landArea; }
    public void setLandArea(String landArea) { this.landArea = landArea; }

    public String getBuiltArea() { return builtArea; }
    public void setBuiltArea(String builtArea) { this.builtArea = builtArea; }

    public String getPrincipalArchitects() { return principalArchitects; }
    public void setPrincipalArchitects(String principalArchitects) { this.principalArchitects = principalArchitects; }

    public String getProjectType() { return projectType; }
    public void setProjectType(String projectType) { this.projectType = projectType; }

    public Boolean getIsFeatured() { return isFeatured; }
    public void setIsFeatured(Boolean isFeatured) { this.isFeatured = isFeatured; }

    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getHeroThumbnailUrl() { return heroThumbnailUrl; }
    public void setHeroThumbnailUrl(String heroThumbnailUrl) { this.heroThumbnailUrl = heroThumbnailUrl; }

    public List<ProjectImageResponse> getImages() { return images; }
    public void setImages(List<ProjectImageResponse> images) { this.images = images; }
}
