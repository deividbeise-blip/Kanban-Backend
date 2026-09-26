package br.com.devbeise.spring_boot_kanban.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*; // Importa todas as anotações do Lombok

import java.time.LocalDateTime;

@Getter // ESSENCIAL para resolver o erro do getUserId()
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder // ESSENCIAL para resolver o erro do .id() no Builder
public class RefreshTokenDto {

    private Long id;

    @NotNull(message = "{refreshtoken.userId.required}")
    private Long userId;

    @NotBlank(message = "{refreshtoken.token.required}")
    private String token;

    private LocalDateTime expiresAt;

    private Boolean revoked;

    private LocalDateTime createdAt;
}
