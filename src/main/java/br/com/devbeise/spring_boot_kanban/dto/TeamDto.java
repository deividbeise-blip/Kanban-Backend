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

    @NotBlank(message = "{team.name.required}")
    private String name;

    private String inviteToken;

    @NotNull(message = "{team.masterId.required}")
    private Long masterId;
}
