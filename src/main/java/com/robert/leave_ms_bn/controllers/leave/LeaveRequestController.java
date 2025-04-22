package com.robert.leave_ms_bn.controllers.leave;

import com.robert.leave_ms_bn.dtos.leave.LeaveRequestDto;
import com.robert.leave_ms_bn.dtos.leave.request.ApproveLeaveRequestDto;
import com.robert.leave_ms_bn.dtos.notifications.create.SendNotificationDto;
import com.robert.leave_ms_bn.entities.*;
import com.robert.leave_ms_bn.mappers.leave.LeaveRequestMapper;
import com.robert.leave_ms_bn.repositories.*;
import com.robert.leave_ms_bn.services.NotificationService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    private final NotificationTypeRepository notificationTypeRepository;
    private final NotificationService notificationService;


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
        SendNotificationDto sendNotificationDto = new SendNotificationDto();

        sendNotificationDto.setUser_id(approvalRequester.getId());
        sendNotificationDto.set_read(false);
        sendNotificationDto.setNotification_type_id(approvalLeaveStatus.getId());
        sendNotificationDto.setCreated_at(updated.getCreated_at());
        String message = generateLeaveStatusMessage(leaveRequest, approvalLeaveStatus);
        sendNotificationDto.setMessage(
             message
        );

        notificationService.sendNotification(
                sendNotificationDto
        );
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

    private NotificationType getNotificationTypeByLeaveStatus(LeaveStatus leaveStatus) {
        // Fetch notification type based on leave status
        switch (leaveStatus.getId()) {
            case 2:  // Approved
                return notificationTypeRepository.findById(2)
                        .orElseThrow(() -> new RuntimeException("Notification type not found for approved leave"));
            case 3:  // Rejected
                return notificationTypeRepository.findById(4)
                        .orElseThrow(() -> new RuntimeException("Notification type not found for rejected leave"));
            case 4:  // Cancelled
                return notificationTypeRepository.findById(3)
                        .orElseThrow(() -> new RuntimeException("Notification type not found for cancelled leave"));
            default:
                return notificationTypeRepository.findById(1)
                        .orElseThrow(() -> new RuntimeException("Notification type not found for unknown status"));
        }
    }

    private String generateLeaveStatusMessage(LeaveRequest leaveRequest, LeaveStatus leaveStatus) {
        // Example message generation based on leave status
        String statusMessage = "";
        if (leaveStatus.getId() == 2) {
            statusMessage = String.format("Congratulations %s, your leave request from %s to %s has been approved.",
                    leaveRequest.getUser().getFirst_name(), leaveRequest.getStart_date(), leaveRequest.getEnd_date());
        } else if (leaveStatus.getId() == 3) {
            statusMessage = String.format("Sorry %s, your leave request from %s to %s has been rejected.",
                    leaveRequest.getUser().getFirst_name(), leaveRequest.getStart_date(), leaveRequest.getEnd_date());
        } else if (leaveStatus.getId() == 4) {
            statusMessage = String.format("Your leave request from %s to %s has been cancelled.",
                    leaveRequest.getStart_date(), leaveRequest.getEnd_date());
        }
        return statusMessage;
    }
}
