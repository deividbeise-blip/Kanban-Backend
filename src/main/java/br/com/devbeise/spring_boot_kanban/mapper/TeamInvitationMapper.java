package br.com.devbeise.spring_boot_kanban.mapper;

import br.com.devbeise.spring_boot_kanban.database.model.TeamInvitation;
import br.com.devbeise.spring_boot_kanban.dto.TeamInvitationDto;

public class TeamInvitationMapper {

    private TeamInvitationMapper() {
    }

    public static TeamInvitationDto toDto(TeamInvitation invitation) {
        return TeamInvitationDto.builder()
                .id(invitation.getId())
                .teamId(invitation.getTeam().getId())
                .teamName(invitation.getTeam().getName())
                .invitedUserId(invitation.getInvitedUser().getId())
                .invitedUserName(invitation.getInvitedUser().getName())
                .invitedUserEmail(invitation.getInvitedUser().getEmail())
                .invitedById(invitation.getInvitedBy().getId())
                .invitedByName(invitation.getInvitedBy().getName())
                .status(invitation.getStatus())
                .createdAt(invitation.getCreatedAt())
                .respondedAt(invitation.getRespondedAt())
                .build();
    }
}
