package com.robert.leave_ms_bn.controllers;

import com.robert.leave_ms_bn.dtos.auth.LoginRequest;
import com.robert.leave_ms_bn.dtos.auth.RegisterRequest;
import com.robert.leave_ms_bn.dtos.auth.UpdatePasswordRequest;
import com.robert.leave_ms_bn.mappers.UserMapper;
import com.robert.leave_ms_bn.repositories.RoleRepository;
import com.robert.leave_ms_bn.repositories.UserRepository;
import com.robert.leave_ms_bn.services.EmailService;
import com.robert.leave_ms_bn.services.JwtService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.resource.ResourceTransformer;

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
    private final ResourceTransformer resourceTransformer;
    private AuthenticationManager authenticationManager;
    private EmailService emailService;

    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
//        var user = userRepository.findUserByEmail(loginRequest.getEmail());
//        if (user == null) {
//            return ResponseEntity.status(401).body(Map.of("message", "No account found with the provided email or password."));
//        }
//
//        boolean isPassword = passwordEncoder.matches(loginRequest.getPassword(), user.getPassword());
//        if (!isPassword) {
//            return ResponseEntity.status(401).body(Map.of("message", "The email or password you entered is incorrect."));
//        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        var user  = userRepository.findByEmail(loginRequest.getEmail());
        var token = jwtService.generateToken(user);
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
        newUser.setEmail(registerRequest.getEmail());
        newUser.setPhone(registerRequest.getPhone());
        newUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        newUser.setFirst_name(registerRequest.getFirstName());
        newUser.setLast_name(registerRequest.getLastName());
        var role = roleRepository.findById((long) registerRequest.getRole_id())
                .orElseThrow(() -> new RuntimeException("Role not found"));
        newUser.setRole(role);
        userRepository.save(newUser);
        return ResponseEntity.status(201).body(Map.of("message", "Registration successful."));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        var user = userRepository.findUserById((Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
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

    @PostMapping ("/check-token")
    public  boolean  checkToken(@RequestHeader("Authorization") String token) {
        return jwtService.validateToken(token);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok(Map.of("message", "Logout successful (token should be discarded on client side)"));
    }


    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleBadCredentialsException(BadCredentialsException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", e.getMessage()));
    }
}
