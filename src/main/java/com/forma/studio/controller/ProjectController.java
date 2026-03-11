package com.forma.studio.controller;

import com.forma.studio.dto.ProjectImageResponse;
import com.forma.studio.dto.ProjectResponse;
import com.forma.studio.entity.ProjectCategory;
import com.forma.studio.service.ProjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Handles public-facing project API requests.
 * These endpoints require no authentication — they're called by the public website.
 *
 * Base path: /api/projects
 */
@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    /**
     * GET /api/projects
     * Returns all projects in display order.
     * Each project includes its hero thumbnail URL for the grid card.
     * Frontend: projects.html, projects-category.html (all filter)
     */
    @GetMapping
    public ResponseEntity<List<ProjectResponse>> getAllProjects() {
        return ResponseEntity.ok(projectService.getAllProjects());
    }

    /**
     * GET /api/projects/featured
     * Returns only featured projects for the homepage.
     * Frontend: index.html "Featured Work" section
     */
    @GetMapping("/featured")
    public ResponseEntity<List<ProjectResponse>> getFeaturedProjects() {
        return ResponseEntity.ok(projectService.getFeaturedProjects());
    }

    /**
     * GET /api/projects/{id}
     * Returns a single project with all its images.
     * Frontend: project-detail.html?id=123
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> getProjectById(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.getProjectById(id));
    }

    /**
     * GET /api/projects/category/BUILDING_DESIGN
     * Returns projects filtered by category.
     * Frontend: projects-category.html?cat=architecture → fetches /api/projects/category/BUILDING_DESIGN
     *
     * Note: The category value in the URL must match the enum exactly (uppercase).
     * The frontend maps "architecture" → "BUILDING_DESIGN" etc.
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<List<ProjectResponse>> getProjectsByCategory(
            @PathVariable ProjectCategory category) {
        return ResponseEntity.ok(projectService.getProjectsByCategory(category));
    }

    /**
     * GET /api/projects/{id}/images
     * Returns all images for a project with all three URL sizes.
     * Frontend: lightbox gallery on project-detail.html
     */
    @GetMapping("/{id}/images")
    public ResponseEntity<List<ProjectImageResponse>> getProjectImages(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.getProjectImages(id));
    }
}
