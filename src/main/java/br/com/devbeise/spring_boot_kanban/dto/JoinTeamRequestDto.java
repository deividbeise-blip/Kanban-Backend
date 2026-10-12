package br.com.devbeise.spring_boot_kanban.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JoinTeamRequestDto {

    @NotBlank(message = "O código de convite é obrigatório.")
    private String inviteToken;
}