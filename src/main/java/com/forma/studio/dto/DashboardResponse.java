package com.forma.studio.dto;

/**
 * Summary statistics shown on the admin dashboard page.
 * This avoids the need for three separate API calls just to populate the dashboard.
 */
public class DashboardResponse {

    private long totalProjects;
    private long totalTeamMembers;
    private long unreadMessages;
    private long totalMessages;

    // ---- Getters and Setters ----

    public long getTotalProjects() { return totalProjects; }
    public void setTotalProjects(long totalProjects) { this.totalProjects = totalProjects; }

    public long getTotalTeamMembers() { return totalTeamMembers; }
    public void setTotalTeamMembers(long totalTeamMembers) { this.totalTeamMembers = totalTeamMembers; }

    public long getUnreadMessages() { return unreadMessages; }
    public void setUnreadMessages(long unreadMessages) { this.unreadMessages = unreadMessages; }

    public long getTotalMessages() { return totalMessages; }
    public void setTotalMessages(long totalMessages) { this.totalMessages = totalMessages; }
}
