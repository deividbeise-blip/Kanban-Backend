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

    @NotNull(message = "{verificationtoken.userId.required}")
    private Long userId;

    @NotBlank(message = "{verificationtoken.token.required}")
    private String token;

    @NotBlank(message = "{verificationtoken.type.required}")
    private String type;

    private LocalDateTime expiresAt;

    private Boolean used;

    private LocalDateTime createdAt;
}
