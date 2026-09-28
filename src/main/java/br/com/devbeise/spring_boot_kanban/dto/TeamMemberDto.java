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

    @NotNull(message = "{teammember.userId.required}")
    private Long userId;

    @NotNull(message = "{teammember.teamId.required}")
    private Long teamId;

    private LocalDateTime joinedAt;
}
