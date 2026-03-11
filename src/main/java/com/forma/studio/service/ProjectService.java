package com.forma.studio.service;

import com.forma.studio.dto.*;
import com.forma.studio.entity.Project;
import com.forma.studio.entity.ProjectCategory;
import com.forma.studio.entity.ProjectImage;
import com.forma.studio.repository.ProjectImageRepository;
import com.forma.studio.repository.ProjectRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Handles all business logic for projects and project images.
 * The controllers are thin — they just call methods here and return results.
 */
@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectImageRepository projectImageRepository;
    private final ImageService imageService;

    // Constructor injection — we don't use @Autowired field injection because
    // constructor injection makes dependencies explicit and easier to test
    public ProjectService(ProjectRepository projectRepository,
                          ProjectImageRepository projectImageRepository,
                          ImageService imageService) {
        this.projectRepository = projectRepository;
        this.projectImageRepository = projectImageRepository;
        this.imageService = imageService;
    }

    // ============================================================
    // PUBLIC API — these methods serve the public-facing website
    // ============================================================

    /**
     * Returns all projects in display order.
     * Each project includes its hero thumbnail URL for use in the project grid cards.
     * We intentionally do NOT include all images here — that would be too much data.
     */
    public List<ProjectResponse> getAllProjects() {
        return projectRepository.findAllByOrderByDisplayOrderAsc()
            .stream()
            .map(this::toResponseWithHeroOnly)
            .collect(Collectors.toList());
    }

    /**
     * Returns all projects in a specific category.
     * Used by projects-category.html?cat=architecture etc.
     */
    public List<ProjectResponse> getProjectsByCategory(ProjectCategory category) {
        return projectRepository.findByCategoryOrderByDisplayOrderAsc(category)
            .stream()
            .map(this::toResponseWithHeroOnly)
            .collect(Collectors.toList());
    }

    /**
     * Returns only projects marked as featured.
     * Used on the homepage "Featured Work" section.
     */
    public List<ProjectResponse> getFeaturedProjects() {
        return projectRepository.findByIsFeaturedTrueOrderByDisplayOrderAsc()
            .stream()
            .map(this::toResponseWithHeroOnly)
            .collect(Collectors.toList());
    }

    /**
     * Returns a single project with ALL of its images.
     * Used on the project detail page (project-detail.html?id=123).
     */
    public ProjectResponse getProjectById(Long projectId) {
        Project project = findProjectOrThrow(projectId);
        return toResponseWithAllImages(project);
    }

    /**
     * Returns all images for a project.
     * Used by the lightbox gallery on the detail page.
     */
    public List<ProjectImageResponse> getProjectImages(Long projectId) {
        findProjectOrThrow(projectId); // Verify the project exists
        return projectImageRepository.findByProjectIdOrderByDisplayOrderAsc(projectId)
            .stream()
            .map(this::toImageResponse)
            .collect(Collectors.toList());
    }

    // ============================================================
    // ADMIN API — these methods are only called from admin endpoints
    // ============================================================

    /**
     * Creates a new project from the admin form.
     */
    public ProjectResponse createProject(ProjectRequest request) {
        Project project = new Project();
        applyRequestToProject(request, project);
        project = projectRepository.save(project);
        return toResponseWithHeroOnly(project);
    }

    /**
     * Updates an existing project's text details.
     * Images are managed separately.
     */
    public ProjectResponse updateProject(Long projectId, ProjectRequest request) {
        Project project = findProjectOrThrow(projectId);
        applyRequestToProject(request, project);
        project = projectRepository.save(project);
        return toResponseWithHeroOnly(project);
    }

    /**
     * Deletes a project and all its images (both DB records and files on disk).
     * Uses @Transactional so that if disk deletion fails partway through,
     * the database changes are rolled back and nothing ends up in an inconsistent state.
     */
    @Transactional
    public void deleteProject(Long projectId) {
        Project project = findProjectOrThrow(projectId);

        // First delete all image files from disk, then remove DB records via cascade
        List<ProjectImage> images = projectImageRepository.findByProjectIdOrderByDisplayOrderAsc(projectId);
        for (ProjectImage image : images) {
            imageService.deleteImage(image.getFilename());
        }

        // Deleting the project cascades to project_images table automatically
        projectRepository.delete(project);
    }

    /**
     * Flips the featured status of a project on/off.
     * Featured projects appear on the homepage.
     */
    public ProjectResponse toggleFeatured(Long projectId) {
        Project project = findProjectOrThrow(projectId);
        project.setIsFeatured(!project.getIsFeatured());
        project = projectRepository.save(project);
        return toResponseWithHeroOnly(project);
    }

    /**
     * Uploads an image, processes it into 3 sizes, and links it to a project.
     * The first image uploaded to a project automatically becomes the hero image.
     */
    public ProjectImageResponse uploadImage(Long projectId, MultipartFile file) throws IOException {
        Project project = findProjectOrThrow(projectId);

        // Resize the uploaded file and save all three versions to disk
        ImageService.ImageResult result = imageService.processAndSave(file);

        // Determine display order (new images go at the end)
        long existingCount = projectImageRepository.countByProjectId(projectId);

        // Create the database record
        ProjectImage image = new ProjectImage();
        image.setProject(project);
        image.setFilename(result.filename);
        image.setLargeUrl(result.largeUrl);
        image.setMediumUrl(result.mediumUrl);
        image.setThumbnailUrl(result.thumbnailUrl);
        image.setOriginalFilename(file.getOriginalFilename());
        image.setDisplayOrder((int) existingCount); // 0-indexed, so first image = order 0

        // If this is the first image for this project, make it the hero automatically
        // This means the grid card will immediately show a photo after the first upload
        boolean isFirstImage = existingCount == 0;
        image.setIsHero(isFirstImage);

        image = projectImageRepository.save(image);
        return toImageResponse(image);
    }

    /**
     * Deletes a single image — removes files from disk and the DB record.
     * If the deleted image was the hero, the next image in order automatically becomes the hero.
     */
    @Transactional
    public void deleteImage(Long imageId) {
        ProjectImage image = findImageOrThrow(imageId);
        Long projectId = image.getProject().getId();
        boolean wasHero = image.getIsHero();

        // Delete files from disk
        imageService.deleteImage(image.getFilename());

        // Remove DB record
        projectImageRepository.delete(image);

        // If this was the hero, promote the next image to hero so the project card still shows a photo
        if (wasHero) {
            List<ProjectImage> remaining = projectImageRepository.findByProjectIdOrderByDisplayOrderAsc(projectId);
            if (!remaining.isEmpty()) {
                remaining.get(0).setIsHero(true);
                projectImageRepository.save(remaining.get(0));
            }
        }
    }

    /**
     * Sets a specific image as the hero (cover) for its project.
     * First clears the hero flag from all other images in this project, then sets this one.
     */
    @Transactional
    public ProjectImageResponse setHeroImage(Long imageId) {
        ProjectImage targetImage = findImageOrThrow(imageId);
        Long projectId = targetImage.getProject().getId();

        // Clear hero flag from all images in this project
        List<ProjectImage> allImages = projectImageRepository.findByProjectIdOrderByDisplayOrderAsc(projectId);
        for (ProjectImage img : allImages) {
            img.setIsHero(false);
        }
        projectImageRepository.saveAll(allImages);

        // Set the target as hero
        targetImage.setIsHero(true);
        targetImage = projectImageRepository.save(targetImage);
        return toImageResponse(targetImage);
    }

    /**
     * Updates the display order of an image within its project's gallery.
     */
    public ProjectImageResponse reorderImage(Long imageId, Integer newOrder) {
        ProjectImage image = findImageOrThrow(imageId);
        image.setDisplayOrder(newOrder);
        image = projectImageRepository.save(image);
        return toImageResponse(image);
    }

    // ============================================================
    // PRIVATE HELPERS
    // ============================================================

    /**
     * Finds a project by ID or throws a 404 error if not found.
     * We use this throughout the service to keep error handling consistent.
     */
    private Project findProjectOrThrow(Long projectId) {
        return projectRepository.findById(projectId)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Project not found with id: " + projectId
            ));
    }

    /**
     * Finds an image by ID or throws a 404 error if not found.
     */
    private ProjectImage findImageOrThrow(Long imageId) {
        return projectImageRepository.findById(imageId)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Image not found with id: " + imageId
            ));
    }

    /**
     * Copies values from a request DTO into a Project entity.
     * Used for both create and update so we don't repeat this logic.
     * Null values in the request are ignored (partial updates are allowed).
     */
    private void applyRequestToProject(ProjectRequest request, Project project) {
        if (request.getTitle()               != null) project.setTitle(request.getTitle());
        if (request.getCategory()            != null) project.setCategory(request.getCategory());
        if (request.getDescription()         != null) project.setDescription(request.getDescription());
        if (request.getYear()                != null) project.setYear(request.getYear());
        if (request.getStatus()              != null) project.setStatus(request.getStatus());
        if (request.getClient()              != null) project.setClient(request.getClient());
        if (request.getLocation()            != null) project.setLocation(request.getLocation());
        if (request.getLandArea()            != null) project.setLandArea(request.getLandArea());
        if (request.getBuiltArea()           != null) project.setBuiltArea(request.getBuiltArea());
        if (request.getPrincipalArchitects() != null) project.setPrincipalArchitects(request.getPrincipalArchitects());
        if (request.getProjectType()         != null) project.setProjectType(request.getProjectType());
        if (request.getIsFeatured()          != null) project.setIsFeatured(request.getIsFeatured());
        if (request.getDisplayOrder()        != null) project.setDisplayOrder(request.getDisplayOrder());
    }

    /**
     * Converts a Project entity to a response DTO.
     * This version only includes the hero thumbnail URL — used for list/grid views.
     * We use thumbnail (not large) URL to keep the payload small.
     */
    private ProjectResponse toResponseWithHeroOnly(Project project) {
        ProjectResponse response = toBaseResponse(project);

        // Find the hero image and include only its thumbnail URL
        Optional<ProjectImage> heroImage = projectImageRepository.findByProjectIdAndIsHeroTrue(project.getId());
        heroImage.ifPresent(img -> response.setHeroThumbnailUrl(img.getThumbnailUrl()));

        return response;
    }

    /**
     * Converts a Project entity to a response DTO.
     * This version includes all images — used only on the detail page.
     */
    private ProjectResponse toResponseWithAllImages(Project project) {
        ProjectResponse response = toBaseResponse(project);

        List<ProjectImageResponse> images = projectImageRepository
            .findByProjectIdOrderByDisplayOrderAsc(project.getId())
            .stream()
            .map(this::toImageResponse)
            .collect(Collectors.toList());

        response.setImages(images);
        return response;
    }

    /**
     * Maps the common fields from a Project entity to a ProjectResponse DTO.
     * Called by both toResponseWithHeroOnly and toResponseWithAllImages.
     */
    private ProjectResponse toBaseResponse(Project project) {
        ProjectResponse response = new ProjectResponse();
        response.setId(project.getId());
        response.setTitle(project.getTitle());
        response.setCategory(project.getCategory());
        response.setDescription(project.getDescription());
        response.setYear(project.getYear());
        response.setStatus(project.getStatus());
        response.setClient(project.getClient());
        response.setLocation(project.getLocation());
        response.setLandArea(project.getLandArea());
        response.setBuiltArea(project.getBuiltArea());
        response.setPrincipalArchitects(project.getPrincipalArchitects());
        response.setProjectType(project.getProjectType());
        response.setIsFeatured(project.getIsFeatured());
        response.setDisplayOrder(project.getDisplayOrder());
        response.setCreatedAt(project.getCreatedAt());
        return response;
    }

    /**
     * Converts a ProjectImage entity to a ProjectImageResponse DTO.
     */
    private ProjectImageResponse toImageResponse(ProjectImage image) {
        ProjectImageResponse response = new ProjectImageResponse();
        response.setId(image.getId());
        response.setLargeUrl(image.getLargeUrl());
        response.setMediumUrl(image.getMediumUrl());
        response.setThumbnailUrl(image.getThumbnailUrl());
        response.setDisplayOrder(image.getDisplayOrder());
        response.setIsHero(image.getIsHero());
        response.setCreatedAt(image.getCreatedAt());
        return response;
    }
}
