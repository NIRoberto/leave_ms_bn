package com.robert.leave_ms_bn.controllers.leave;

import com.robert.leave_ms_bn.dtos.leave.LeaveRequestDto;
import com.robert.leave_ms_bn.dtos.leave.request.ApproveLeaveRequestDto;
import com.robert.leave_ms_bn.entities.LeaveRequest;
import com.robert.leave_ms_bn.entities.LeaveStatus;
import com.robert.leave_ms_bn.entities.LeaveType;
import com.robert.leave_ms_bn.entities.User;
import com.robert.leave_ms_bn.mappers.leave.LeaveRequestMapper;
import com.robert.leave_ms_bn.repositories.LeaveRequestRepository;
import com.robert.leave_ms_bn.repositories.LeaveStatusRepository;
import com.robert.leave_ms_bn.repositories.LeaveTypeRepository;
import com.robert.leave_ms_bn.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/leave/request")
public class LeaveRequestController {

    private final LeaveRequestRepository leaveRequestRepository;
    private final UserRepository userRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final LeaveStatusRepository leaveStatusRepository;
    private final LeaveRequestMapper leaveRequestMapper;

    // ✅ Get all leave requests
    @GetMapping
    public List<LeaveRequestDto> getLeaveRequests() {
        return leaveRequestRepository.findAll().stream()
                .map(leaveRequestMapper::toEntity)
                .toList();
    }

    // ✅ Submit new leave request
    @PostMapping
    public ResponseEntity<LeaveRequestDto> requestLeave(@RequestBody LeaveRequestDto requestDto) {

        LeaveRequest leaveRequest = leaveRequestMapper.fromEntity(requestDto);
        leaveRequest.setStart_date(requestDto.getStartDate());
        leaveRequest.setEnd_date(requestDto.getEndDate());

        long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(
                requestDto.getStartDate(), requestDto.getEndDate()) + 1;
        leaveRequest.setDuration((int) daysBetween);

        LeaveStatus defaultStatus = leaveStatusRepository.findAll()
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Default leave status not found"));

         User defaultReviewer = userRepository.findAll()
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Default leave status not found"));
         LeaveType requestLeaveType = leaveTypeRepository.findById(requestDto.getLeaveTypeId()).orElseThrow();

        User  requestUser  = userRepository.findById(requestDto.getUserId()).orElseThrow();

        leaveRequest.setUser(requestUser);
        leaveRequest.setLeaveType(requestLeaveType);
        leaveRequest.setReviewer_by(defaultReviewer);
        leaveRequest.setLeaveStatus(defaultStatus);

        LeaveRequest saved = leaveRequestRepository.save(leaveRequest);
        return ResponseEntity.ok(leaveRequestMapper.toEntity(saved));
    }

    // ✅ Approve/Reject/Cancel leave request
    @PutMapping("/status")
    public ResponseEntity<LeaveRequestDto> updateLeaveStatus(@RequestBody ApproveLeaveRequestDto approveDto) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(approveDto.getRequestId())
                .orElseThrow(() -> new RuntimeException("Leave request not found"));

        User  approvalRequester  =  userRepository.findById(approveDto.getRequestId()).orElseThrow();
        User  approvalReview =  userRepository.findById(approveDto.getReviewerById()).orElseThrow();
        LeaveStatus  approvalLeaveStatus = leaveStatusRepository.findById(approveDto.getLeaveStatusId()).orElseThrow();

        leaveRequest.setUser(approvalRequester);
        leaveRequest.setLeaveStatus(approvalLeaveStatus);
        leaveRequest.setReviewer_by(approvalReview);


        LeaveRequest updated = leaveRequestRepository.save(leaveRequest);
        return ResponseEntity.ok(leaveRequestMapper.toEntity(updated));
    }

    // ✅ Delete a leave request
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLeaveRequest(@PathVariable Long id) {
        if (!leaveRequestRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        leaveRequestRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
