package br.com.devbeise.spring_boot_kanban.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TeamMemberDto {

    private Long id;

    @NotNull(message = "O ID do usuário membro é obrigatório.")
    private Long userId;

    @NotNull(message = "O ID do time é obrigatório.")
    private Long teamId;

    private LocalDateTime joinedAt;
}
