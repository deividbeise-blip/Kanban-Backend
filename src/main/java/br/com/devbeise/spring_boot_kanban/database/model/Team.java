package br.com.devbeise.spring_boot_kanban.database.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class Team {
    private Long id;
    private String name;
    private String inviteToken;

    public Team(String name, String inviteToken) {
        this.name = name;
        this.inviteToken = inviteToken;
    }
}