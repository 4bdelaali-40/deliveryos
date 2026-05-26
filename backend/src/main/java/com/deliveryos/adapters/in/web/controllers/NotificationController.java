package com.deliveryos.adapters.in.web.controllers;

import com.deliveryos.application.usecases.NotificationUseCase;
import com.deliveryos.domain.model.Notification;
import com.deliveryos.shared.ApiResponse;
import com.deliveryos.shared.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Notifications", description = "Notification management endpoints")
public class NotificationController {

    private final NotificationUseCase notificationUseCase;

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get notifications for a user")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','DISPATCHER','DRIVER','VIEWER')")
    public ResponseEntity<ApiResponse<PageResponse<Notification>>> findByUserId(
            @PathVariable UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Page<Notification> result = notificationUseCase.findByUserId(
                userId, PageRequest.of(page, size));

        return ResponseEntity.ok(ApiResponse.ok(PageResponse.from(result)));
    }

    @GetMapping("/user/{userId}/unread-count")
    @Operation(summary = "Get unread notification count")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','DISPATCHER','DRIVER','VIEWER')")
    public ResponseEntity<ApiResponse<Map<String, Long>>> countUnread(
            @PathVariable UUID userId) {

        long count = notificationUseCase.countUnread(userId);
        return ResponseEntity.ok(ApiResponse.ok(Map.of("unreadCount", count)));
    }

    @PatchMapping("/{id}/read")
    @Operation(summary = "Mark notification as read")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','DISPATCHER','DRIVER','VIEWER')")
    public ResponseEntity<ApiResponse<Notification>> markAsRead(
            @PathVariable UUID id) {

        Notification updated = notificationUseCase.markAsRead(id);
        return ResponseEntity.ok(ApiResponse.ok(updated));
    }

    @PatchMapping("/user/{userId}/read-all")
    @Operation(summary = "Mark all notifications as read")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','DISPATCHER','DRIVER','VIEWER')")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(
            @PathVariable UUID userId) {

        notificationUseCase.markAllAsRead(userId);
        return ResponseEntity.ok(ApiResponse.ok(null, "All notifications marked as read"));
    }
}