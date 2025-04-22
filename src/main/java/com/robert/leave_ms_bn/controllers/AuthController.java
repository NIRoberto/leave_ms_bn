package com.robert.leave_ms_bn.controllers;

import com.robert.leave_ms_bn.dtos.auth.LoginRequest;
import com.robert.leave_ms_bn.dtos.auth.RegisterRequest;
import com.robert.leave_ms_bn.dtos.auth.UpdatePasswordRequest;
import com.robert.leave_ms_bn.entities.User;
import com.robert.leave_ms_bn.mappers.UserMapper;
import com.robert.leave_ms_bn.repositories.RoleRepository;
import com.robert.leave_ms_bn.repositories.UserRepository;
import com.robert.leave_ms_bn.services.JwtService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        var user = userRepository.findUserByEmail(loginRequest.getEmail());
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("message", "No account found with the provided email or password."));
        }

        boolean isPassword = passwordEncoder.matches(loginRequest.getPassword(), user.getPassword());
        if (!isPassword) {
            return ResponseEntity.status(401).body(Map.of("message", "The email or password you entered is incorrect."));
        }

        var token = jwtService.generateToken(user.getEmail());
        return ResponseEntity.ok(Map.of(
                "message", "You have successfully logged in.",
                "token", token
        ));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        if (userRepository.findUserByEmail(registerRequest.getEmail()) != null) {
            return ResponseEntity.status(409).body(Map.of("message", "An account with this email already exists."));
        }

        var newUser = userMapper.registerUser(registerRequest);
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        newUser.setFirst_name(registerRequest.getFirstName());
        newUser.setLast_name(registerRequest.getLastName());
        var role = roleRepository.findById((long) registerRequest.getRole_id())
                .orElseThrow(() -> new RuntimeException("Role not found"));
        newUser.setRole(role);

        userRepository.save(newUser);
        return ResponseEntity.status(201).body(Map.of("message", "Registration successful."));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Principal principal) {
        var user = userRepository.findUserByEmail(principal.getName());
        if (user == null) {
            return ResponseEntity.status(404).body(Map.of("message", "User not found"));
        }
        return ResponseEntity.ok(userMapper.toUserDto(user));
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody UpdatePasswordRequest passwordRequest, Principal principal) {
        var user = userRepository.findUserByEmail(principal.getName());
        if (user == null) {
            return ResponseEntity.status(404).body(Map.of("message", "User not found"));
        }

        if (!passwordEncoder.matches(passwordRequest.getCurrentPassword(), user.getPassword())) {
            return ResponseEntity.status(400).body(Map.of("message", "Current password is incorrect"));
        }

        user.setPassword(passwordEncoder.encode(passwordRequest.getNewPassword()));
        userRepository.save(user);
        return ResponseEntity.ok(Map.of("message", "Password updated successfully"));
    }

    @GetMapping("/check-token")
    public ResponseEntity<?> checkToken(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
        }
        return ResponseEntity.ok(Map.of("message", "Token is valid", "user", principal.getName()));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // With JWT this is often handled on the client by simply deleting the token.
        return ResponseEntity.ok(Map.of("message", "Logout successful (token should be discarded on client side)"));
    }
}
