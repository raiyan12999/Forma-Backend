package com.forma.studio.repository;

import com.forma.studio.entity.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Database access for TeamMember records.
 */
@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {

    /**
     * Returns all team members sorted by their display order.
     * Used on the full Team page.
     */
    List<TeamMember> findAllByOrderByDisplayOrderAsc();

    /**
     * Returns only partners (founding/senior members shown prominently at the top of the Team page).
     * Sorted by display order so their position can be controlled from the admin panel.
     */
    List<TeamMember> findByIsPartnerTrueOrderByDisplayOrderAsc();
}
