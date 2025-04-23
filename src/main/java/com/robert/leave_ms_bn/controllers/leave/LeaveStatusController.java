package com.robert.leave_ms_bn.controllers.leave;

import com.robert.leave_ms_bn.dtos.leave.LeaveStatusDto;
import com.robert.leave_ms_bn.entities.LeaveStatus;
import com.robert.leave_ms_bn.mappers.leave.LeaveStatusMapper;
import com.robert.leave_ms_bn.repositories.LeaveStatusRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@RestController
@RequestMapping("/leave/status")
public class LeaveStatusController {

    private final LeaveStatusRepository leaveStatusRepository;
    private final LeaveStatusMapper leaveStatusMapper;

    // 🔹 GET all leave statuses
    @GetMapping
    public List<LeaveStatusDto> getAllLeaveStatuses() {
        return leaveStatusRepository.findAll()
                .stream()
                .map(leaveStatusMapper::toDto)
                .collect(Collectors.toList());
    }


    @GetMapping("/{id}")
    public ResponseEntity<LeaveStatusDto> getLeaveStatusById(@PathVariable int id) {
        Optional<LeaveStatus> leaveStatus = leaveStatusRepository.findById(id);
        return leaveStatus.map(status -> ResponseEntity.ok(leaveStatusMapper.toDto(status)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @PostMapping
    public ResponseEntity<LeaveStatusDto> createLeaveStatus(@Valid @RequestBody LeaveStatusDto leaveStatusDto) {
        LeaveStatus status = leaveStatusMapper.CreateLeaveStatus(leaveStatusDto);
        LeaveStatus saved = leaveStatusRepository.save(status);
        return ResponseEntity.status(201).body(leaveStatusMapper.toDto(saved));
    }


    @PutMapping("/{id}")
    public ResponseEntity<LeaveStatusDto> updateLeaveStatus(
            @PathVariable int  id,
            @Valid @RequestBody LeaveStatusDto leaveStatusDto) {

        Optional<LeaveStatus> existing = leaveStatusRepository.findById(id);
        if (existing.isPresent()) {
            LeaveStatus statusToUpdate = existing.get();
            statusToUpdate.setName(leaveStatusDto.getName());
            LeaveStatus saved = leaveStatusRepository.save(statusToUpdate);
            return ResponseEntity.ok(leaveStatusMapper.toDto(saved));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // 🔹 DELETE leave status
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLeaveStatus(@PathVariable int id) {
        if (leaveStatusRepository.existsById(id)) {
            leaveStatusRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
