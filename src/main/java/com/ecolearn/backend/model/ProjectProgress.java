package com.ecolearn.backend.model;

import jakarta.persistence.*;
import java.io.Serializable;

class ProjectProgressId implements Serializable {
    private Long userId;
    private Long projectId;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProjectProgressId that = (ProjectProgressId) o;
        return java.util.Objects.equals(userId, that.userId) &&
               java.util.Objects.equals(projectId, that.projectId);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(userId, projectId);
    }
}

@Entity
@Table(name = "project_progress", uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "project_id"})})
@IdClass(ProjectProgressId.class)
public class ProjectProgress {
    @Id
    @Column(name="user_id")
    private Long userId;

    @Id
    @Column(name="project_id")
    private Long projectId;

    @Column(columnDefinition = "TEXT")
    private String proof;

    @Column(nullable = false)
    private String status = "PENDING";

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }

    public String getProof() { return proof; }
    public void setProof(String proof) { this.proof = proof; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
