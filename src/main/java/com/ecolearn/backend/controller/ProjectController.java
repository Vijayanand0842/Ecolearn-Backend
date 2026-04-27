package com.ecolearn.backend.controller;

import com.ecolearn.backend.model.Project;
import com.ecolearn.backend.model.ProjectProgress;
import com.ecolearn.backend.repository.ProjectProgressRepository;
import com.ecolearn.backend.repository.ProjectRepository;
import com.ecolearn.backend.repository.UserRepository;
import com.ecolearn.backend.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;


import java.util.Map;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    @Autowired private ProjectRepository projectRepository;
    @Autowired private ProjectProgressRepository projectProgressRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private JdbcTemplate jdbcTemplate;
    @Autowired private EmailService emailService;

    @GetMapping
    public ResponseEntity<?> getProjects() {
        return ResponseEntity.ok(projectRepository.findAll());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getAllProjects(@PathVariable Long userId) {
        String sql = "SELECT p.*, pp.status " +
                     "FROM projects p LEFT JOIN project_progress pp ON p.id = pp.project_id AND pp.user_id = ?";
        return ResponseEntity.ok(jdbcTemplate.queryForList(sql, userId));
    }

    @GetMapping("/pending")
    public ResponseEntity<?> getPendingApprovals() {
        String sql = "SELECT pp.user_id as userId, u.username, pp.project_id as projectId, p.title, pp.proof " +
                     "FROM project_progress pp " +
                     "JOIN users u ON pp.user_id = u.id " +
                     "JOIN projects p ON pp.project_id = p.id " +
                     "WHERE pp.status = 'PENDING'";
        return ResponseEntity.ok(jdbcTemplate.queryForList(sql));
    }

    @PostMapping
    @SuppressWarnings("null")
    public ResponseEntity<?> createProject(@RequestBody Project project) {
        Project saved = projectRepository.save(project);
        return ResponseEntity.ok(Map.of("success", true, "id", saved.getId()));
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<?> completeProject(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
        Long userId = payload.get("userId") != null ? Long.valueOf(payload.get("userId").toString()) : null;
        String proof = payload.get("proof") != null ? payload.get("proof").toString() : "";

        if (userId != null && !projectProgressRepository.existsByUserIdAndProjectId(userId, id)) {
            ProjectProgress pp = new ProjectProgress();
            pp.setUserId(userId);
            pp.setProjectId(id);
            pp.setProof(proof);
            pp.setStatus("PENDING");
            projectProgressRepository.save(pp);
            return ResponseEntity.ok(Map.of("success", true, "message", "Project submitted for approval"));
        }
        return ResponseEntity.badRequest().body(Map.of("error", "Invalid or already submitted"));
    }

    @PostMapping("/{projectId}/approve/{userId}")
    @SuppressWarnings("null")
    public ResponseEntity<?> approveProject(@PathVariable Long projectId, @PathVariable Long userId) {
        ProjectProgress pp = projectProgressRepository.findByUserIdAndProjectId(userId, projectId);
        if (pp != null && "PENDING".equals(pp.getStatus())) {
            pp.setStatus("APPROVED");
            projectProgressRepository.save(pp);

            userRepository.findById(userId).ifPresent(user -> {
                user.setPoints(user.getPoints() + 100);
                userRepository.save(user);

                projectRepository.findById(projectId).ifPresent(project -> {
                    if (user.getEmail() != null) {
                        emailService.sendProjectCompletionToUser(user.getEmail(), project.getTitle());
                        emailService.sendProjectCompletionToAdmin(user.getEmail(), project.getTitle(), pp.getProof());
                    }
                });
            });
            return ResponseEntity.ok(Map.of("success", true));
        }
        return ResponseEntity.badRequest().body(Map.of("error", "Not found or already approved"));
    }

    @DeleteMapping("/{id}")
    @SuppressWarnings("null")
    public ResponseEntity<?> deleteProject(@PathVariable Long id) {
        if (projectRepository.existsById(id)) {
            projectProgressRepository.deleteByProjectId(id);
            projectRepository.deleteById(id);
            return ResponseEntity.ok(Map.of("success", true));
        }
        return ResponseEntity.status(404).body(Map.of("error", "Project not found"));
    }
}
