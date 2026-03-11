package com.forma.studio.repository;

import com.forma.studio.entity.Project;
import com.forma.studio.entity.ProjectCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Database access for Project records.
 * Spring Data JPA generates all the SQL automatically — we just define method names.
 */
@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    /**
     * Returns all projects in a specific category, sorted by display order.
     * Used by the category page: /projects-category.html?cat=architecture
     */
    List<Project> findByCategoryOrderByDisplayOrderAsc(ProjectCategory category);

    /**
     * Returns only featured projects, sorted by display order.
     * Used on the homepage "Featured Work" section.
     */
    List<Project> findByIsFeaturedTrueOrderByDisplayOrderAsc();

    /**
     * Returns all projects sorted by display order.
     * Used on the projects listing page.
     */
    List<Project> findAllByOrderByDisplayOrderAsc();
}
