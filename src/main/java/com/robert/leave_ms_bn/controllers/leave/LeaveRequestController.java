package com.robert.leave_ms_bn.controllers.leave;
import com.robert.leave_ms_bn.dtos.leave.LeaveBalanceDto;
import com.robert.leave_ms_bn.dtos.leave.LeaveRequestDto;
import com.robert.leave_ms_bn.dtos.leave.request.ApproveLeaveRequestDto;
import com.robert.leave_ms_bn.dtos.notifications.create.SendNotificationDto;
import com.robert.leave_ms_bn.entities.*;
import com.robert.leave_ms_bn.mappers.leave.LeaveRequestMapper;
import com.robert.leave_ms_bn.repositories.*;
import com.robert.leave_ms_bn.services.EmailService;
import com.robert.leave_ms_bn.services.LeaveBalanceService;
import com.robert.leave_ms_bn.services.NotificationService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

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
    private final LeaveBalanceService leaveBalanceService;
    private EmailService emailService;


    @GetMapping
    public  List<LeaveRequest> getLeaveRequests() {
        return leaveRequestRepository.findAll().stream()
//                .map(leaveRequestMapper::toEntity)
                .toList().reversed();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getLeaveRequestsByUserId(@PathVariable Long userId) {
        // Check if the user exists
        if (!userRepository.existsById(userId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User with ID " + userId + " not found.");
        }

        // Fetch leave requests
        List<LeaveRequest> leaveRequests = leaveRequestRepository.findAllByUserId(userId).reversed();

        // Optional: Return a message if there are no leave requests
        if (leaveRequests.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body("No leave requests found for user with ID " + userId + ".");
        }

        // Return leave requests
        return ResponseEntity.ok(leaveRequests);
    }



    @PostMapping
    public ResponseEntity<?> requestLeave(@RequestBody LeaveRequestDto requestDto) {
        try {
            // Map DTO to entity
            LeaveRequest leaveRequest = leaveRequestMapper.fromEntity(requestDto);
            leaveRequest.setStart_date(requestDto.getStartDate());
            leaveRequest.setEnd_date(requestDto.getEndDate());

            // Calculate duration (inclusive of both dates)
            long durationDays = ChronoUnit.DAYS.between(requestDto.getStartDate(), requestDto.getEndDate()) + 1;
            leaveRequest.setDuration((int) durationDays);

            // Fetch default leave status
            LeaveStatus defaultStatus = leaveStatusRepository.findAll()
                    .stream()
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("No default leave status found."));

            // Fetch default reviewer
            User defaultReviewer = userRepository.findAll()
                    .stream()
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("No default reviewer found."));

            // Get leave type
            LeaveType leaveType = leaveTypeRepository.findById(requestDto.getLeaveTypeId())
                    .orElseThrow(() -> new RuntimeException("Leave type not found with ID: " + requestDto.getLeaveTypeId()));

            // Get requesting user
            User user = userRepository.findById(requestDto.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + requestDto.getUserId()));

            // Set relationships
            leaveRequest.setUser(user);
            leaveRequest.setLeaveType(leaveType);
            leaveRequest.setReviewer_by(defaultReviewer);
            leaveRequest.setLeaveStatus(defaultStatus);

            // Save leave request
            LeaveRequest savedLeaveRequest = leaveRequestRepository.save(leaveRequest);

            // Return response
            return ResponseEntity.ok(Map.of(
                    "message", "Leave request submitted successfully.",
                    "leaveRequestId", savedLeaveRequest.getId()
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/status")
    public ResponseEntity<?> updateLeaveStatus(@RequestBody ApproveLeaveRequestDto approveDto) {
        try {
            LeaveRequest leaveRequest = leaveRequestRepository.findById(approveDto.getRequestId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Leave request with ID " + approveDto.getRequestId() + " not found"));
            User approvalRequester = userRepository.findById(leaveRequest.getUser().getId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Requester user not found"));
            User approvalReviewer = userRepository.findById(approveDto.getReviewerById())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Reviewer user with ID " + approveDto.getReviewerById() + " not found"));
            LeaveStatus approvalLeaveStatus = leaveStatusRepository.findById(approveDto.getLeaveStatusId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Leave status with ID " + approveDto.getLeaveStatusId() + " not found"));
            leaveRequest.setReviewer_by(approvalReviewer);
            leaveRequest.setLeaveStatus(approvalLeaveStatus);

            try {
                LeaveBalanceDto updatedBalance = leaveBalanceService.createOrUpdateLeaveBalance(leaveRequest.getUser().getId(), leaveRequest.getLeaveType().getId() , leaveRequest.getDuration());


            }
            catch (Exception e) {
                System.out.println(e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("error", "Failed to update leave balance."));
            }


            LeaveRequest updatedRequest = leaveRequestRepository.save(leaveRequest);

            try {
                SendNotificationDto notificationDto = new SendNotificationDto();
                notificationDto.setUser_id(approvalRequester.getId());
                notificationDto.set_read(false);
                notificationDto.setNotification_type_id(approvalLeaveStatus.getId());
                notificationDto.setCreated_at(Instant.now());
                notificationDto.setMessage(generateLeaveStatusMessage(updatedRequest, approvalLeaveStatus));
                notificationService.sendNotification(notificationDto);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            try {
                sendLeaveStatusEmail(updatedRequest, approvalLeaveStatus);
            } catch (Exception e) {
            }
            return ResponseEntity.ok(leaveRequestMapper.toEntity(updatedRequest));

        } catch (ResponseStatusException e) {
            return ResponseEntity.status(
                    HttpStatus.BAD_REQUEST
            ).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
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
        String formattedStart = leaveRequest.getStart_date().toString();
        String formattedEnd = leaveRequest.getEnd_date().toString();
        String leaveType = leaveRequest.getLeaveType().getName();
        int duration = leaveRequest.getDuration();
        String reviewerName = leaveRequest.getReviewer_by() != null
                ? leaveRequest.getReviewer_by().getFirst_name() + " " + leaveRequest.getReviewer_by().getLast_name()
                : "N/A";

        String greeting = String.format("Dear %s,", firstName);
        String body;

        switch (leaveStatus.getId()) {
            case 2: // Approved
                body = String.format("""
            %s

            We’re pleased to inform you that your leave request for %s (%d days) from %s to %s has been approved.

            Reviewer: %s

            Please ensure any necessary handovers are completed prior to your leave.

            Best regards,
            LeaveSys Team
            """, greeting, leaveType, duration, formattedStart, formattedEnd, reviewerName);
                break;

            case 3: // Rejected
                body = String.format("""
            %s

            We regret to inform you that your leave request for %s (%d days) from %s to %s has been rejected.

            Reviewer: %s

            For more details, please reach out to your reviewer or HR department.

            Kind regards,
            LeaveSys Team
            """, greeting, leaveType, duration, formattedStart, formattedEnd, reviewerName);
                break;

            case 4: // Cancelled
                body = String.format("""
            %s

            This is to confirm that your leave request for %s (%d days) from %s to %s has been cancelled.

            If this was an error, please submit a new request or contact support.

            Warm regards,
            LeaveSys Team
            """, greeting, leaveType, duration, formattedStart, formattedEnd);
                break;

            default: // Other updates
                body = String.format("""
            %s

            There has been an update to your leave request for %s (%d days) from %s to %s.

            Please log in to the system for more information.

            Best regards,
            LeaveSys Team
            """, greeting, leaveType, duration, formattedStart, formattedEnd);
                break;
        }

        return body.strip();
    }

    private String formatDateTime(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern("MMMM dd, yyyy 'at' HH:mm"));
    }

}
