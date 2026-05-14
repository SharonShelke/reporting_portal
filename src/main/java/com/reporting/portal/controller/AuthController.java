package com.reporting.portal.controller;


import com.reporting.portal.dto.LoginRequest;
import com.reporting.portal.dto.RegisterRequest;
import com.reporting.portal.dto.ForgotPasswordRequest;
import com.reporting.portal.dto.VerifyOtpRequest;
import com.reporting.portal.dto.ResetPasswordRequest;
import com.reporting.portal.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/api/auth", "/auth"})
@CrossOrigin(origins = "http://65.1.248.88") 
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            return ResponseEntity.ok(userService.login(request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody RegisterRequest request) {
        try {
            return ResponseEntity.ok(userService.register(request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/kingchat")
    public ResponseEntity<?> kingchatLogin(@RequestBody java.util.Map<String, String> request) {
        try {
            String token = request.get("token");
            String email = request.get("email");
            String firstName = request.get("firstName");
            String lastName = request.get("lastName");
            String username = request.get("username");
            return ResponseEntity.ok(userService.loginWithKingChatToken(token, email, firstName, lastName, username));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/complete-invite")
    public ResponseEntity<?> completeInvite(@RequestBody com.reporting.portal.dto.CompleteInviteRequest request) {
        try {
            return ResponseEntity.ok(userService.completeInvite(request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        try {
            userService.forgotPassword(request);
            return ResponseEntity.ok("OTP sent to email.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody VerifyOtpRequest request) {
        try {
            boolean isValid = userService.verifyOtp(request);
            if (isValid) {
                return ResponseEntity.ok("OTP verified.");
            } else {
                return ResponseEntity.badRequest().body("Invalid OTP.");
            }
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        try {
            userService.resetPassword(request);
            return ResponseEntity.ok("Password reset successful.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody com.reporting.portal.dto.ChangePasswordRequest request) {
        try {
            userService.changePassword(request);
            return ResponseEntity.ok(java.util.Map.of("message", "Password updated successfully."));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/security-question")
    public ResponseEntity<?> getSecurityQuestion(@RequestParam String email) {
        try {
            return ResponseEntity.ok(java.util.Map.of("question", userService.getSecurityQuestion(email)));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/reset-password-security")
    public ResponseEntity<?> resetPasswordSecurity(@RequestBody java.util.Map<String, String> request) {
        try {
            if (request.containsKey("answer1")) {
                userService.resetPasswordWithSecurityAnswers(
                    request.get("email"), 
                    request.get("answer1"),
                    request.get("answer2"),
                    request.get("answer3"),
                    request.get("newPassword")
                );
            } else {
                userService.resetPasswordWithSecurityAnswer(
                    request.get("email"), 
                    request.get("answer"), 
                    request.get("newPassword")
                );
            }
            return ResponseEntity.ok("Password reset successful.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/update-security-settings")
    public ResponseEntity<?> updateSecuritySettings(@RequestBody java.util.Map<String, String> request) {
        try {
            if (request.containsKey("answer1")) {
                userService.updateSecuritySettings(
                    request.get("email"),
                    request.get("answer1"),
                    request.get("answer2"),
                    request.get("answer3")
                );
            } else {
                userService.updateSecuritySettings(
                    request.get("email"),
                    request.get("question"),
                    request.get("answer")
                );
            }
            return ResponseEntity.ok("Security settings updated.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PostMapping("/reset-password-set-questions")
    public ResponseEntity<?> resetPasswordSetQuestions(@RequestBody java.util.Map<String, String> request) {
        try {
            userService.resetPasswordAndSetQuestions(
                request.get("email"),
                request.get("answer1"),
                request.get("answer2"),
                request.get("answer3"),
                request.get("newPassword")
            );
            return ResponseEntity.ok("Security questions set and password reset successful.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PostMapping("/admin-set-questions")
    public ResponseEntity<?> adminSetQuestions(@RequestBody java.util.Map<String, String> request) {
        try {
            userService.updateSecuritySettings(
                request.get("email"),
                request.get("answer1"),
                request.get("answer2"),
                request.get("answer3")
            );
            return ResponseEntity.ok("Security questions updated successfully (Admin).");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

