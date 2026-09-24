package br.com.devbeise.spring_boot_kanban.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TeamDto {

    private Long id;

    @NotBlank(message = "O nome do time é obrigatório.")
    private String name;

    private String inviteToken;

    @NotNull(message = "O ID do master (dono do time) é obrigatório.")
    private Long masterId;
}
