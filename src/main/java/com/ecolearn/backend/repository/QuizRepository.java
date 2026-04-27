package com.ecolearn.backend.repository;

import com.ecolearn.backend.model.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
    Optional<Quiz> findByModuleId(Long moduleId);
}
