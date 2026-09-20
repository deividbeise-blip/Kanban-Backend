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
public class Notification {
    private Long id;
    private String message;
    private Boolean isRead;
    private LocalDateTime createdAt;

    public Notification(String message, Boolean isRead, LocalDateTime createdAt) {
        this.message = message;
        this.isRead = isRead;
        this.createdAt = createdAt;
    }
}