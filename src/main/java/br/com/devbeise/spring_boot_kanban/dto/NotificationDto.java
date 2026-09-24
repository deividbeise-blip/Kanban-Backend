package br.com.devbeise.spring_boot_kanban.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationDto {

    private Long id;

    @NotBlank(message = "A mensagem da notificação não pode estar vazia.")
    private String message;


    private Boolean isRead;

    private LocalDateTime createdAt;

    @NotNull(message = "O ID do usuário associado à notificação é obrigatório.")
    private Long userId;

    private Long taskId;
}