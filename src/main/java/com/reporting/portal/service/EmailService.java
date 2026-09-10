package com.reporting.portal.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    
    @org.springframework.beans.factory.annotation.Value("${spring.mail.username}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(String to, String subject, String body) {
        sendSimpleEmail(to, subject, body);
    }

    public void sendSimpleEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
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
        String link = "https://65.2.153.58/invite?token=" + token;
        String subject = "Account Invitation - Kingsforms";
        String body = "You have been invited to join Kingsforms. Please click the link below to complete your registration and set your password:\n\n" + link;
        sendEmail(to, subject, body);
    }

    public void sendOtp(String to, String otp) {
        String subject = "Your Password Reset OTP - Kingsforms";
        String body = "Your one-time password for resetting your password is: " + otp + "\n\nThis OTP will expire in 10 minutes.";
        sendEmail(to, subject, body);
    }

    public void sendHtmlEmail(String to, String subject, String htmlBody) {
        try {
            jakarta.mail.internet.MimeMessage message = mailSender.createMimeMessage();
            org.springframework.mail.javamail.MimeMessageHelper helper = new org.springframework.mail.javamail.MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Failed to send HTML email: " + e.getMessage());
            throw new RuntimeException("Failed to send email. Please check SMTP configuration: " + e.getMessage());
        }
    }

    public void sendAdminApprovalRequest(String userEmail) {
        String to = "healingschool.intl.offices@gmail.com";
        String subject = "Action Required: User Approval - Healing School Reporting Portal";
        String htmlBody = "<html><body style='font-family: Arial, sans-serif; line-height: 1.6;'>"
                + "<h2>User Approval Required</h2>"
                + "<p>Please approve <a href='mailto:" + userEmail + "'>" + userEmail + "</a> on the Healing School Reporting Portal.</p>"
                + "<br/>"
                + "<a href='https://65.2.153.58/admin/users' style='background-color: #1d4ed8; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px; display: inline-block; font-weight: bold;'>Review & Approve User</a>"
                + "</body></html>";
        sendHtmlEmail(to, subject, htmlBody);
    }

    public void sendUserApprovalNotification(String toEmail) {
        String subject = "Account Approved - Healing School Reporting Portal";
        String htmlBody = "<html><body style='font-family: Arial, sans-serif; line-height: 1.6;'>"
                + "<h2>Account Approved</h2>"
                + "<p>Your account has been approved. Please log in to the Healing School Reporting Portal to access your account.</p>"
                + "<br/>"
                + "<a href='https://65.2.153.58/login' style='background-color: #16a34a; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px; display: inline-block; font-weight: bold;'>Login to Portal</a>"
                + "</body></html>";
        sendHtmlEmail(toEmail, subject, htmlBody);
    }
}
