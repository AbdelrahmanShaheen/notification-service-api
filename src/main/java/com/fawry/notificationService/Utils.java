package com.fawry.notificationService;

import org.springframework.mail.SimpleMailMessage;

public class Utils {
    public static SimpleMailMessage createMessageFromNotification(Notification notification) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("${spring.mail.username}");
        message.setTo(notification.getDestination());
        message.setSubject(notification.getSubject());
        message.setText(notification.getContent());
        return message;
    }
}
