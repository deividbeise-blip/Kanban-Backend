package br.com.devbeise.spring_boot_kanban.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationDto {

    private Long id;

    @NotBlank(message = "{notification.message.required}")
    private String message;


    private Boolean isRead;

    private LocalDateTime createdAt;

    @NotNull(message = "{notification.userId.required}")
    private Long userId;

    private Long taskId;
}