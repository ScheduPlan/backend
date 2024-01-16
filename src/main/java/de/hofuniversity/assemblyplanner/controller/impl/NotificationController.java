package de.hofuniversity.assemblyplanner.controller.impl;

import de.hofuniversity.assemblyplanner.persistence.model.dto.NotificationItem;
import de.hofuniversity.assemblyplanner.persistence.model.dto.NotificationMarkedEvent;
import de.hofuniversity.assemblyplanner.persistence.model.notification.Notification;
import de.hofuniversity.assemblyplanner.service.api.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    @Operation(summary = "returns notifications for the logged-in user")
    @ResponseStatus(HttpStatus.OK)
    public Set<NotificationItem> getNotifications(@RequestParam Date after) {
        if(after == null)
            return notificationService.getNotifications();
        return notificationService.getNotifications(after);
    }

    @GetMapping("/{notificationId}")
    @Operation(summary = "returns notifications for the logged-in user")
    @ResponseStatus(HttpStatus.OK)
    public NotificationItem getNotification(@PathVariable UUID notificationId) {
        return notificationService.getNotification(notificationId);
    }

    @PutMapping("/{notificationId}")
    @Operation(summary = "sets the read-state of the given notification", responses = {
            @ApiResponse(responseCode = "400", description = "the read state can't be updated. Most likely," +
                    " the current user is not allowed to set the read-state for this notification."),
            @ApiResponse(responseCode = "404", description = "the notification can't be found")
    })
    @ResponseStatus(HttpStatus.OK)
    public NotificationItem mark(@RequestBody NotificationMarkedEvent markedEvent, @PathVariable UUID notificationId) {
        try {
            return notificationService.mark(markedEvent, notificationId);
        } catch (AccessDeniedException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The notification can't be marked for the current user");
        }
    }
}
