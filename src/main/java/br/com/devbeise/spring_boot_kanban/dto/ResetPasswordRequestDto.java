package br.com.devbeise.spring_boot_kanban.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResetPasswordRequestDto {

    @NotBlank(message = "O token de redefinição é obrigatório.")
    private String token;

    @NotBlank(message = "A senha é obrigatória.")
    @Size(min = 8, max = 72, message = "A senha deve ter entre 8 e 72 caracteres.")
    private String newPassword;
}