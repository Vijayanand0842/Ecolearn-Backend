package com.ecolearn.backend.repository;

import com.ecolearn.backend.model.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LessonRepository extends JpaRepository<Lesson, Long> {}
