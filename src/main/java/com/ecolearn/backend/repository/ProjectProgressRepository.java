package com.ecolearn.backend.repository;

import com.ecolearn.backend.model.ProjectProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProjectProgressRepository extends JpaRepository<ProjectProgress, Long> {
    List<ProjectProgress> findByUserId(Long userId);
    boolean existsByUserIdAndProjectId(Long userId, Long projectId);
    ProjectProgress findByUserIdAndProjectId(Long userId, Long projectId);
    
    @org.springframework.transaction.annotation.Transactional
    void deleteByProjectId(Long projectId);
}
