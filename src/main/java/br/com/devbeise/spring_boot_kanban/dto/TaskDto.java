package br.com.devbeise.spring_boot_kanban.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskDto {

    private Long id;

    @NotBlank(message = "{task.title.required}")
    private String title;

    private String description;

    @NotBlank(message = "{task.status.required}")
    private String status;

    @NotNull(message = "{task.userId.required}")
    private Long userId;
}
