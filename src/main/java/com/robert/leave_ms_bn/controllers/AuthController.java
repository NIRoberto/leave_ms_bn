package com.robert.leave_ms_bn.controllers.auth;
import com.robert.leave_ms_bn.dtos.auth.LoginRequest;
import com.robert.leave_ms_bn.dtos.auth.RegisterRequest;
import com.robert.leave_ms_bn.mappers.UserMapper;
import com.robert.leave_ms_bn.repositories.UserRepository.UserRepository;
import com.robert.leave_ms_bn.services.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@RequestMapping("/auth")
public class AuthController {
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private UserMapper userMapper;
    private JwtService jwtService;


    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        String userNotFoundMessage = "No account found with the provided email or password.";
        String invalidCredentialsMessage = "The email or password you entered is incorrect.";
        String loginSuccessMessage = "You have successfully logged in.";

        var  user  =  userRepository.findUserByEmail(loginRequest.getEmail());
        boolean isPassword  = passwordEncoder.matches(loginRequest.getPassword(), user.getPassword());
        if(user == null) {
            return  ResponseEntity.status(401).body(
                    Map.of("message", userNotFoundMessage)
            );
        }
        if(!isPassword) {
            return  ResponseEntity.status(401).body(
                    Map.of("message", invalidCredentialsMessage)
            );
        }
        var token = jwtService.generateToken(user.getEmail());
        return  ResponseEntity.status(200).body(
                Map.of("message", loginSuccessMessage,
                       "token",    token
                        )
        );
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        String emailAlreadyExistsMessage = "An account with this email already exists.";
        String registrationSuccessMessage = "Registration successful.";

        if (userRepository.findUserByEmail(registerRequest.getEmail()) != null) {
            return ResponseEntity.status(409).body(
                    Map.of("message", emailAlreadyExistsMessage)
            );
        }
        var newUser  =  userMapper.registerUser(registerRequest);
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        userRepository.save(newUser);
        return ResponseEntity.status(201).body(
                Map.of("message", registrationSuccessMessage)
        );
    }
}
