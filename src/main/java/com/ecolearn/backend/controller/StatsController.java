package com.ecolearn.backend.controller;

import com.ecolearn.backend.repository.LessonRepository;
import com.ecolearn.backend.repository.ProjectRepository;
import com.ecolearn.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/stats")
public class StatsController {

    @Autowired private UserRepository userRepository;
    @Autowired private LessonRepository lessonRepository;
    @Autowired private ProjectRepository projectRepository;

    @GetMapping
    public ResponseEntity<Map<String, Long>> getStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("users", userRepository.count());
        stats.put("lessons", lessonRepository.count());
        stats.put("projects", projectRepository.count());
        return ResponseEntity.ok(stats);
    }
}
