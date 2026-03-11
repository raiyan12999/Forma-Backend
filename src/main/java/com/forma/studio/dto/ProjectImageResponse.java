package com.forma.studio.dto;

import java.time.LocalDateTime;

/**
 * The JSON shape for a single project image.
 * Always includes all three URL sizes so the frontend can pick the right one for each context.
 */
public class ProjectImageResponse {

    private Long id;
    private String largeUrl;
    private String mediumUrl;
    private String thumbnailUrl;
    private Integer displayOrder;
    private Boolean isHero;
    private LocalDateTime createdAt;

    // ---- Getters and Setters ----

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getLargeUrl() { return largeUrl; }
    public void setLargeUrl(String largeUrl) { this.largeUrl = largeUrl; }

    public String getMediumUrl() { return mediumUrl; }
    public void setMediumUrl(String mediumUrl) { this.mediumUrl = mediumUrl; }

    public String getThumbnailUrl() { return thumbnailUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }

    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }

    public Boolean getIsHero() { return isHero; }
    public void setIsHero(Boolean isHero) { this.isHero = isHero; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
