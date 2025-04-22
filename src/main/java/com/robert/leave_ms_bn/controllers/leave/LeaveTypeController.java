package com.robert.leave_ms_bn.controllers.leave;

import com.robert.leave_ms_bn.dtos.leave.LeaveTypeDto;
import com.robert.leave_ms_bn.entities.LeaveType;
import com.robert.leave_ms_bn.mappers.leave.LeaveTypeMapper;
import com.robert.leave_ms_bn.repositories.LeaveTypeRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping("leave/types")
public class LeaveTypeController {

    private final LeaveTypeRepository leaveTypeRepository;
    private final LeaveTypeMapper leaveTypeMapper;

    // 🔹 GET all leave types
    @GetMapping
    public Iterable<LeaveTypeDto> getLeaveTypes() {
        return leaveTypeRepository.findAll()
                .stream()
                .map(leaveTypeMapper::toDto)
                .toList();
    }

    // 🔹 GET leave type by ID
    @GetMapping("/{id}")
    public ResponseEntity<LeaveTypeDto> getLeaveTypeById(@PathVariable int id) {
        Optional<LeaveType> leaveType = leaveTypeRepository.findById(id);
        return leaveType.map(type -> ResponseEntity.ok(leaveTypeMapper.toDto(type)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 🔹 POST (create) a new leave type
    @PostMapping
    public ResponseEntity<LeaveTypeDto> createLeaveType(@Valid @RequestBody LeaveTypeDto leaveTypeDto) {
        LeaveType leaveType = leaveTypeMapper.toEntity(leaveTypeDto);
        LeaveType saved = leaveTypeRepository.save(leaveType);
        return ResponseEntity.status(201).body(leaveTypeMapper.toDto(saved));
    }

    // 🔹 PUT (update) an existing leave type
    @PutMapping("/{id}")
    public ResponseEntity<LeaveTypeDto> updateLeaveType(
            @PathVariable int id,
            @Valid @RequestBody LeaveTypeDto leaveTypeDto) {

        Optional<LeaveType> existing = leaveTypeRepository.findById(id);
        if (existing.isPresent()) {
            LeaveType updated = existing.get();
            updated.setName(leaveTypeDto.getName());
            updated.setDescription(leaveTypeDto.getDescription());
            LeaveType saved = leaveTypeRepository.save(updated);
            return ResponseEntity.ok(leaveTypeMapper.toDto(saved));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // 🔹 DELETE a leave type
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLeaveType(@PathVariable int id) {
        if (leaveTypeRepository.existsById(id)) {
            leaveTypeRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
