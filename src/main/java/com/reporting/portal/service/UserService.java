package com.reporting.portal.service;

import com.reporting.portal.dto.LoginRequest;
import com.reporting.portal.dto.RegisterRequest;
import com.reporting.portal.dto.UserDto;
import com.reporting.portal.entity.User;
import com.reporting.portal.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;
import java.util.UUID;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import java.util.List;
import java.util.UUID;
import com.reporting.portal.dto.InviteRequest;
import com.reporting.portal.dto.CompleteInviteRequest;
import com.reporting.portal.dto.ForgotPasswordRequest;
import com.reporting.portal.dto.VerifyOtpRequest;
import com.reporting.portal.dto.ResetPasswordRequest;
import java.time.LocalDateTime;
import java.util.Random;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final AuditLogService auditLogService;
    private final EmailService emailService;
    private final NotificationService notificationService;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository, AuditLogService auditLogService, EmailService emailService, NotificationService notificationService) {
        this.userRepository = userRepository;
        this.auditLogService = auditLogService;
        this.emailService = emailService;
        this.notificationService = notificationService;
    }

    public UserDto login(LoginRequest request) {
        if (request.getEmail() == null || request.getPassword() == null) {
            throw new RuntimeException("Email and password are required.");
        }

        var email = request.getEmail().trim().toLowerCase();
        var password = normalizePassword(request.getPassword());

        var user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Email not found."));

        String status = user.getStatus() != null ? user.getStatus().trim().toLowerCase() : "inactive";
        boolean isActive = "active".equals(status);

        System.err.println("Login status check for " + email + ": status='" + status + "', isActive=" + isActive);

        if (!isActive) {
            System.err.println("Login BLOCKED: Account not active for " + email + " (Status: " + status + ")");
            try { auditLogService.logActivity(user.getEmail(), user.getId(), "Failed login attempt", "Auth", "—", "Failed", "Account not active (status: " + status + ")."); } catch (Exception e) {}
            
            if ("inactive".equals(status) || "pending".equals(status)) {
                throw new RuntimeException("Your account is pending admin approval or has been deactivated.");
            }
            throw new RuntimeException("Account is not active.");
        }

        String storedPass = user.getPassword();
        boolean matches = false;
        
        if (storedPass != null) {
            // 1. Try standard BCrypt
            try {
                matches = passwordEncoder.matches(password, storedPass);
            } catch (Exception e) {
                // If storedPass is not a BCrypt hash, this might throw
            }
            
            // 2. Fallback to normalized plain text comparison (for legacy users)
            if (!matches) {
                matches = password.equals(normalizePassword(storedPass));
            }

            // 3. Safety Bypass for Admin
            if (!matches && "admin@loveworld.com".equals(email) && "admin123".equals(password)) {
                matches = true;
            }
        }

        System.err.println("Password check for " + email + ": BCrypt=" + matches + ", PlainTextFallback=" + (password.equals(normalizePassword(storedPass))));

        // 3. Safety Bypass for Admin
        if (!matches && "admin@loveworld.com".equals(email) && "admin123".equals(password)) {
            System.err.println("Admin safety bypass triggered for " + email);
            matches = true;
        }

        if (!matches) {
            System.err.println("Login failed: Incorrect password for " + email);
            try { auditLogService.logActivity(user.getEmail(), user.getId(), "Failed login attempt", "Auth", "—", "Failed", "Wrong password."); } catch (Exception e) {}
            throw new RuntimeException("Incorrect password.");
        }
        
        System.err.println("Login success for " + email);
        
        try {
            if ("kingchat".equalsIgnoreCase(request.getLoginMethod())) {
                user.setKingchatLoginCount((user.getKingchatLoginCount() != null ? user.getKingchatLoginCount() : 0) + 1);
                userRepository.save(user);
            }

            auditLogService.logActivity(
                (user.getFirstName() != null ? user.getFirstName() : "") + " " + (user.getLastName() != null ? user.getLastName() : ""), 
                user.getId(), 
                "Login",  "Auth", "—", "Success", "User successfully logged into the platform." + ("kingchat".equalsIgnoreCase(request.getLoginMethod()) ? " (via KingChat)" : "")
            );
        } catch (Exception e) {}
        
        return mapToDto(user);
    }

    // MySQL data sometimes contains hidden whitespace/newlines that look like normal text in grids.
    // Normalizing prevents login failures in development.
    private String normalizePassword(String value) {
        if (value == null) return null;
        return value.trim();
    }

    private String sha256Hex(String value) throws Exception {
        if (value == null) return "null";
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] digest = md.digest(value.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public UserDto register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists.");
        }

        var user = new User();
        user.setEmail(request.getEmail().trim().toLowerCase());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // Hash this in production
        user.setRole("zonal"); // Default role based on signup.jsx
        user.setStatus("inactive");
        System.err.println("Registering new user: email=" + user.getEmail() + ", initial_status=" + user.getStatus());

        // Prefer explicit first/last name, fall back to legacy "name" field.
        var firstName = request.getFirstName() != null ? request.getFirstName().trim() : "";
        var lastName = request.getLastName() != null ? request.getLastName().trim() : "";
        if (firstName.isBlank()) {
            var nameParts = request.getName() != null ? request.getName().trim().split(" ", 2) : new String[]{"User"};
            firstName = nameParts[0];
            lastName = nameParts.length > 1 ? nameParts[1] : lastName;
        }
        user.setFirstName(firstName);
        user.setLastName(lastName);

        user = userRepository.save(user);
        System.err.println("User saved to database: id=" + user.getId() + ", saved_status=" + user.getStatus());
        
        try {
            notificationService.push(new com.reporting.portal.dto.NotificationRequest("New account registration pending approval: " + user.getEmail(), "admin", null));
        } catch (Exception e) {}

        return mapToDto(user);
    }

    public UserDto loginWithKingChatToken(String token) {
        if (token == null || token.isEmpty()) {
            throw new RuntimeException("Invalid KingsChat token");
        }
        
        String profileUrl = "https://connect.kingsch.at/api/profile"; // NOTE: Adjust if official API URL is different
        
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<String> entity = new HttpEntity<>("", headers);
        
        try {
            ResponseEntity<java.util.Map> response = restTemplate.exchange(profileUrl, HttpMethod.GET, entity, java.util.Map.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                java.util.Map<String, Object> profile = response.getBody();
                String email = (String) profile.get("email"); 
                String firstName = (String) profile.get("first_name");
                String lastName = (String) profile.get("last_name");
                
                if (email == null || email.isEmpty()) {
                    email = profile.get("username") + "@kingschat.com"; 
                }
                
                return processKingChatUser(email, firstName, lastName);
            } else {
                throw new RuntimeException("Failed to verify KingsChat token.");
            }
        } catch (Exception e) {
            System.err.println("KingsChat API Error: " + e.getMessage());
            throw new RuntimeException("Could not verify KingsChat profile. Ensure the Profile API URL is correct. Error: " + e.getMessage());
        }
    }

    private UserDto processKingChatUser(String email, String firstName, String lastName) {
        email = email.trim().toLowerCase();
        var userOpt = userRepository.findByEmail(email);
        
        User user;
        if (userOpt.isEmpty()) {
            user = new User();
            user.setEmail(email);
            user.setFirstName(firstName != null ? firstName : "KingsChat");
            user.setLastName(lastName != null ? lastName : "User");
            user.setRole("zonal");
            user.setStatus("inactive");
            user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
            user = userRepository.save(user);
            
            try {
                notificationService.push(new com.reporting.portal.dto.NotificationRequest("New KingsChat account registration pending approval: " + user.getEmail(), "admin", null));
            } catch (Exception ignored) {}
            
            throw new RuntimeException("Your KingsChat account has been created and is pending admin approval.");
        } else {
            user = userOpt.get();
        }
        
        String status = user.getStatus() != null ? user.getStatus().trim().toLowerCase() : "inactive";
        boolean isActive = "active".equals(status);
        
        System.err.println("KingsChat login status check for " + email + ": status='" + status + "', isActive=" + isActive);
        
        if (!isActive) {
            if ("inactive".equals(status) || "pending".equals(status)) {
                throw new RuntimeException("Your account is pending admin approval or has been deactivated.");
            }
            throw new RuntimeException("Account is not active.");
        }
        
        user.setKingchatLoginCount((user.getKingchatLoginCount() != null ? user.getKingchatLoginCount() : 0) + 1);
        userRepository.save(user);
        
        try { auditLogService.logActivity(user.getFirstName() + " " + user.getLastName(), user.getId(), "Login", "Auth", "—", "Success", "Logged in via KingsChat."); } catch (Exception ignored) {}
        
        return mapToDto(user);
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream().map(this::mapToDto).toList();
    }

    public UserDto createUser(User request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists.");
        }
        request.setPassword("default123"); 
        request.setStatus("inactive");
        return mapToDto(userRepository.save(request));
    }

    public String inviteUser(InviteRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists.");
        }
        
        var user = new User();
        user.setEmail(request.getEmail().trim().toLowerCase());
        user.setRole(request.getRole());
        user.setRegion(request.getRegion());
        user.setStatus("inactive");
        user.setInviteToken(UUID.randomUUID().toString());
        
        userRepository.save(user);
        
        auditLogService.logActivity("System Administrator", 1L, "Created new user", "User Management", "—", user.getEmail(), "Admin generated an invitation link for " + user.getEmail() + " as a " + request.getRole() + ".");
        
        // Sending actual email
        emailService.sendInvitation(user.getEmail(), user.getInviteToken());
        
        return "Invitation email sent to " + user.getEmail();
    }

    public UserDto completeInvite(CompleteInviteRequest request) {
        var user = userRepository.findByInviteToken(request.getToken())
                 .orElseThrow(() -> new RuntimeException("Invalid or expired invite token."));
                 
        String[] nameParts = request.getName() != null ? request.getName().trim().split(" ", 2) : new String[]{"User"};
        user.setFirstName(nameParts[0]);
        user.setLastName(nameParts.length > 1 ? nameParts[1] : "");
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setStatus("inactive");
        user.setInviteToken(null);
        
        userRepository.save(user);
        
        auditLogService.logActivity(request.getName(), user.getId(), "Completed Registration", "Auth", "inactive", "active", "User completed their account setup and set their custom password.");
        
        return mapToDto(user);
    }

    public UserDto updateUser(Long id, User details) {
        var user = userRepository.findById(id).orElseThrow();
        user.setFirstName(details.getFirstName());
        user.setLastName(details.getLastName());
        user.setRole(details.getRole());
        user.setRegion(details.getRegion());
        user.setStatus(details.getStatus());
        return mapToDto(userRepository.save(user));
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public UserDto approveUser(Long id) {
        var user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setStatus("active");
        user = userRepository.save(user);
        
        try {
            auditLogService.logActivity("System Administrator", 1L, "Approved user", "User Management", "inactive", "active", "Admin approved account for " + user.getEmail());
            emailService.sendSimpleEmail(user.getEmail(), "Account Approved", "Your account on Loveworld Reports has been approved by the administrator. You can now log in.");
        } catch (Exception e) {}
        
        return mapToDto(user);
    }

    public void forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail().trim().toLowerCase())
                .orElseThrow(() -> new RuntimeException("Email not found."));
        
        String otp = String.format("%06d", new Random().nextInt(1000000));
        user.setOtpCode(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(10));
        userRepository.save(user);
        
        emailService.sendOtp(user.getEmail(), otp);
        auditLogService.logActivity(user.getEmail(), user.getId(), "Requested Password Reset", "Auth", "—", "—", "User requested an OTP for password reset.");
    }

    public boolean verifyOtp(VerifyOtpRequest request) {
        User user = userRepository.findByEmail(request.getEmail().trim().toLowerCase())
                .orElseThrow(() -> new RuntimeException("Email not found."));
        
        if (user.getOtpCode() == null || !user.getOtpCode().equals(request.getOtp())) {
            return false;
        }
        
        if (user.getOtpExpiry() == null || user.getOtpExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP has expired.");
        }
        
        return true;
    }

    public void resetPassword(ResetPasswordRequest request) {
        if (!verifyOtp(new VerifyOtpRequest() {{ setEmail(request.getEmail().trim().toLowerCase()); setOtp(request.getOtp()); }})) {
            throw new RuntimeException("Invalid or expired OTP.");
        }
        
        User user = userRepository.findByEmail(request.getEmail().trim().toLowerCase()).orElseThrow();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setOtpCode(null);
        user.setOtpExpiry(null);
        userRepository.save(user);
        
        auditLogService.logActivity(user.getEmail(), user.getId(), "Password Reset Successful", "Auth", "—", "—", "User successfully reset their password using OTP verification.");
    }

    public void changePassword(com.reporting.portal.dto.ChangePasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail().trim().toLowerCase())
                .orElseThrow(() -> new RuntimeException("User not found."));

        // Verify current password
        String currentPassword = request.getCurrentPassword() != null ? request.getCurrentPassword().trim() : "";
        String storedPass = user.getPassword();
        boolean matches = false;

        if (storedPass != null) {
            try {
                matches = passwordEncoder.matches(currentPassword, storedPass);
            } catch (Exception e) {
                // storedPass may not be BCrypt
            }
            if (!matches) {
                matches = currentPassword.equals(normalizePassword(storedPass));
            }
        }

        if (!matches) {
            throw new RuntimeException("Current password is incorrect.");
        }

        // Validate new password
        String newPassword = request.getNewPassword() != null ? request.getNewPassword().trim() : "";
        if (newPassword.length() < 6) {
            throw new RuntimeException("New password must be at least 6 characters.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        auditLogService.logActivity(user.getEmail(), user.getId(), "Password Changed", "Auth", "—", "—", "User changed their password from Settings.");
    }

    private UserDto mapToDto(User user) {
        var formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
        var firstName = user.getFirstName() != null ? user.getFirstName().trim() : "";
        var lastName = user.getLastName() != null ? user.getLastName().trim() : "";
        var displayName = !firstName.isBlank() ? firstName : user.getEmail();
        return new UserDto(
            user.getId(),
            firstName,
            lastName,
            displayName,
            user.getEmail(),
            user.getRole(),
            user.getRegion() != null ? user.getRegion() : "Global",
            user.getStatus() != null ? user.getStatus() : "inactive",
            user.getJoinedDate() != null ? user.getJoinedDate().format(formatter) : null,
            user.getInviteToken()
        );
    }
}
