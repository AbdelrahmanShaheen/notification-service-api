package com.fawry.notificationService;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class NotificationRequest {
    @Email
    @NotEmpty
    private String destinationEmail;
    @NotEmpty
    private String msg;
    @NotEmpty
    private String subject;
}