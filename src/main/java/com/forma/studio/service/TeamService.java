package com.forma.studio.service;

import com.forma.studio.dto.TeamMemberRequest;
import com.forma.studio.dto.TeamMemberResponse;
import com.forma.studio.entity.TeamMember;
import com.forma.studio.repository.TeamMemberRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles all business logic for team members.
 */
@Service
public class TeamService {

    private final TeamMemberRepository teamMemberRepository;
    private final ImageService imageService;

    public TeamService(TeamMemberRepository teamMemberRepository, ImageService imageService) {
        this.teamMemberRepository = teamMemberRepository;
        this.imageService = imageService;
    }

    // ============================================================
    // PUBLIC API
    // ============================================================

    /**
     * Returns all team members in display order.
     * Used to render the full Team page.
     */
    public List<TeamMemberResponse> getAllTeamMembers() {
        return teamMemberRepository.findAllByOrderByDisplayOrderAsc()
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    /**
     * Returns only partners (isPartner = true).
     * Partners are shown in the prominent bio section at the top of the Team page.
     */
    public List<TeamMemberResponse> getPartners() {
        return teamMemberRepository.findByIsPartnerTrueOrderByDisplayOrderAsc()
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    // ============================================================
    // ADMIN API
    // ============================================================

    /**
     * Creates a new team member with an optional photo.
     * Photo upload is optional — a member can be added without a photo and photo added later.
     */
    public TeamMemberResponse createTeamMember(TeamMemberRequest request, MultipartFile photo) throws IOException {
        TeamMember member = new TeamMember();
        applyRequestToMember(request, member);

        // Upload and resize the photo if one was provided
        if (photo != null && !photo.isEmpty()) {
            applyPhotoToMember(photo, member);
        }

        member = teamMemberRepository.save(member);
        return toResponse(member);
    }

    /**
     * Updates a team member's text details (not their photo).
     */
    public TeamMemberResponse updateTeamMember(Long memberId, TeamMemberRequest request) {
        TeamMember member = findMemberOrThrow(memberId);
        applyRequestToMember(request, member);
        member = teamMemberRepository.save(member);
        return toResponse(member);
    }

    /**
     * Replaces a team member's photo.
     * Deletes the old photo files from disk before saving the new ones.
     */
    public TeamMemberResponse updateTeamMemberPhoto(Long memberId, MultipartFile photo) throws IOException {
        TeamMember member = findMemberOrThrow(memberId);

        // Delete old photo files if they exist
        if (member.getPhotoFilename() != null) {
            imageService.deleteImage(member.getPhotoFilename());
        }

        // Save new photo
        applyPhotoToMember(photo, member);
        member = teamMemberRepository.save(member);
        return toResponse(member);
    }

    /**
     * Deletes a team member and their photo files.
     */
    public void deleteTeamMember(Long memberId) {
        TeamMember member = findMemberOrThrow(memberId);

        // Delete photo files from disk if they exist
        if (member.getPhotoFilename() != null) {
            imageService.deleteImage(member.getPhotoFilename());
        }

        teamMemberRepository.delete(member);
    }

    /**
     * Updates the display order of a team member.
     */
    public TeamMemberResponse reorderTeamMember(Long memberId, Integer newOrder) {
        TeamMember member = findMemberOrThrow(memberId);
        member.setDisplayOrder(newOrder);
        member = teamMemberRepository.save(member);
        return toResponse(member);
    }

    // ============================================================
    // PRIVATE HELPERS
    // ============================================================

    /**
     * Finds a team member by ID or throws a 404 error.
     */
    private TeamMember findMemberOrThrow(Long memberId) {
        return teamMemberRepository.findById(memberId)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Team member not found with id: " + memberId
            ));
    }

    /**
     * Copies values from a request DTO into a TeamMember entity.
     * Used for both create and update. Null values are ignored (partial update allowed).
     */
    private void applyRequestToMember(TeamMemberRequest request, TeamMember member) {
        if (request.getName()         != null) member.setName(request.getName());
        if (request.getRole()         != null) member.setRole(request.getRole());
        if (request.getCredentials()  != null) member.setCredentials(request.getCredentials());
        if (request.getBio()          != null) member.setBio(request.getBio());
        if (request.getIsPartner()    != null) member.setIsPartner(request.getIsPartner());
        if (request.getDisplayOrder() != null) member.setDisplayOrder(request.getDisplayOrder());
    }

    /**
     * Processes a photo upload and sets the URLs on the team member entity.
     * Team member photos use the "large" size for bio cards and "medium" for team grid.
     */
    private void applyPhotoToMember(MultipartFile photo, TeamMember member) throws IOException {
        ImageService.ImageResult result = imageService.processAndSave(photo);
        member.setPhotoFilename(result.filename);
        member.setPhotoLargeUrl(result.largeUrl);  // Used in partner bio cards
        member.setPhotoMediumUrl(result.mediumUrl); // Used in team grid cards
    }

    /**
     * Converts a TeamMember entity to a TeamMemberResponse DTO.
     */
    private TeamMemberResponse toResponse(TeamMember member) {
        TeamMemberResponse response = new TeamMemberResponse();
        response.setId(member.getId());
        response.setName(member.getName());
        response.setRole(member.getRole());
        response.setCredentials(member.getCredentials());
        response.setBio(member.getBio());
        response.setPhotoLargeUrl(member.getPhotoLargeUrl());
        response.setPhotoMediumUrl(member.getPhotoMediumUrl());
        response.setIsPartner(member.getIsPartner());
        response.setDisplayOrder(member.getDisplayOrder());
        response.setCreatedAt(member.getCreatedAt());
        return response;
    }
}
