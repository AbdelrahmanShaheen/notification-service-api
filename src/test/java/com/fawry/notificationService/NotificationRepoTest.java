package com.fawry.notificationService;

import com.fawry.notificationService.Notification;
import com.fawry.notificationService.NotificationRepo;
import com.fawry.notificationService.Status;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class NotificationRepoTest {

    @Autowired
    private NotificationRepo notificationRepo;

    @Test
    // Arrange
    @Sql(statements = {
            "INSERT INTO notification (destination, content, sent_at, subject, status, retry_amount) VALUES ('destination1@example.com', 'Test content 1', CURRENT_TIMESTAMP, 'Test subject 1', 'FAILED', 3);",
            "INSERT INTO notification (destination, content, sent_at, subject, status, retry_amount) VALUES ('destination2@example.com', 'Test content 2', CURRENT_TIMESTAMP, 'Test subject 2', 'SENT', 3);"
    })
    public void testFindByStatus() {
        // Act
        List<Notification> notifications = notificationRepo.findByStatus(Status.FAILED);

        // Assert
        assertThat(notifications).hasSize(1);
        assertThat(notifications.get(0).getStatus()).isEqualTo(Status.FAILED);
    }
}