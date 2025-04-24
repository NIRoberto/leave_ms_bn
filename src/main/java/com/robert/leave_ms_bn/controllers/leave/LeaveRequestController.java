package com.robert.leave_ms_bn.controllers.leave;

import com.robert.leave_ms_bn.dtos.leave.LeaveRequestDto;
import com.robert.leave_ms_bn.dtos.leave.request.ApproveLeaveRequestDto;
import com.robert.leave_ms_bn.dtos.notifications.create.SendNotificationDto;
import com.robert.leave_ms_bn.entities.*;
import com.robert.leave_ms_bn.mappers.leave.LeaveRequestMapper;
import com.robert.leave_ms_bn.repositories.*;
import com.robert.leave_ms_bn.services.EmailService;
import com.robert.leave_ms_bn.services.NotificationService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    private EmailService emailService;






    // ✅ Get all leave requests
    @GetMapping
    public List<LeaveRequestDto> getLeaveRequests() {
        return leaveRequestRepository.findAll().stream()
                .map(leaveRequestMapper::toEntity)
                .toList();
    }

    // ✅ Get leave requests by user ID
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getLeaveRequestsByUserId(@PathVariable Long userId) {
        // Check if the user exists
        if (!userRepository.existsById(userId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User with ID " + userId + " not found.");
        }

        // Fetch leave requests
        List<LeaveRequest> leaveRequests = leaveRequestRepository.findAllByUserId(userId);

        // Optional: Return a message if there are no leave requests
        if (leaveRequests.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body("No leave requests found for user with ID " + userId + ".");
        }

        // Return leave requests
        return ResponseEntity.ok(leaveRequests);
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

        sendLeaveStatusEmail(leaveRequest, approvalLeaveStatus);
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







    private void sendLeaveStatusEmail(LeaveRequest leaveRequest, LeaveStatus leaveStatus) {
        String recipientEmail = leaveRequest.getUser().getEmail();
        String subject = buildEmailSubject(leaveStatus);
        String body = buildEmailBody(leaveRequest, leaveStatus);

        emailService.sendSimpleEmail(recipientEmail, subject, body);
    }

    private String buildEmailSubject(LeaveStatus leaveStatus) {
        switch (leaveStatus.getId()) {
            case 2:
                return "Leave Request Approved";
            case 3:
                return "Leave Request Rejected";
            case 4:
                return "Leave Request Cancelled";
            default:
                return "Leave Request Update";
        }
    }

    private String buildEmailBody(LeaveRequest leaveRequest, LeaveStatus leaveStatus) {
        String firstName = leaveRequest.getUser().getFirst_name();
        String formattedStart =  leaveRequest.getStart_date().toString();
        String formattedEnd =  leaveRequest.getEnd_date().toString();

        String greeting = String.format("Dear %s,", firstName);
        String body;

        switch (leaveStatus.getId()) {
            case 2:
                body = String.format("""
                %s

                We’re pleased to inform you that your leave request from %s to %s has been approved.

                Please ensure any necessary handovers are completed prior to your leave.

                Best regards,
                LeaveSys Team
                """, greeting, formattedStart, formattedEnd);
                break;

            case 3:
                body = String.format("""
                %s

                We regret to inform you that your leave request from %s to %s has been rejected.

                For more details, please reach out to your reviewer or HR department.

                Kind regards,
                LeaveSys Team
                """, greeting, formattedStart, formattedEnd);
                break;

            case 4:
                body = String.format("""
                %s

                This is to confirm that your leave request from %s to %s has been cancelled.

                If this was an error, please submit a new request or contact support.

                Warm regards,
                LeaveSys Team
                """, greeting, formattedStart, formattedEnd);
                break;

            default:
                body = String.format("""
                %s

                There has been an update to your leave request from %s to %s.

                Please log in to the system for more information.

                Best,
                LeaveSys Team
                """, greeting, formattedStart, formattedEnd);
                break;
        }

        return body.strip();
    }

    private String formatDateTime(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern("MMMM dd, yyyy 'at' HH:mm"));
    }

}
