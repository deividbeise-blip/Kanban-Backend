package br.com.devbeise.spring_boot_kanban.service;

import br.com.devbeise.spring_boot_kanban.database.model.Notification;
import br.com.devbeise.spring_boot_kanban.database.model.Task;
import br.com.devbeise.spring_boot_kanban.database.model.User;
import br.com.devbeise.spring_boot_kanban.database.repository.NotificationRepository;
import br.com.devbeise.spring_boot_kanban.database.repository.TaskRepository;
import br.com.devbeise.spring_boot_kanban.database.repository.UserRepository;
import br.com.devbeise.spring_boot_kanban.dto.NotificationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    @Transactional
    public NotificationDto createNotification(NotificationDto dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        Task task = null;
        if (dto.getTaskId() != null) {
            task = taskRepository.findById(dto.getTaskId()).orElse(null);
        }

        Notification notification = Notification.builder()
                .message(dto.getMessage())
                .isRead(dto.getIsRead() != null ? dto.getIsRead() : false)
                .createdAt(LocalDateTime.now())
                .user(user)
                .task(task)
                .build();

        return convertToDto(notificationRepository.save(notification));
    }

    @Transactional(readOnly = true)
    public List<NotificationDto> getNotificationsByUser(Long userId, Boolean isRead) {
        return notificationRepository.findAllByUserIdAndIsRead(userId, isRead).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private NotificationDto convertToDto(Notification notification) {
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
