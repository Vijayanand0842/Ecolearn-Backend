package com.ecolearn.backend.model;

import jakarta.persistence.*;
import java.io.Serializable;

class UserProgressId implements Serializable {
    private Long userId;
    private Long moduleId;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getModuleId() { return moduleId; }
    public void setModuleId(Long moduleId) { this.moduleId = moduleId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserProgressId that = (UserProgressId) o;
        return java.util.Objects.equals(userId, that.userId) &&
               java.util.Objects.equals(moduleId, that.moduleId);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(userId, moduleId);
    }
}

@Entity
@Table(name = "user_progress", uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "module_id"})})
@IdClass(UserProgressId.class)
public class UserProgress {
    @Id
    @Column(name="user_id")
    private Long userId;

    @Id
    @Column(name="module_id")
    private Long moduleId;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getModuleId() { return moduleId; }
    public void setModuleId(Long moduleId) { this.moduleId = moduleId; }
}
