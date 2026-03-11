package com.forma.studio.dto;

import com.forma.studio.entity.ProjectCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * The JSON shape the admin sends when creating or updating a project.
 * The @NotBlank and @NotNull annotations cause Spring to reject invalid requests
 * before they even reach the service layer.
 */
public class ProjectRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotNull(message = "Category is required")
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

    // ---- Getters and Setters ----

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
}
