package de.hofuniversity.assemblyplanner.service;

import de.hofuniversity.assemblyplanner.exceptions.ResourceNotFoundException;
import de.hofuniversity.assemblyplanner.persistence.model.Employee;
import de.hofuniversity.assemblyplanner.persistence.model.dto.NotificationItem;
import de.hofuniversity.assemblyplanner.persistence.model.dto.NotificationMarkedEvent;
import de.hofuniversity.assemblyplanner.persistence.model.notification.Notification;
import de.hofuniversity.assemblyplanner.persistence.repository.NotificationRepository;
import de.hofuniversity.assemblyplanner.service.api.NotificationHandler;
import de.hofuniversity.assemblyplanner.service.api.NotificationService;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserService userService;
    private final List<NotificationHandler> handlers;

    @Autowired
    public NotificationServiceImpl(NotificationRepository notificationRepository, UserService userService, List<NotificationHandler> handlers) {
        this.notificationRepository = notificationRepository;
        this.userService = userService;
        this.handlers = handlers;
    }

    @Override
    public NotificationItem getNotification(UUID id) {
        Notification<?> notification = notificationRepository
                .getNotificationByUserId(id, userService.getCurrentUser().getId())
                .orElseThrow(ResourceNotFoundException::new);

        return new NotificationItem(notification, notification.didRead(userService.getCurrentUser()));
    }

    @Override
    public Set<NotificationItem> getNotifications() {
        Employee currentUser = userService.getCurrentUser();

        return currentUser.getNotifications()
                .stream()
                .map(n -> new NotificationItem(n.getNotification(), n.isRead()))
                .collect(Collectors.toSet());
    }

    @Override
    public Set<NotificationItem> getNotifications(@NotNull Date after) {
        Employee currentUser = userService.getCurrentUser();

        return notificationRepository.getNotificationsByUserId(currentUser.getId(), after)
                .stream()
                .map(n -> new NotificationItem(n, n.didRead(currentUser)))
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional
    public NotificationItem createNotification(Notification<?> notification) {
        var createdNotification = notificationRepository.save(notification);
        handlers.forEach(h -> {
            if(!h.canHandle(createdNotification))
                return;
            h.handle(createdNotification);
        });
        return new NotificationItem(notification, false);
    }

    @Override
    public NotificationItem mark(NotificationMarkedEvent markedEvent, UUID id) {
        Employee currentUser = userService.getCurrentUser();
        Notification<?> notification = notificationRepository
                .getNotificationByUserId(id, currentUser.getId())
                .orElseThrow(ResourceNotFoundException::new);

        boolean marked = notification.setRead(currentUser, markedEvent.read());
        if(!marked)
            throw new AccessDeniedException("unable to mark notification for user " + currentUser.getUser().getUsername());

        notification = notificationRepository.save(notification);
        return new NotificationItem(notification, notification.didRead(currentUser));
    }
}
