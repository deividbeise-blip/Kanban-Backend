package br.com.devbeise.spring_boot_kanban.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginResponseDto {
    private Boolean requiresTwoFactor;
    private String accessToken;
    private String refreshToken;
    private String message;
}