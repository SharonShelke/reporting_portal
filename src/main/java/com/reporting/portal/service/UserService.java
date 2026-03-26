package com.reporting.portal.service;

import com.reporting.portal.dto.LoginRequest;
import com.reporting.portal.dto.RegisterRequest;
import com.reporting.portal.dto.UserDto;
import com.reporting.portal.entity.User;
import com.reporting.portal.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;
import java.util.UUID;
import com.reporting.portal.dto.InviteRequest;
import com.reporting.portal.dto.CompleteInviteRequest;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final AuditLogService auditLogService;

    public UserService(UserRepository userRepository, AuditLogService auditLogService) {
        this.userRepository = userRepository;
        this.auditLogService = auditLogService;
    }

    public UserDto login(LoginRequest request) {
        if (request == null || request.getEmail() == null || request.getPassword() == null) {
            throw new RuntimeException("Email and password are required.");
        }

        var email = request.getEmail().trim();
        var password = request.getPassword().trim();

        var user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Email not found."));

        boolean isActive = user.getStatus() == null || "active".equals(user.getStatus());

        if (!isActive) {
            try { auditLogService.logActivity(user.getEmail(), user.getId(), "Failed login attempt", "Auth", "—", "Failed", "Inactive account."); } catch (Exception e) {}
            throw new RuntimeException("Account is not active.");
        }

        if (user.getPassword() == null || !user.getPassword().equals(password)) {
            try { auditLogService.logActivity(user.getEmail(), user.getId(), "Failed login attempt", "Auth", "—", "Failed", "Wrong password."); } catch (Exception e) {}
            throw new RuntimeException("Incorrect password.");
        }
        
        try {
            auditLogService.logActivity(
                (user.getFirstName() != null ? user.getFirstName() : "") + " " + (user.getLastName() != null ? user.getLastName() : ""), 
                user.getId(), 
                "Login",  "Auth", "—", "Success", "User successfully logged into the platform."
            );
        } catch (Exception e) {}
        
        return mapToDto(user);
    }

    // MySQL data sometimes contains hidden whitespace/newlines that look like normal text in grids.
    // Normalizing prevents login failures in development.
    private String normalizePassword(String value) {
        if (value == null) return null;
        return value
            .replace("\u0000", "")     // strip null chars (if any)
            .replace('\u00A0', ' ')    // non-breaking space -> regular space
            .replace("\u200B", "")    // zero-width space
            .replace("\uFEFF", "")    // zero-width no-break space (BOM)
            .trim()
            .replaceAll("\\s+", "");   // remove all whitespace (space/tab/newline)
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
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword()); // Hash this in production
        user.setRole("zonal"); // Default role based on signup.jsx
        user.setStatus("active");
        
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
        return mapToDto(userRepository.save(request));
    }

    public String inviteUser(InviteRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists.");
        }
        
        var user = new User();
        user.setEmail(request.getEmail());
        user.setRole(request.getRole());
        user.setRegion(request.getRegion());
        user.setStatus("pending");
        user.setInviteToken(UUID.randomUUID().toString());
        
        userRepository.save(user);
        
        auditLogService.logActivity("System Administrator", 1L, "Created new user", "User Management", "—", user.getEmail(), "Admin generated an invitation link for " + user.getEmail() + " as a " + request.getRole() + ".");
        
        // Simulating email dispatch
        return "http://localhost:3000/invite?token=" + user.getInviteToken();
    }

    public UserDto completeInvite(CompleteInviteRequest request) {
        var user = userRepository.findByInviteToken(request.getToken())
                 .orElseThrow(() -> new RuntimeException("Invalid or expired invite token."));
                 
        String[] nameParts = request.getName() != null ? request.getName().trim().split(" ", 2) : new String[]{"User"};
        user.setFirstName(nameParts[0]);
        user.setLastName(nameParts.length > 1 ? nameParts[1] : "");
        user.setPassword(request.getPassword());
        user.setStatus("active");
        user.setInviteToken(null);
        
        userRepository.save(user);
        
        auditLogService.logActivity(request.getName(), user.getId(), "Completed Registration", "Auth", "pending", "active", "User completed their account setup and set their custom password.");
        
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
            user.getStatus() != null ? user.getStatus() : "active",
            user.getJoinedDate() != null ? user.getJoinedDate().format(formatter) : null
        );
    }
}
