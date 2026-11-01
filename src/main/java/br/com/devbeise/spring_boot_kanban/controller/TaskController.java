package br.com.devbeise.spring_boot_kanban.controller;

import br.com.devbeise.spring_boot_kanban.database.model.User;
import br.com.devbeise.spring_boot_kanban.dto.TaskDto;
import br.com.devbeise.spring_boot_kanban.dto.UpdateTaskRequestDto;
import br.com.devbeise.spring_boot_kanban.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskDto> createTask(@RequestBody @Valid TaskDto dto,
                                              @AuthenticationPrincipal User user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.createTask(dto, user));
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<TaskDto> updateTask(@PathVariable Long taskId,
                                              @RequestBody @Valid UpdateTaskRequestDto dto,
                                              @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(taskService.updateTask(taskId, dto, user));
    }

    @PatchMapping("/{taskId}/status")
    public ResponseEntity<TaskDto> updateStatus(@PathVariable Long taskId,
                                                @RequestParam String status,
                                                @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(taskService.updateStatus(taskId, status, user));
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long taskId,
                                           @AuthenticationPrincipal User user) {
        taskService.deleteTask(taskId, user);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/team/{teamId}")
    public ResponseEntity<List<TaskDto>> getTasksByTeam(@PathVariable Long teamId,
                                                        @RequestParam(required = false) String status,
                                                        @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(taskService.getTasksByTeam(teamId, status, user));
    }
}