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
public class TeamMember {
    private Long id;
    private LocalDateTime joinedAt;

    public TeamMember(LocalDateTime joinedAt) {
        this.joinedAt = joinedAt;
    }
}