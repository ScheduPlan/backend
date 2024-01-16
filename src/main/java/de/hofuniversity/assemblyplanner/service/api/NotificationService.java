package de.hofuniversity.assemblyplanner.service.api;

import de.hofuniversity.assemblyplanner.persistence.model.dto.NotificationItem;
import de.hofuniversity.assemblyplanner.persistence.model.dto.NotificationMarkedEvent;
import de.hofuniversity.assemblyplanner.persistence.model.notification.Notification;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

public interface NotificationService {

    public NotificationItem getNotification(UUID id);

    Set<NotificationItem> getNotifications();

    Set<NotificationItem> getNotifications(Date after);

    NotificationItem createNotification(Notification<?> notification);

    NotificationItem mark(NotificationMarkedEvent markedEvent, UUID id);
}
