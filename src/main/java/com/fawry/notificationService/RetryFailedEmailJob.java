package com.fawry.notificationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;


import java.util.Date;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class RetryFailedEmailJob {
    private final JavaMailSender emailSender;
    private final NotificationRepo notificationRepo;

    @Scheduled(cron = "${my.cron.expression}") // Schedule every 1 hour
    public void retryFailedEmails() {
        notificationRepo.findByStatus(Status.FAILED)
                        .forEach(notification -> {
                            try {
                                SimpleMailMessage message = Utils.createMessageFromNotification(notification);

                                emailSender.send(message);

                                notification.setStatus(Status.SENT);
                                notification.setSentAt(new Date());

                                log.info("Notification resent successfully");
                            } catch (MailException ex) {
                                int retryAmount = notification.getRetryAmount() - 1;
                                notification.setRetryAmount(retryAmount);
                                if(retryAmount == 0) {
                                    notification.setStatus(Status.RETRY_FAILED);
                                }
                                log.error("Failed to resent notification");
                            }
                            notificationRepo.save(notification);
                        });
    }
}
