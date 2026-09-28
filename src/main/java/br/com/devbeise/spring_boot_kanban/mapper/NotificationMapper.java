package br.com.devbeise.spring_boot_kanban.mapper;

import br.com.devbeise.spring_boot_kanban.database.model.Notification;
import br.com.devbeise.spring_boot_kanban.dto.NotificationDto;

public class NotificationMapper {

    private NotificationMapper() {
    }

    public static NotificationDto toDto(Notification notification) {
        return NotificationDto.builder()
                .id(notification.getId())
                .message(notification.getMessage())
                .isRead(notification.getIsRead())
                .createdAt(notification.getCreatedAt())
                .userId(notification.getUser().getId())
                .taskId(notification.getTask() != null ? notification.getTask().getId() : null)
                .build();
    }
}