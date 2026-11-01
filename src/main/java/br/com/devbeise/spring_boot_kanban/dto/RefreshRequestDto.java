package br.com.devbeise.spring_boot_kanban.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RefreshRequestDto {

    @NotBlank(message = "O refresh token é obrigatório.")
    private String refreshToken;
}