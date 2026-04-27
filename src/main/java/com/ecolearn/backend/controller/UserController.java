package com.ecolearn.backend.controller;

import com.ecolearn.backend.model.User;
import com.ecolearn.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.ecolearn.backend.service.EmailService;
import com.ecolearn.backend.service.OtpService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private EmailService emailService;

    @Autowired
    private OtpService otpService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginUser) {
        User user = userRepository.findByUsernameAndPassword(loginUser.getUsername(), loginUser.getPassword());
        if (user != null) {
            // Send email alert asynchronously (or synchronously)
            emailService.sendLoginAlert(user.getUsername());
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid username or password"));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User registerUser) {
        try {
            String role = "admin".equals(registerUser.getUsername()) ? "admin" : "user";
            registerUser.setRole(role);
            registerUser.setPoints(0);
            registerUser.setVerified(0);
            // email and name are automatically mapped from JSON if they exist in User class
            User savedUser = userRepository.save(registerUser);
            return ResponseEntity.ok(savedUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Username taken"));
        }
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @GetMapping("/{id}/details")
    public ResponseEntity<?> getUserDetails(@PathVariable Long id) {
        String modSql = "SELECT m.title, m.id as lesson_id FROM user_progress up JOIN modules m ON up.module_id = m.id WHERE up.user_id = ?";
        List<Map<String, Object>> modules = jdbcTemplate.queryForList(modSql, id);

        String projSql = "SELECT p.title, p.difficulty FROM project_progress pp JOIN projects p ON pp.project_id = p.id WHERE pp.user_id = ?";
        List<Map<String, Object>> projects = jdbcTemplate.queryForList(projSql, id);

        Map<String, Object> response = new HashMap<>();
        response.put("modules", modules);
        response.put("projects", projects);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/verify")
    @SuppressWarnings("null")
    public ResponseEntity<?> verifyUser(@PathVariable Long id) {
        return userRepository.findById(id).<ResponseEntity<?>>map(user -> {
            user.setVerified(1);
            userRepository.save(user);
            Map<String, Object> body = new HashMap<>();
            body.put("success", true);
            return ResponseEntity.ok(body);
        }).orElseGet(() -> {
            Map<String, Object> errorBody = new HashMap<>();
            errorBody.put("error", "User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorBody);
        });
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Email not registered"));
        }
        String otp = otpService.generateOtp(email);
        emailService.sendOtpEmail(email, otp);
        return ResponseEntity.ok(Map.of("success", true, "message", "OTP sent to email"));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        String otp = payload.get("otp");
        if (otpService.validateOtp(email, otp)) {
            return ResponseEntity.ok(Map.of("success", true));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Invalid or expired OTP"));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        String newPassword = payload.get("password");
        User user = userRepository.findByEmail(email);
        if (user != null) {
            user.setPassword(newPassword);
            userRepository.save(user);
            return ResponseEntity.ok(Map.of("success", true));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "User not found"));
    }
}
