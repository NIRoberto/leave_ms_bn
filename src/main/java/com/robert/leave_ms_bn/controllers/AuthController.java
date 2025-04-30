package com.robert.leave_ms_bn.controllers;

import com.robert.leave_ms_bn.dtos.auth.LoginRequest;
import com.robert.leave_ms_bn.dtos.auth.RegisterRequest;
import com.robert.leave_ms_bn.dtos.auth.UpdatePasswordRequest;
import com.robert.leave_ms_bn.entities.User;
import com.robert.leave_ms_bn.mappers.UserMapper;
import com.robert.leave_ms_bn.repositories.RoleRepository;
import com.robert.leave_ms_bn.repositories.UserRepository;
import com.robert.leave_ms_bn.services.EmailService;
import com.robert.leave_ms_bn.services.JwtService;
import com.robert.leave_ms_bn.services.PasswordGeneratorService;
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
    private final PasswordGeneratorService passwordGeneratorService;

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

        var user = userRepository.findByEmail(loginRequest.getEmail());
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

        var generatedPassword = passwordGeneratorService.generateSecurePassword();
        var newUser = userMapper.registerUser(registerRequest);
        newUser.setEmail(registerRequest.getEmail());
        newUser.setPhone(registerRequest.getPhone());
        newUser.setPassword(passwordEncoder.encode(
                generatedPassword
        ));
        newUser.setFirst_name(registerRequest.getFirstName());
        newUser.setLast_name(registerRequest.getLastName());
        var role = roleRepository.findById((long) registerRequest.getRole_id())
                .orElseThrow(() -> new RuntimeException("Role not found"));
        newUser.setRole(role);
        userRepository.save(newUser);
        sendNewAccountEmail(newUser, generatedPassword);
        return ResponseEntity.status(201).body(Map.of("message", "Registration successful."));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        var user = userRepository.findUserById((Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        if (user == null) {
            return ResponseEntity.status(404).body(Map.of("message", "User not found"));
        }
        return ResponseEntity.ok(user);
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody UpdatePasswordRequest passwordRequest, Principal principal) {
        var user = userRepository.findUserById((Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        if (user == null) {
            return ResponseEntity.status(404).body(Map.of("message", "User not found"));
        }
        if (!passwordEncoder.matches(passwordRequest.getCurrentPassword(), user.getPassword())) {
            return ResponseEntity.status(400).body(Map.of("message", "Current password is incorrect"));
        }
        user.setPassword(passwordEncoder.encode(passwordRequest.getNewPassword()));
        userRepository.save(user);
        sendPasswordChangeSuccessEmail(user);
        return ResponseEntity.ok(Map.of("message", "Password updated successfully"));
    }

    @PostMapping("/check-token")
    public boolean checkToken(@RequestHeader("Authorization") String token) {
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


    private void sendNewAccountEmail(User user, String temporaryPassword) {
        String recipientEmail = user.getEmail();
        String subject = "Welcome to Leave Management System - Your Account Details";
        String body = buildNewAccountEmailBody(user, temporaryPassword);

        emailService.sendSimpleEmail(recipientEmail, subject, body);
    }

    private String buildNewAccountEmailBody(User user, String temporaryPassword) {
        String firstName = user.getFirst_name();
        String role = user.getRole().getName(); // Assuming `Role` has a `getName()` method
        String username = user.getEmail(); // Assuming email is used as the username

        return String.format("""
                Dear %s,
                
                Welcome to the Leave Management System! Your account has been successfully created, and you can now access the system to manage your leave requests and approvals.
                
                Here are your account details:
                - **Username**: %s
                - **Temporary Password**: %s
                - **Role**: %s
                
                For security reasons, please log in to the system immediately and update your password to a new one. This will ensure your account remains secure.
                
                You can access the Leave Management System at: [Leave Management System URL]
                
                Steps to update your password:
                1. Log in using the credentials above.
                2. Navigate to the "Change Password" section in your profile settings.
                3. Enter your temporary password as the current password.
                4. Set a new password of your choice and save the changes.
                
                If you have any questions or encounter any issues, please contact the support team at [Support Email or Phone Number].
                
                Best regards,
                LeaveSys Team
                """, firstName, username, temporaryPassword, role).strip();
    };




    private void sendPasswordChangeSuccessEmail(User user) {
        String recipientEmail = user.getEmail();
        String subject = "Your Password Has Been Successfully Changed";
        String body = buildPasswordChangeSuccessEmailBody(user);

        emailService.sendSimpleEmail(recipientEmail, subject, body);
    }

    private String buildPasswordChangeSuccessEmailBody(User user) {
        String firstName = user.getFirst_name(); // Assuming `User` has a `getFirst_name()` method
        String supportEmail = "support@leavemanagement.com"; // Replace with your actual support email

        return String.format("""
        Dear %s,

        We wanted to let you know that your password has been successfully changed. If you made this change, no further action is required.

        If you did not request this change, please contact our support team immediately to secure your account.

        For your security:
        - Do not share your password with anyone.
        - Ensure your password is strong and unique.
        - Regularly update your password to keep your account secure.

        If you have any questions or need assistance, please contact our support team at %s.

        Best regards,
        LeaveSys Team
        """, firstName, supportEmail).strip();
    }
}
