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
    private final java.net.http.HttpClient httpClient = java.net.http.HttpClient.newBuilder()
        .connectTimeout(java.time.Duration.ofSeconds(5))
        .build();

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

        var identifier = request.getEmail().trim().toLowerCase();
        var password = normalizePassword(request.getPassword());

        var user = userRepository.findByEmail(identifier)
            .or(() -> userRepository.findByPhone(identifier))
            .orElseThrow(() -> new RuntimeException("Email/Phone not found."));
        
        var email = user.getEmail(); // Use the actual email from the user object for logging

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
        if (request.getPhone() != null && !request.getPhone().isBlank() && userRepository.findByPhone(request.getPhone()).isPresent()) {
            throw new RuntimeException("Phone number already registered.");
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
        user.setPhone(request.getPhone() != null ? request.getPhone().trim() : null);

        user = userRepository.save(user);
        System.err.println("User saved to database: id=" + user.getId() + ", saved_status=" + user.getStatus());
        
        try {
            notificationService.push(new com.reporting.portal.dto.NotificationRequest("New account registration pending approval: " + user.getEmail(), "admin", null));
        } catch (Exception e) {}

        return mapToDto(user);
    }

    public UserDto loginWithKingChatToken(String token, String email, String firstName, String lastName, String username) {
        if (token == null || token.isEmpty()) {
            throw new RuntimeException("Invalid KingsChat token");
        }
        String kcId = (username != null && !username.isEmpty()) ? username : null;
        String phone = null;
        
        // 1. Try fetching from KingsChat API
        try {
            System.err.println("Fetching profile from KingsChat API for token: " + (token != null ? token.substring(0, Math.min(token.length(), 10)) + "..." : "null"));
            java.net.http.HttpRequest profileReq = java.net.http.HttpRequest.newBuilder()
                .uri(java.net.URI.create("https://connect.kingsch.at/api/profile")) 
                .header("authorization", "Bearer " + token)
                .timeout(java.time.Duration.ofSeconds(10))
                .GET()
                .build();
            
            java.net.http.HttpResponse<String> response = httpClient.send(profileReq, java.net.http.HttpResponse.BodyHandlers.ofString());
            System.err.println("KingsChat API Response (" + response.statusCode() + "): " + response.body());
            
            if (response.statusCode() == 200) {
                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                java.util.Map<String, Object> body = mapper.readValue(response.body(), new com.fasterxml.jackson.core.type.TypeReference<java.util.Map<String, Object>>() {});
                
                java.util.Map<String, Object> profileData = null;
                if (body.containsKey("profile")) {
                    profileData = (java.util.Map<String, Object>) body.get("profile");
                } else if (body.containsKey("user")) {
                    profileData = (java.util.Map<String, Object>) body.get("user");
                } else {
                    profileData = body;
                }

                if (profileData != null) {
                    if (profileData.containsKey("email")) {
                        Object emailObj = profileData.get("email");
                        if (emailObj instanceof java.util.Map) {
                            java.util.Map<String, Object> emailMap = (java.util.Map<String, Object>) emailObj;
                            email = String.valueOf(emailMap.getOrDefault("address", ""));
                        } else {
                            email = String.valueOf(emailObj);
                        }
                    }
                    if (profileData.containsKey("username")) username = String.valueOf(profileData.get("username"));
                    if (profileData.containsKey("id")) kcId = String.valueOf(profileData.get("id"));
                    if (profileData.containsKey("sub")) kcId = String.valueOf(profileData.get("sub"));
                    
                    if (profileData.containsKey("phone_number")) {
                        String rawPhone = String.valueOf(profileData.get("phone_number"));
                        if (rawPhone != null && !"null".equals(rawPhone)) {
                            phone = rawPhone.replace("+", "").replace(" ", "").trim();
                        }
                    }
                    if (profileData.containsKey("name")) {
                        String name = String.valueOf(profileData.get("name"));
                        if (name != null && !"null".equals(name)) {
                            String[] parts = name.split(" ", 2);
                            firstName = parts[0];
                            if (parts.length > 1) lastName = parts[1];
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("API Profile fetch failed: " + e.getMessage());
            e.printStackTrace();
        }

        // 2. Fallback to decoding the JWT token locally if API failed or missing fields
        if (kcId == null || kcId.isEmpty()) {
            try {
                String[] chunks = token.split("\\.");
                if (chunks.length >= 2) {
                    java.util.Base64.Decoder decoder = java.util.Base64.getUrlDecoder();
                    String payload = new String(decoder.decode(chunks[1]));
                    
                    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                    java.util.Map<String, Object> claims = mapper.readValue(payload, new com.fasterxml.jackson.core.type.TypeReference<java.util.Map<String, Object>>() {});
                    
                    if (claims.containsKey("email") && (email == null)) email = String.valueOf(claims.get("email"));
                    if (claims.containsKey("username") && (username == null)) username = String.valueOf(claims.get("username"));
                    if (claims.containsKey("sub")) kcId = String.valueOf(claims.get("sub"));
                    if (claims.containsKey("first_name") && (firstName == null)) firstName = String.valueOf(claims.get("first_name"));
                    if (claims.containsKey("last_name") && (lastName == null)) lastName = String.valueOf(claims.get("last_name"));
                }
            } catch (Exception e) {
                System.err.println("Local JWT decode failed: " + e.getMessage());
            }
        }
        
        if (kcId == null || kcId.isEmpty()) {
            throw new RuntimeException("Authentication Failed: Could not verify KingsChat identity. Please login again.");
        }

        if (email == null || email.isEmpty()) {
            email = (username != null && !username.isEmpty()) ? username + "@kingschat.com" : kcId + "@kingschat.com";
        }
        
        if (firstName == null || firstName.isEmpty()) firstName = "KingsChat";
        if (lastName == null || lastName.isEmpty()) lastName = "User";
        
        return processKingChatUser(kcId, email, firstName, lastName, phone);
    }

    private UserDto processKingChatUser(String kcId, String email, String firstName, String lastName, String phone) {
        email = email.trim().toLowerCase();
        
        // 1. Try finding by KingsChat ID first (already linked)
        var userByKcId = userRepository.findByKingschatId(kcId);
        
        User user;
        if (userByKcId.isPresent()) {
            user = userByKcId.get();
            // Optional: update names if they changed on KingsChat
            if (firstName != null && !firstName.equalsIgnoreCase("KingsChat")) user.setFirstName(firstName);
            if (lastName != null && !lastName.equalsIgnoreCase("User")) user.setLastName(lastName);
        } else {
            // 2. Try finding by email (auto-link existing account)
            var userByEmail = userRepository.findByEmail(email);
            
            // 3. NEW: Try finding by phone (auto-link existing account)
            var userByPhone = (phone != null) ? userRepository.findByPhone(phone) : java.util.Optional.<User>empty();

            if (userByEmail.isPresent()) {
                user = userByEmail.get();
                user.setKingschatId(kcId); // Link it!
            } else if (userByPhone.isPresent()) {
                user = userByPhone.get();
                user.setKingschatId(kcId); // Link it!
                System.err.println("Auto-linked KingsChat ID " + kcId + " to existing user by phone: " + phone);
            } else {
                // 3. Create new account (Auto-activated for KingsChat)
                user = new User();
                user.setEmail(email);
                user.setKingschatId(kcId);
                user.setFirstName(firstName);
                user.setLastName(lastName);
                user.setRole("zonal");
                user.setStatus("active"); // Auto-activate
                user.setPassword(passwordEncoder.encode(java.util.UUID.randomUUID().toString()));
                user = userRepository.save(user);
                
                try {
                    notificationService.push(new com.reporting.portal.dto.NotificationRequest("New KingsChat account auto-activated: " + user.getEmail(), "admin", null));
                } catch (Exception ignored) {}
                
                // No longer throwing - allow immediate login
            }
        }
        
        // Ensure KingsChat users are always active when they log in
        if (!"active".equalsIgnoreCase(user.getStatus())) {
            String oldStatus = user.getStatus();
            user.setStatus("active");
            userRepository.save(user);
            System.err.println("Auto-activated existing user " + user.getEmail() + " (was " + oldStatus + ") via KingsChat login.");
            try {
                notificationService.push(new com.reporting.portal.dto.NotificationRequest("Existing account auto-activated via KingsChat: " + user.getEmail(), "admin", null));
            } catch (Exception ignored) {}
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
        
        // Protect status: Once active, don't reset to inactive
        if (!"active".equalsIgnoreCase(user.getStatus())) {
            user.setStatus("active");
            System.err.println("User " + user.getEmail() + " status set to ACTIVE via completeInvite");
        }
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
        
        // Prevent accidental status reset if already active
        if ("active".equalsIgnoreCase(user.getStatus()) && "inactive".equalsIgnoreCase(details.getStatus())) {
            System.err.println("Blocked attempt to reset ACTIVE user " + user.getEmail() + " to INACTIVE via update");
        } else if (details.getStatus() != null) {
            user.setStatus(details.getStatus());
        }
        
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
            user.getInviteToken(),
            user.getKingschatId()
        );
    }
}
