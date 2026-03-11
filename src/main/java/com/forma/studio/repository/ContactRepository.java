package com.forma.studio.repository;

import com.forma.studio.entity.ContactSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Database access for ContactSubmission records.
 */
@Repository
public interface ContactRepository extends JpaRepository<ContactSubmission, Long> {

    /**
     * Returns all messages, newest first.
     * Used in the admin messages table.
     */
    List<ContactSubmission> findAllByOrderBySubmittedAtDesc();

    /**
     * Counts unread messages.
     * Used on the admin dashboard to show a notification badge.
     */
    long countByIsReadFalse();
}
