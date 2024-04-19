package com.fawry.notificationService;

import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    public Notification toNotification(NotificationRequest notificationRequest) {
        return Notification.builder()
                .content(notificationRequest.getMsg())
                .destination(notificationRequest.getDestinationEmail())
                .subject(notificationRequest.getSubject())
                .build();
    }
}