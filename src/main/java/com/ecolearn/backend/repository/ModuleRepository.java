package com.ecolearn.backend.repository;

import com.ecolearn.backend.model.ModuleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ModuleRepository extends JpaRepository<ModuleEntity, Long> {
    List<ModuleEntity> findByLessonIdOrderByOrderIndexAsc(Long lessonId);
}
