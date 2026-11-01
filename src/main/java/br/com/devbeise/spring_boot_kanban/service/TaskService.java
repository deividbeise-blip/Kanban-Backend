package br.com.devbeise.spring_boot_kanban.service;

import br.com.devbeise.spring_boot_kanban.database.model.Task;
import br.com.devbeise.spring_boot_kanban.database.model.Team;
import br.com.devbeise.spring_boot_kanban.database.model.User;
import br.com.devbeise.spring_boot_kanban.database.repository.NotificationRepository;
import br.com.devbeise.spring_boot_kanban.database.repository.TaskRepository;
import br.com.devbeise.spring_boot_kanban.database.repository.TeamMemberRepository;
import br.com.devbeise.spring_boot_kanban.database.repository.TeamRepository;
import br.com.devbeise.spring_boot_kanban.database.repository.UserRepository;
import br.com.devbeise.spring_boot_kanban.dto.NotificationDto;
import br.com.devbeise.spring_boot_kanban.dto.TaskDto;
import br.com.devbeise.spring_boot_kanban.dto.UpdateTaskRequestDto;
import br.com.devbeise.spring_boot_kanban.exception.AccessDeniedBusinessException;
import br.com.devbeise.spring_boot_kanban.exception.ResourceNotFoundException;
import br.com.devbeise.spring_boot_kanban.mapper.TaskMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TaskService {

    private static final String STATUS_TODO = "To do";
    private static final String STATUS_DONE = "Done";
    private static final Set<String> ALLOWED_STATUS = Set.of(STATUS_TODO, "Doing", "Revision", STATUS_DONE);

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final NotificationRepository notificationRepository;
    private final TeamAccessService teamAccessService;
    private final NotificationService notificationService;

    @Transactional
    public TaskDto createTask(TaskDto dto, User requester) {
        teamAccessService.requireMaster(dto.getTeamId(), requester.getId());

        String status = (dto.getStatus() == null || dto.getStatus().isBlank()) ? STATUS_TODO : dto.getStatus();
        validateStatus(status);

        Team team = teamRepository.findById(dto.getTeamId())
                .orElseThrow(() -> new ResourceNotFoundException("Equipe não encontrada."));
        User assignee = findTeamMember(team.getId(), dto.getUserId());

        Task savedTask = taskRepository.save(Task.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .status(status)
                .user(assignee)
                .team(team)
                .build());

        notifyAssignee(savedTask, assignee, requester);

        return TaskMapper.toDto(savedTask);
    }

    /**
     * Edita título, descrição e responsável. Status fica no PATCH /status
     * (regras de movimentação) e a equipe não muda.
     */
    @Transactional
    public TaskDto updateTask(Long taskId, UpdateTaskRequestDto dto, User requester) {
        Task task = findTask(taskId);
        Long teamId = teamIdOf(task);

        teamAccessService.requireMaster(teamId, requester.getId());

        User newAssignee = findTeamMember(teamId, dto.getUserId());
        boolean assigneeChanged = task.getUser() == null || !task.getUser().getId().equals(newAssignee.getId());

        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setUser(newAssignee);
        Task savedTask = taskRepository.save(task);

        if (assigneeChanged) {
            notifyAssignee(savedTask, newAssignee, requester);
        }

        return TaskMapper.toDto(savedTask);
    }

    @Transactional
    public TaskDto updateStatus(Long taskId, String status, User requester) {
        validateStatus(status);

        Task task = findTask(taskId);
        Long teamId = teamIdOf(task);

        teamAccessService.requireMember(teamId, requester.getId());

        if (STATUS_DONE.equals(status) && !teamRepository.existsByIdAndMasterId(teamId, requester.getId())) {
            throw new AccessDeniedBusinessException("Apenas o master da equipe pode mover uma task para 'Done'.");
        }

        task.setStatus(status);
        return TaskMapper.toDto(taskRepository.save(task));
    }

    @Transactional
    public void deleteTask(Long taskId, User requester) {
        Task task = findTask(taskId);

        teamAccessService.requireMaster(teamIdOf(task), requester.getId());

        notificationRepository.detachTask(task.getId());
        taskRepository.delete(task);
    }

    @Transactional(readOnly = true)
    public List<TaskDto> getTasksByTeam(Long teamId, String status, User requester) {
        teamAccessService.requireMember(teamId, requester.getId());

        List<Task> tasks;
        if (status == null || status.isBlank()) {
            tasks = taskRepository.findAllByTeamId(teamId);
        } else {
            validateStatus(status);
            tasks = taskRepository.findAllByTeamIdAndStatus(teamId, status);
        }
        return tasks.stream().map(TaskMapper::toDto).toList();
    }

    private Task findTask(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Tarefa não encontrada."));
    }

    private Long teamIdOf(Task task) {
        if (task.getTeam() == null) {
            throw new AccessDeniedBusinessException("Tarefa sem equipe associada.");
        }
        return task.getTeam().getId();
    }

    private User findTeamMember(Long teamId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));

        if (!teamMemberRepository.existsByTeamIdAndUserId(teamId, user.getId())) {
            throw new IllegalArgumentException("O responsável precisa ser membro da equipe.");
        }
        return user;
    }

    // RN10: avisa o responsável, exceto quando o master atribui a task a si mesmo.
    private void notifyAssignee(Task task, User assignee, User requester) {
        if (assignee.getId().equals(requester.getId())) {
            return;
        }
        notificationService.createNotification(
                NotificationDto.builder()
                        .userId(assignee.getId())
                        .taskId(task.getId())
                        .message("Uma nova tarefa foi atribuída a você: " + task.getTitle())
                        .build()
        );
    }

    private void validateStatus(String status) {
        if (status == null || !ALLOWED_STATUS.contains(status)) {
            throw new IllegalArgumentException("Status inválido. Use: To do, Doing, Revision ou Done.");
        }
    }
}