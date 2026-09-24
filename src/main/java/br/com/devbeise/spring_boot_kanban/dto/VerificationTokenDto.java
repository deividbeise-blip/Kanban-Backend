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
public class VerificationTokenDto {

    private Long id;

    @NotNull(message = "O ID do usuário é obrigatório.")
    private Long userId;

    @NotBlank(message = "O token é obrigatório.")
    private String token;

    @NotBlank(message = "O tipo do token é obrigatório.")
    private String type;

    private LocalDateTime expiresAt;

    private Boolean used;

    private LocalDateTime createdAt;
}
