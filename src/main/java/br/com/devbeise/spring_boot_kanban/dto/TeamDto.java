package br.com.devbeise.spring_boot_kanban.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TeamDto {

    private Long id;

    @NotBlank(message = "{team.name.required}")
    @Size(max = 100, message = "O nome da equipe deve ter no máximo 100 caracteres.")
    private String name;

    private String inviteToken;

    private Long masterId;
}