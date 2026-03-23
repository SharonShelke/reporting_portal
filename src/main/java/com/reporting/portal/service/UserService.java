package com.reporting.portal.service;

import com.reporting.portal.dto.LoginRequest;
import com.reporting.portal.dto.RegisterRequest;
import com.reporting.portal.dto.UserDto;
import com.reporting.portal.entity.User;
import com.reporting.portal.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDto login(LoginRequest request) {
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password."));

        if (!user.getPassword().equals(request.getPassword()) || "inactive".equals(user.getStatus())) {
            throw new RuntimeException("Invalid email or password.");
        }
        
        return mapToDto(user);
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
