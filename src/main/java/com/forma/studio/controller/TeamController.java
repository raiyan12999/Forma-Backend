package com.forma.studio.controller;

import com.forma.studio.dto.TeamMemberResponse;
import com.forma.studio.service.TeamService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Handles public-facing team member API requests.
 * These endpoints require no authentication.
 *
 * Base path: /api/team
 */
@RestController
@RequestMapping("/api/team")
public class TeamController {

    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    /**
     * GET /api/team
     * Returns all team members in display order.
     * Frontend: team.html — renders both the partner bio section and the team grid
     */
    @GetMapping
    public ResponseEntity<List<TeamMemberResponse>> getAllTeamMembers() {
        return ResponseEntity.ok(teamService.getAllTeamMembers());
    }

    /**
     * GET /api/team/partners
     * Returns only partners (the founding/senior architects shown prominently).
     * Used if you want to load partners separately from the rest of the team.
     */
    @GetMapping("/partners")
    public ResponseEntity<List<TeamMemberResponse>> getPartners() {
        return ResponseEntity.ok(teamService.getPartners());
    }
}
