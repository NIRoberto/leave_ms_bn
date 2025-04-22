package com.robert.leave_ms_bn.controllers.notification;

import com.robert.leave_ms_bn.dtos.notifications.NotificationTypeDto;
import com.robert.leave_ms_bn.dtos.notifications.type.CreateNotificationType;
import com.robert.leave_ms_bn.entities.NotificationType;
import com.robert.leave_ms_bn.mappers.NotificationTypeMapper;
import com.robert.leave_ms_bn.repositories.NotificationTypeRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@RequestMapping("/notification/types")
public class NotificationTypeController {

    private final NotificationTypeRepository notificationTypeRepository;
    private final NotificationTypeMapper notificationTypeMapper;

    // 🔹 GET all
    @GetMapping
    public List<NotificationTypeDto> getAllNotificationTypes() {
        return notificationTypeRepository.findAll()
                .stream()
                .map(notificationTypeMapper::toEntity)
                .collect(Collectors.toList());
    }

    // 🔹 GET by ID
    @GetMapping("/{id}")
    public ResponseEntity<NotificationTypeDto> getById(@PathVariable int id) {
        Optional<NotificationType> optional = notificationTypeRepository.findById(id);
        return optional.map(type -> ResponseEntity.ok(notificationTypeMapper.toEntity(type)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 🔹 CREATE
    @PostMapping
    public ResponseEntity<NotificationTypeDto> createNotificationType(@Valid @RequestBody CreateNotificationType dto) {
        NotificationType entity = notificationTypeMapper.creaNotificationType(dto);
        NotificationType saved = notificationTypeRepository.save(entity);
        return ResponseEntity.status(201).body(notificationTypeMapper.toEntity(saved));
    }

    // 🔹 UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<NotificationTypeDto> updateNotificationType(
            @PathVariable int id,
            @Valid @RequestBody NotificationTypeDto dto) {

        Optional<NotificationType> optional = notificationTypeRepository.findById(id);
        if (optional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        NotificationType existing = optional.get();
        existing.setName(dto.getName()); // assuming your DTO has a name
        NotificationType updated = notificationTypeRepository.save(existing);
        return ResponseEntity.ok(notificationTypeMapper.toEntity(updated));
    }

    // 🔹 DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotificationType(@PathVariable int id) {
        if (!notificationTypeRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        notificationTypeRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
