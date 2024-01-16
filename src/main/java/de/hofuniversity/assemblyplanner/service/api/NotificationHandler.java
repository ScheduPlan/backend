package de.hofuniversity.assemblyplanner.service.api;

import de.hofuniversity.assemblyplanner.persistence.model.notification.Notification;

public interface NotificationHandler {
    boolean canHandle(Notification<?> notification);
    boolean canHandle(Class<? extends Notification<?>> notificationClass);
    void handle(Notification<?> notification);
}
