package com.fawry.notificationService;

import com.fawry.notificationService.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;
@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {
    @Mock
    private NotificationRepo notificationRepo;

    @Mock
    private JavaMailSender emailSender;

    @Mock
    private Environment environment;

    @InjectMocks
    private NotificationService notificationService;
    @Test
    public void sendingNotificationShouldSuccess() {
        // Arrange
        NotificationRequest notificationRequest = new NotificationRequest();
        notificationRequest.setDestinationEmail("test@test.com");
        notificationRequest.setMsg("Test message");
        notificationRequest.setSubject("Test subject");
        // Act
        notificationService.send(notificationRequest);

        // Assert
        ArgumentCaptor<Notification> notificationArgumentCaptor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepo).save(notificationArgumentCaptor.capture());
        Notification notification = notificationArgumentCaptor.getValue();

        assertThat(notification.getStatus()).isEqualTo(Status.SENT);
        verify(emailSender).send(any(SimpleMailMessage.class));

    }
    @Test
    public void testSend_WhenMailExceptionIsThrown() {
        // Arrange
        NotificationRequest notificationRequest = new NotificationRequest();
        notificationRequest.setDestinationEmail("test@test.com");
        notificationRequest.setMsg("Test message");
        notificationRequest.setSubject("Test subject");


        when(environment.getProperty("retry.amount")).thenReturn("3");
        doThrow(new MailSendException("Test exception")).when(emailSender).send(any(SimpleMailMessage.class));

        try {
            // Act
            notificationService.send(notificationRequest);
        }catch (MailException ex){
            // Assert
            ArgumentCaptor<Notification> notificationArgumentCaptor = ArgumentCaptor.forClass(Notification.class);
            verify(notificationRepo).save(notificationArgumentCaptor.capture());
            Notification notification = notificationArgumentCaptor.getValue();

            assertThat(notification.getStatus()).isEqualTo(Status.FAILED);
            assertThat(notification.getRetryAmount()).isEqualTo(3);
        }
    }
}
