package com.forma.studio.repository;

import com.forma.studio.entity.ProjectImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Database access for ProjectImage records.
 */
@Repository
public interface ProjectImageRepository extends JpaRepository<ProjectImage, Long> {

    /**
     * Returns all images for a specific project, ordered for display.
     * The hero image typically has displayOrder = 0 so it appears first.
     */
    List<ProjectImage> findByProjectIdOrderByDisplayOrderAsc(Long projectId);

    /**
     * Returns the hero image for a project, if one is set.
     * We use this to get the thumbnail URL for project grid cards.
     */
    Optional<ProjectImage> findByProjectIdAndIsHeroTrue(Long projectId);

    /**
     * Counts how many images a project has.
     * Used in admin panel to show image count.
     */
    long countByProjectId(Long projectId);
}
