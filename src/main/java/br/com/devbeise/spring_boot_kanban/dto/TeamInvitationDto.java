package br.com.devbeise.spring_boot_kanban.dto;

import br.com.devbeise.spring_boot_kanban.database.model.InvitationStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TeamInvitationDto {

    private Long id;

    private Long teamId;

    private String teamName;

    private Long invitedUserId;

    private String invitedUserName;

    private String invitedUserEmail;

    private Long invitedById;

    private String invitedByName;

    private InvitationStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime respondedAt;
}
