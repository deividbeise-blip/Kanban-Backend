package br.com.devbeise.spring_boot_kanban.mapper;

import br.com.devbeise.spring_boot_kanban.database.model.Team;
import br.com.devbeise.spring_boot_kanban.dto.TeamDto;

public class TeamMapper {

    private TeamMapper() {
    }

    public static TeamDto toDto(Team team) {
        return TeamDto.builder()
                .id(team.getId())
                .name(team.getName())
                .inviteToken(team.getInviteToken())
                .masterId(team.getMaster().getId())
                .build();
    }
}