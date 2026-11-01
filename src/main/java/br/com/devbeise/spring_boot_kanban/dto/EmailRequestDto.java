package br.com.devbeise.spring_boot_kanban.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmailRequestDto {

    @NotBlank(message = "{user.email.required}")
    @Email(message = "{user.email.invalid}")
    private String email;
}