package com.robert.leave_ms_bn.controllers;

import com.robert.leave_ms_bn.dtos.role.CreateRoleRequest;
import com.robert.leave_ms_bn.dtos.role.UpdateRoleRequest;
import com.robert.leave_ms_bn.entities.Role;
import com.robert.leave_ms_bn.mappers.RoleMapper;
import com.robert.leave_ms_bn.repositories.RoleRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping("/roles")
public class RolesController {

    private final RoleMapper roleMapper;
    private final RoleRepository roleRepository;

    // GET: Fetch all roles
    @GetMapping
    public Iterable<UpdateRoleRequest.RoleDto> findAllRoles() {
        return roleRepository.findAll().stream().map(roleMapper::toRole).toList();
    }

    // GET: Fetch a role by ID
    @GetMapping("/{id}")
    public ResponseEntity<UpdateRoleRequest.RoleDto> findRoleById(@PathVariable Long id) {
        Optional<Role> role = roleRepository.findById(id);
        return role.map(value -> ResponseEntity.ok(roleMapper.toRole(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // POST: Create a new role
    @PostMapping
    public ResponseEntity<UpdateRoleRequest.RoleDto> createRole(@Valid @RequestBody CreateRoleRequest roleDto) {
        // Map DTO to Role entity
        Role role = roleMapper.toRoleEntity(roleDto);

        role.setName(roleDto.getName());

        // Save to repository
        Role savedRole = roleRepository.save(role);

        // Return created role
        return ResponseEntity.status(201).body(roleMapper.toRole(savedRole));
    }

    // PUT: Update an existing role
    @PutMapping("/{id}")
    public ResponseEntity<UpdateRoleRequest.RoleDto> updateRole(@PathVariable Long id, @Valid @RequestBody UpdateRoleRequest roleDto) {
        Optional<Role> existingRole = roleRepository.findById(id);
        if (existingRole.isPresent()) {
            Role updatedRole = existingRole.get();

            // Update role properties
            updatedRole.setName(roleDto.getName()); // Assuming Role has a name field.
            updatedRole.setDescription(roleDto.getDescription()); // Example additional field

            // Save the updated role
            Role savedRole = roleRepository.save(updatedRole);

            // Return updated role
            return ResponseEntity.ok(roleMapper.toRole(savedRole));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE: Delete a role by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        if (roleRepository.existsById(id)) {
            roleRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
