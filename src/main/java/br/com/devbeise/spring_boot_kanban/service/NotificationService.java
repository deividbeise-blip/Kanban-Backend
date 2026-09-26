package br.com.devbeise.spring_boot_kanban.service;

import br.com.devbeise.spring_boot_kanban.database.model.Notification;
import br.com.devbeise.spring_boot_kanban.database.model.Task;
import br.com.devbeise.spring_boot_kanban.database.model.User;
import br.com.devbeise.spring_boot_kanban.database.repository.NotificationRepository;
import br.com.devbeise.spring_boot_kanban.database.repository.TaskRepository;
import br.com.devbeise.spring_boot_kanban.database.repository.UserRepository;
import br.com.devbeise.spring_boot_kanban.dto.NotificationDto;
import br.com.devbeise.spring_boot_kanban.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.com.devbeise.spring_boot_kanban.mapper.NotificationMapper;

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
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));

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

        return NotificationMapper.toDto(notificationRepository.save(notification));
    }

    @Transactional(readOnly = true)
    public List<NotificationDto> getNotificationsByUser(Long userId, Boolean isRead) {
        return notificationRepository.findAllByUserIdAndIsRead(userId, isRead).stream()
                .map(NotificationMapper::toDto)
                .collect(Collectors.toList());
    }
    
}
