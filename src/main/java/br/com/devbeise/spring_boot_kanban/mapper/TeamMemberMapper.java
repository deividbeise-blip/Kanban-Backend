package br.com.devbeise.spring_boot_kanban.mapper;

import br.com.devbeise.spring_boot_kanban.database.model.TeamMember;
import br.com.devbeise.spring_boot_kanban.dto.TeamMemberDto;

public class TeamMemberMapper {

    private TeamMemberMapper() {
    }

    public static TeamMemberDto toDto(TeamMember member) {
        return TeamMemberDto.builder()
                .id(member.getId())
                .teamId(member.getTeam().getId())
                .userId(member.getUser().getId())
                .joinedAt(member.getJoinedAt())
                .build();
    }
}