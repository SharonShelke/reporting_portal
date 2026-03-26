package com.reporting.portal.controller;


import com.reporting.portal.dto.LoginRequest;
import com.reporting.portal.dto.RegisterRequest;
import com.reporting.portal.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000") 
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

    @PostMapping("/complete-invite")
    public ResponseEntity<?> completeInvite(@RequestBody com.reporting.portal.dto.CompleteInviteRequest request) {
        try {
            return ResponseEntity.ok(userService.completeInvite(request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
