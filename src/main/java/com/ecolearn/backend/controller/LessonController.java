package com.ecolearn.backend.controller;

import com.ecolearn.backend.model.*;
import com.ecolearn.backend.repository.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class LessonController {

    @Autowired private LessonRepository lessonRepository;
    @Autowired private PageRepository pageRepository;
    @Autowired private QuizRepository quizRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private UserProgressRepository userProgressRepository;
    @Autowired private JdbcTemplate jdbcTemplate;

    @GetMapping("/lessons")
    public ResponseEntity<List<Lesson>> getAllLessons() {
        return ResponseEntity.ok(lessonRepository.findAll());
    }

    @GetMapping("/lessons/{id}/modules")
    public ResponseEntity<List<Map<String, Object>>> getModulesForLesson(@PathVariable Long id, @RequestParam(required = false) Long userId) {
        String sql = "SELECT m.*, CASE WHEN up.module_id IS NOT NULL THEN 1 ELSE 0 END as completed " +
                     "FROM modules m LEFT JOIN user_progress up ON m.id = up.module_id AND up.user_id = ? " +
                     "WHERE m.lesson_id = ? ORDER BY m.order_index ASC";
        List<Map<String, Object>> modules = jdbcTemplate.queryForList(sql, userId, id);
        return ResponseEntity.ok(modules);
    }

    @GetMapping("/modules/{id}/pages")
    public ResponseEntity<List<Page>> getPagesForModule(@PathVariable Long id) {
        return ResponseEntity.ok(pageRepository.findByModuleIdOrderByPageNumberAsc(id));
    }

    @GetMapping("/modules/{id}/quiz")
    public ResponseEntity<?> getQuizForModule(@PathVariable Long id) {
        Optional<Quiz> quizOpt = quizRepository.findByModuleId(id);
        if (quizOpt.isPresent()) {
            Quiz quiz = quizOpt.get();
            ObjectMapper mapper = new ObjectMapper();
            try {
                Object questions = mapper.readValue(quiz.getQuestionsJson(), Object.class);
                quiz.setQuestions(questions);
                quiz.setQuestionsJson(null);
                return ResponseEntity.ok(quiz);
            } catch (JsonProcessingException e) {
                return ResponseEntity.status(500).body(Map.of("error", "Error parsing quiz JSON"));
            }
        }
        return ResponseEntity.status(404).body(Map.of("error", "Quiz not found"));
    }

    @PostMapping("/modules/{id}/complete")
    public ResponseEntity<?> completeModule(@PathVariable Long id, @RequestBody Map<String, Long> payload) {
        Long userId = payload.get("userId");
        if (userId != null && !userProgressRepository.existsByUserIdAndModuleId(userId, id)) {
            UserProgress up = new UserProgress();
            up.setUserId(userId);
            up.setModuleId(id);
            userProgressRepository.save(up);

            userRepository.findById(userId).ifPresent(user -> {
                user.setPoints(user.getPoints() + 50);
                userRepository.save(user);
            });
        }
        return ResponseEntity.ok(Map.of("success", true));
    }
}
