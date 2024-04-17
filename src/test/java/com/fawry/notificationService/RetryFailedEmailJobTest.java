package com.fawry.notificationService;

import com.fawry.notificationService.Notification;
import com.fawry.notificationService.NotificationRepo;
import com.fawry.notificationService.RetryFailedEmailJob;
import com.fawry.notificationService.Status;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;
@ExtendWith(MockitoExtension.class)
public class RetryFailedEmailJobTest {
    @Mock
    private JavaMailSender emailSender;

    @Mock
    private NotificationRepo notificationRepo;

    @InjectMocks
    private RetryFailedEmailJob retryFailedEmailJob;

    @Test
    public void retryFailedEmailsShouldSuccess() {
        // Arrange
        Notification notification1 = new Notification();
        notification1.setStatus(Status.FAILED);
        notification1.setRetryAmount(3);

        Notification notification2 = new Notification();
        notification2.setStatus(Status.FAILED);
        notification2.setRetryAmount(3);

        when(notificationRepo.findByStatus(Status.FAILED)).thenReturn(Arrays.asList(notification1, notification2));

        // Act
        retryFailedEmailJob.retryFailedEmails();

        // Assert
        ArgumentCaptor<Notification> notificationArgumentCaptor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepo, times(2)).save(notificationArgumentCaptor.capture());
        List<Notification> capturedNotifications = notificationArgumentCaptor.getAllValues();

        for (Notification capturedNotification : capturedNotifications) {
            assertThat(capturedNotification.getStatus()).isEqualTo(Status.SENT);
        }
        verify(emailSender, times(2)).send(any(SimpleMailMessage.class));
    }
    @Test
    public void retryFailedEmailsShouldFail() {
        // Arrange
        Notification notification1 = new Notification();
        notification1.setStatus(Status.FAILED);
        notification1.setRetryAmount(1);

        Notification notification2 = new Notification();
        notification2.setStatus(Status.FAILED);
        notification2.setRetryAmount(1);

        when(notificationRepo.findByStatus(Status.FAILED)).thenReturn(Arrays.asList(notification1, notification2));
        doThrow(new MailSendException("Test exception")).when(emailSender).send(any(SimpleMailMessage.class));
        try {
            // Act
            retryFailedEmailJob.retryFailedEmails();
        }catch (MailSendException ex){
            // Assert
            ArgumentCaptor<Notification> notificationArgumentCaptor = ArgumentCaptor.forClass(Notification.class);
            verify(notificationRepo, times(2)).save(notificationArgumentCaptor.capture());
            List<Notification> capturedNotifications = notificationArgumentCaptor.getAllValues();

            for (Notification capturedNotification : capturedNotifications) {
                assertThat(capturedNotification.getStatus()).isEqualTo(Status.RETRY_FAILED);
            }
        }
    }
}
