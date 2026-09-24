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

    @NotBlank(message = "O título da tarefa é obrigatório.")
    private String title;

    private String description;

    @NotBlank(message = "O status (coluna do Kanban) é obrigatório.")
    private String status;

    @NotNull(message = "O ID do usuário responsável pela tarefa é obrigatório.")
    private Long userId;
}
