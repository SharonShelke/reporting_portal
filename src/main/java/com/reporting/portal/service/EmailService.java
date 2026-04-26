package com.reporting.portal.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(String to, String subject, String body) {
        sendSimpleEmail(to, subject, body);
    }

    public void sendSimpleEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("sharonshelke7@gmail.com");
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Failed to send email: " + e.getMessage());
            throw new RuntimeException("Failed to send email. Please check SMTP configuration: " + e.getMessage());
        }
    }

    public void sendInvitation(String to, String token) {
        String link = "http://65.0.71.13/invite?token=" + token;
        String subject = "Account Invitation - Kingsforms";
        String body = "You have been invited to join Kingsforms. Please click the link below to complete your registration and set your password:\n\n" + link;
        sendEmail(to, subject, body);
    }

    public void sendOtp(String to, String otp) {
        String subject = "Your Password Reset OTP - Kingsforms";
        String body = "Your one-time password for resetting your password is: " + otp + "\n\nThis OTP will expire in 10 minutes.";
        sendEmail(to, subject, body);
    }
}
