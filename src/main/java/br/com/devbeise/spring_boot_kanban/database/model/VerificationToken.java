package br.com.devbeise.spring_boot_kanban.database.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class VerificationToken {
    private Long id;
    private String token;
    private String type;
    private LocalDateTime expireAt;
    private Boolean used;

    public VerificationToken(String token, String type, LocalDateTime expireAt, Boolean used) {
        this.token = token;
        this.type = type;
        this.expireAt = expireAt;
        this.used = used;
    }
}