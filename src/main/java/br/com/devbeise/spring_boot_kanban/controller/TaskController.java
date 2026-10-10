package br.com.devbeise.spring_boot_kanban.controller;

import br.com.devbeise.spring_boot_kanban.database.model.User;
import br.com.devbeise.spring_boot_kanban.dto.TaskDto;
import br.com.devbeise.spring_boot_kanban.exception.AccessDeniedBusinessException;
import br.com.devbeise.spring_boot_kanban.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private static final String STATUS_DONE = "Done";

    private final TaskService taskService;

    @PostMapping
    @PreAuthorize("@taskService.isMasterOfTeam(#dto.teamId, authentication.principal.id)")
    public ResponseEntity<TaskDto> createTask(@RequestBody @Valid TaskDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.createTask(dto));
    }

    @PatchMapping("/{taskId}/status")
    public ResponseEntity<TaskDto> updateStatus(
            @PathVariable Long taskId,
            @RequestParam String status,
            Authentication authentication) {

        if (STATUS_DONE.equalsIgnoreCase(status)) {
            Long userId = authentication.getPrincipal() instanceof User user
                    ? user.getId()
                    : null;

            if (!taskService.isMasterOfTask(taskId, userId)) {
                throw new AccessDeniedBusinessException("Apenas o master da equipe pode mover uma task para 'Done'.");
            }
        }

        return ResponseEntity.ok(taskService.updateStatus(taskId, status));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TaskDto>> getTasksByUserIdAndStatus(
            @PathVariable Long userId,
            @RequestParam String status) {
        return ResponseEntity.ok(taskService.getTasksByUserIdAndStatus(userId, status));
    }
}