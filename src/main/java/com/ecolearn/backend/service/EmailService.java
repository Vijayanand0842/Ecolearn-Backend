package com.ecolearn.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendLoginAlert(String username) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("vijaygrd0803@gmail.com");
        message.setSubject("EcoLearn Security Alert: New Login");
        message.setText("Hello Admin,\n\nA new login was detected on the EcoLearn platform.\n\nUsername: " + username + "\nTime: " + new Date().toString() + "\n\nIf this was not expected, please check the system logs.");
        message.setFrom("vijaygrd0803@gmail.com");

        try {
            mailSender.send(message);
            System.out.println("Login alert email sent successfully to Admin.");
        } catch (Exception e) {
            System.err.println("Failed to send login alert email: " + e.getMessage());
        }
    }
    public void sendOtpEmail(String toEmail, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("EcoLearn Security: Password Reset OTP");
        message.setText("Your OTP for password reset is: " + otp + "\n\nThis OTP will expire in 2 minutes.");
        message.setFrom("vijaygrd0803@gmail.com");

        try {
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Failed to send OTP email: " + e.getMessage());
        }
    }

    public void sendProjectCompletionToUser(String toEmail, String projectTitle) {
        if (toEmail == null || toEmail.isEmpty()) return;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("EcoLearn: Project Completed!");
        message.setText("Congratulations! You have successfully submitted proof for: " + projectTitle + ".\nYou earned 100 Eco-Points.");
        message.setFrom("vijaygrd0803@gmail.com");

        try {
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Failed to send user completion email: " + e.getMessage());
        }
    }

    public void sendProjectCompletionToAdmin(String userEmail, String projectTitle, String proof) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("vijaygrd0803@gmail.com"); // Admin email
        message.setSubject("EcoLearn: New Project Completion Submission");
        message.setText("User " + userEmail + " completed project: " + projectTitle + "\n\nProof submitted:\n" + proof);
        message.setFrom("vijaygrd0803@gmail.com");

        try {
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Failed to send admin notification email: " + e.getMessage());
        }
    }
}
