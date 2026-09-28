package br.com.devbeise.spring_boot_kanban.service;

import br.com.devbeise.spring_boot_kanban.database.model.Task;
import br.com.devbeise.spring_boot_kanban.database.model.Team;
import br.com.devbeise.spring_boot_kanban.database.model.User;
import br.com.devbeise.spring_boot_kanban.database.repository.TaskRepository;
import br.com.devbeise.spring_boot_kanban.database.repository.TeamRepository;
import br.com.devbeise.spring_boot_kanban.database.repository.UserRepository;
import br.com.devbeise.spring_boot_kanban.dto.NotificationDto;
import br.com.devbeise.spring_boot_kanban.dto.TaskDto;
import br.com.devbeise.spring_boot_kanban.exception.ResourceNotFoundException;
import br.com.devbeise.spring_boot_kanban.mapper.TaskMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final NotificationService notificationService;

    @Transactional
    public TaskDto createTask(TaskDto dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));
        Team team = teamRepository.findById(dto.getTeamId())
                .orElseThrow(() -> new ResourceNotFoundException("Equipe não encontrada."));

        Task task = Task.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .status(dto.getStatus())
                .user(user)
                .team(team)
                .build();

        Task savedTask = taskRepository.save(task);

        // RN10: dispara notificação automática para o responsável
        notificationService.createNotification(
                NotificationDto.builder()
                        .userId(user.getId())
                        .taskId(savedTask.getId())
                        .message("Uma nova tarefa foi atribuída a você: " + savedTask.getTitle())
                        .build()
        );

        return TaskMapper.toDto(savedTask);
    }

    @Transactional
    public TaskDto updateStatus(Long taskId, String status) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Tarefa não encontrada."));
        task.setStatus(status);
        return TaskMapper.toDto(taskRepository.save(task));
    }

    @Transactional(readOnly = true)
    public List<TaskDto> getTasksByUserIdAndStatus(Long userId, String status) {
        return taskRepository.findAllByUserIdAndStatus(userId, status).stream()
                .map(TaskMapper::toDto)
                .collect(Collectors.toList());
    }

    public boolean isMasterOfTask(Long taskId, Long userId) {
        return taskRepository.findById(taskId)
                .map(task -> task.getTeam().getMaster().getId().equals(userId))
                .orElse(false);
    }

    public boolean isMasterOfTeam(Long teamId, Long userId) {
        return teamRepository.existsByIdAndMasterId(teamId, userId);
    }
}