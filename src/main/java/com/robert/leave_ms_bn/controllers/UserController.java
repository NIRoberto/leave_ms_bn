package com.robert.leave_ms_bn.controllers;

import com.robert.leave_ms_bn.dtos.users.UserDto;
import com.robert.leave_ms_bn.entities.Role;
import com.robert.leave_ms_bn.entities.User;
import com.robert.leave_ms_bn.mappers.UserMapper;
import com.robert.leave_ms_bn.repositories.RoleRepository;
import com.robert.leave_ms_bn.repositories.UserRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;

    @GetMapping
    public Iterable<User> getUsers() {
        return userRepository.findAll().stream()
//                .map(userMapper::toUserDto)
                .toList()    //   duration in days as int from start date to end date
                .reversed();


    }

    @GetMapping("{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(user -> ResponseEntity.ok(userMapper.toUserDto(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody User user) {
        userRepository.save(user);
        return ResponseEntity.status(201).body(userMapper.toUserDto(user));
    }

    @PutMapping("{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @Valid @RequestBody User updatedUser) {
        return userRepository.findById(id).map(existingUser -> {
            existingUser.setFirst_name(updatedUser.getFirst_name());
            existingUser.setLast_name(updatedUser.getLast_name());
            existingUser.setEmail(updatedUser.getEmail());
            existingUser.setPhone(updatedUser.getPhone());
            existingUser.setGender(updatedUser.getGender());
            existingUser.setPassword(updatedUser.getPassword());
            existingUser.setProfile_picture_url(updatedUser.getProfile_picture_url());
            userRepository.save(existingUser);
            return ResponseEntity.ok(userMapper.toUserDto(existingUser));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        if (!userRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        userRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
    }

    @PatchMapping("{userId}/assign-role/{roleId}")
    public ResponseEntity<?> assignRoleToUser(@PathVariable Long userId, @PathVariable int roleId) {
        User user = userRepository.findById(userId)
                .orElse(null);
        Role role = roleRepository.findById((long) roleId)
                .orElse(null);

        if (user == null || role == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "User or Role not found"));
        }

        user.setRole(role);
        userRepository.save(user);
        return ResponseEntity.ok(Map.of("message", "Role assigned successfully"));
    }
}
