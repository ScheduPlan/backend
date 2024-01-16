package de.hofuniversity.assemblyplanner.persistence.model.dto;

import de.hofuniversity.assemblyplanner.persistence.model.notification.Notification;
import de.hofuniversity.assemblyplanner.persistence.model.notification.NotificationPayload;

import java.util.Date;
import java.util.UUID;

public record NotificationItem(String type, String notificationType, UUID id, Date createdAt, NotificationPayload payload, boolean read) {
    public NotificationItem(Notification<?> notification, boolean read) {
        this(notification.getType(), notification.getNotificationType(), notification.getId(), notification.getCreatedAt(), notification.getPayload(), read);
    }
}
