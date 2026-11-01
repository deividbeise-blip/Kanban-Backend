package br.com.devbeise.spring_boot_kanban.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateTaskRequestDto {

    @NotBlank(message = "{task.title.required}")
    @Size(max = 100, message = "O título deve ter no máximo 100 caracteres.")
    private String title;

    private String description;

    @NotNull(message = "{task.userId.required}")
    private Long userId;
}