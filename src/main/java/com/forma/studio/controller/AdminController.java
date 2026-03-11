package com.forma.studio.controller;

import com.forma.studio.dto.*;
import com.forma.studio.repository.ContactRepository;
import com.forma.studio.repository.ProjectRepository;
import com.forma.studio.repository.TeamMemberRepository;
import com.forma.studio.service.ContactService;
import com.forma.studio.service.ProjectService;
import com.forma.studio.service.TeamService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * Handles all admin API requests.
 * All endpoints here are protected by Spring Security — only authenticated users can access them.
 * See SecurityConfig for how authentication is configured.
 *
 * Base path: /api/admin
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final ProjectService projectService;
    private final TeamService teamService;
    private final ContactService contactService;
    private final ProjectRepository projectRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final ContactRepository contactRepository;

    public AdminController(ProjectService projectService,
                           TeamService teamService,
                           ContactService contactService,
                           ProjectRepository projectRepository,
                           TeamMemberRepository teamMemberRepository,
                           ContactRepository contactRepository) {
        this.projectService = projectService;
        this.teamService = teamService;
        this.contactService = contactService;
        this.projectRepository = projectRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.contactRepository = contactRepository;
    }

    // ============================================================
    // DASHBOARD
    // ============================================================

    /**
     * GET /api/admin/dashboard
     * Returns summary counts for the admin dashboard page.
     * One request instead of four separate calls.
     */
    @GetMapping("/dashboard")
    public ResponseEntity<DashboardResponse> getDashboard() {
        DashboardResponse dashboard = new DashboardResponse();
        dashboard.setTotalProjects(projectRepository.count());
        dashboard.setTotalTeamMembers(teamMemberRepository.count());
        dashboard.setUnreadMessages(contactRepository.countByIsReadFalse());
        dashboard.setTotalMessages(contactRepository.count());
        return ResponseEntity.ok(dashboard);
    }

    // ============================================================
    // PROJECTS — text details
    // ============================================================

    /**
     * POST /api/admin/projects
     * Creates a new project. Images are uploaded separately after creation.
     */
    @PostMapping("/projects")
    public ResponseEntity<ProjectResponse> createProject(@Valid @RequestBody ProjectRequest request) {
        ProjectResponse response = projectService.createProject(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * PUT /api/admin/projects/{id}
     * Updates a project's text details. Does not affect images.
     */
    @PutMapping("/projects/{id}")
    public ResponseEntity<ProjectResponse> updateProject(
            @PathVariable Long id,
            @Valid @RequestBody ProjectRequest request) {
        return ResponseEntity.ok(projectService.updateProject(id, request));
    }

    /**
     * DELETE /api/admin/projects/{id}
     * Deletes a project and ALL its images (files on disk + DB records).
     * This cannot be undone.
     */
    @DeleteMapping("/projects/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build(); // 204 No Content = success with no body
    }

    /**
     * PUT /api/admin/projects/{id}/featured
     * Toggles the featured status of a project on or off.
     * Featured projects appear in the homepage "Featured Work" section.
     */
    @PutMapping("/projects/{id}/featured")
    public ResponseEntity<ProjectResponse> toggleFeatured(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.toggleFeatured(id));
    }

    // ============================================================
    // IMAGES — upload and manage project photos
    // ============================================================

    /**
     * POST /api/admin/projects/{id}/images
     * Uploads an image for a project. Accepts multipart form data.
     * The file is automatically resized into 3 versions — original is never stored.
     * Returns the URLs for all three versions.
     *
     * Form field name must be "file".
     */
    @PostMapping("/projects/{id}/images")
    public ResponseEntity<ProjectImageResponse> uploadImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) throws IOException {
        ProjectImageResponse response = projectService.uploadImage(id, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * DELETE /api/admin/images/{imageId}
     * Deletes an image — removes all 3 file versions from disk and the DB record.
     */
    @DeleteMapping("/images/{imageId}")
    public ResponseEntity<Void> deleteImage(@PathVariable Long imageId) {
        projectService.deleteImage(imageId);
        return ResponseEntity.noContent().build();
    }

    /**
     * PUT /api/admin/images/{imageId}/set-hero
     * Sets this image as the hero (cover) for its project.
     * The hero thumbnail is shown on the project grid card.
     */
    @PutMapping("/images/{imageId}/set-hero")
    public ResponseEntity<ProjectImageResponse> setHeroImage(@PathVariable Long imageId) {
        return ResponseEntity.ok(projectService.setHeroImage(imageId));
    }

    /**
     * PUT /api/admin/images/{imageId}/reorder
     * Updates the display order of an image within its project gallery.
     * Body: { "order": 2 }
     */
    @PutMapping("/images/{imageId}/reorder")
    public ResponseEntity<ProjectImageResponse> reorderImage(
            @PathVariable Long imageId,
            @RequestBody ReorderRequest request) {
        return ResponseEntity.ok(projectService.reorderImage(imageId, request.getOrder()));
    }

    // ============================================================
    // TEAM MEMBERS
    // ============================================================

    /**
     * POST /api/admin/team
     * Creates a new team member.
     * Accepts multipart form data: "data" field (JSON) + optional "photo" field (image file).
     *
     * Why multipart here? Because we need to send both JSON text AND a file in one request.
     * The admin form submits both at once.
     */
    @PostMapping(value = "/team", consumes = "multipart/form-data")
    public ResponseEntity<TeamMemberResponse> createTeamMember(
            @RequestPart("data") @Valid TeamMemberRequest request,
            @RequestPart(value = "photo", required = false) MultipartFile photo) throws IOException {
        TeamMemberResponse response = teamService.createTeamMember(request, photo);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * PUT /api/admin/team/{id}
     * Updates a team member's text details (name, role, bio etc).
     * Does not change their photo — use the /photo endpoint for that.
     */
    @PutMapping("/team/{id}")
    public ResponseEntity<TeamMemberResponse> updateTeamMember(
            @PathVariable Long id,
            @Valid @RequestBody TeamMemberRequest request) {
        return ResponseEntity.ok(teamService.updateTeamMember(id, request));
    }

    /**
     * PUT /api/admin/team/{id}/photo
     * Replaces a team member's photo. Accepts multipart with "file" field.
     * Automatically deletes the old photo files before saving the new ones.
     */
    @PutMapping(value = "/team/{id}/photo", consumes = "multipart/form-data")
    public ResponseEntity<TeamMemberResponse> updateTeamMemberPhoto(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(teamService.updateTeamMemberPhoto(id, file));
    }

    /**
     * DELETE /api/admin/team/{id}
     * Deletes a team member and their photo files.
     */
    @DeleteMapping("/team/{id}")
    public ResponseEntity<Void> deleteTeamMember(@PathVariable Long id) {
        teamService.deleteTeamMember(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * PUT /api/admin/team/{id}/reorder
     * Updates the display order of a team member.
     * Body: { "order": 3 }
     */
    @PutMapping("/team/{id}/reorder")
    public ResponseEntity<TeamMemberResponse> reorderTeamMember(
            @PathVariable Long id,
            @RequestBody ReorderRequest request) {
        return ResponseEntity.ok(teamService.reorderTeamMember(id, request.getOrder()));
    }

    // ============================================================
    // CONTACT MESSAGES
    // ============================================================

    /**
     * GET /api/admin/contact
     * Returns all contact form submissions, newest first.
     */
    @GetMapping("/contact")
    public ResponseEntity<List<ContactResponse>> getAllMessages() {
        return ResponseEntity.ok(contactService.getAllSubmissions());
    }

    /**
     * PUT /api/admin/contact/{id}/read
     * Marks a message as read. The unread count on the dashboard decreases.
     */
    @PutMapping("/contact/{id}/read")
    public ResponseEntity<ContactResponse> markMessageAsRead(@PathVariable Long id) {
        return ResponseEntity.ok(contactService.markAsRead(id));
    }

    // ============================================================
    // INNER CLASS — simple request body for reorder endpoints
    // ============================================================

    /**
     * Simple wrapper for the reorder request body: { "order": 2 }
     * Using an inner class here to avoid creating a whole separate file for one field.
     */
    public static class ReorderRequest {
        private Integer order;
        public Integer getOrder() { return order; }
        public void setOrder(Integer order) { this.order = order; }
    }
}
