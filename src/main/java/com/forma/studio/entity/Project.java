package com.forma.studio.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single architecture project in the portfolio.
 * Maps to the "projects" table in the database.
 */
@Entity
@Table(name = "projects")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The display title, e.g. "Purbachal Convention Centre"
    @Column(nullable = false)
    private String title;

    // Which category this project belongs to
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProjectCategory category;

    // Long-form description shown on the project detail page
    @Column(columnDefinition = "TEXT")
    private String description;

    // The year the project was completed or presented
    private Integer year;

    // e.g. "2nd Prize - Open Competition", "Built", "In Progress"
    private String status;

    // Who commissioned the project, e.g. "RAJUK"
    private String client;

    // Where the project is located, e.g. "Purbachal, Dhaka"
    private String location;

    // Land area in sqm, e.g. "36,981 sqm"
    private String landArea;

    // Total built area in sqm, e.g. "41,463 sqm + 3 basements"
    private String builtArea;

    // Comma-separated list of architects who led the project
    private String principalArchitects;

    // Type of project, e.g. "Civic — Convention Centre"
    private String projectType;

    // Whether this project appears on the homepage featured section
    @Column(nullable = false)
    private Boolean isFeatured = false;

    // Controls the order projects appear in lists (lower number = shown first)
    @Column(nullable = false)
    private Integer displayOrder = 0;

    // When this record was created - set automatically before insert
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // All images belonging to this project. Cascade means deleting a project also deletes its images.
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("displayOrder ASC")
    private List<ProjectImage> images = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

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

    public List<ProjectImage> getImages() { return images; }
    public void setImages(List<ProjectImage> images) { this.images = images; }
}
