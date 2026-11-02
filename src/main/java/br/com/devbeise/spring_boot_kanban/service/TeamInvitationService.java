package br.com.devbeise.spring_boot_kanban.service;

import br.com.devbeise.spring_boot_kanban.database.model.InvitationStatus;
import br.com.devbeise.spring_boot_kanban.database.model.Team;
import br.com.devbeise.spring_boot_kanban.database.model.TeamInvitation;
import br.com.devbeise.spring_boot_kanban.database.model.TeamMember;
import br.com.devbeise.spring_boot_kanban.database.model.User;
import br.com.devbeise.spring_boot_kanban.database.repository.TeamInvitationRepository;
import br.com.devbeise.spring_boot_kanban.database.repository.TeamMemberRepository;
import br.com.devbeise.spring_boot_kanban.database.repository.TeamRepository;
import br.com.devbeise.spring_boot_kanban.database.repository.UserRepository;
import br.com.devbeise.spring_boot_kanban.dto.NotificationDto;
import br.com.devbeise.spring_boot_kanban.dto.TeamDto;
import br.com.devbeise.spring_boot_kanban.dto.TeamInvitationDto;
import br.com.devbeise.spring_boot_kanban.exception.DuplicateResourceException;
import br.com.devbeise.spring_boot_kanban.exception.ResourceNotFoundException;
import br.com.devbeise.spring_boot_kanban.mapper.TeamInvitationMapper;
import br.com.devbeise.spring_boot_kanban.mapper.TeamMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Fluxo de convite: o master convida por e-mail (PENDING) e o convidado
 * aceita (vira membro) ou recusa. Nos dois casos o master é notificado.
 */
@Service
@RequiredArgsConstructor
public class TeamInvitationService {

    private final TeamInvitationRepository invitationRepository;
    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final UserRepository userRepository;
    private final TeamAccessService teamAccessService;
    private final NotificationService notificationService;

    @Transactional
    public TeamInvitationDto invite(Long teamId, String email, User requester) {
        teamAccessService.requireMaster(teamId, requester.getId());

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Equipe não encontrada."));
        User invitedUser = userRepository.findByEmail(email.trim())
                .orElseThrow(() -> new ResourceNotFoundException("Este usuário não possui conta."));

        if (teamMemberRepository.existsByTeamIdAndUserId(teamId, invitedUser.getId())) {
            throw new DuplicateResourceException("Este usuário já é membro da equipe.");
        }
        if (invitationRepository.existsByTeamIdAndInvitedUserIdAndStatus(
                teamId, invitedUser.getId(), InvitationStatus.PENDING)) {
            throw new DuplicateResourceException("Já existe um convite pendente para este usuário.");
        }

        TeamInvitation invitation = invitationRepository.save(TeamInvitation.builder()
                .team(team)
                .invitedUser(invitedUser)
                .invitedBy(requester)
                .build());

        return TeamInvitationMapper.toDto(invitation);
    }

    /** Convites pendentes do usuário logado (aparecem no sino com Aceitar/Recusar). */
    @Transactional(readOnly = true)
    public List<TeamInvitationDto> listMine(User user) {
        return invitationRepository
                .findAllByInvitedUserIdAndStatusOrderByCreatedAtDesc(user.getId(), InvitationStatus.PENDING)
                .stream()
                .map(TeamInvitationMapper::toDto)
                .toList();
    }

    /** Convites pendentes de uma equipe, para o master acompanhar quem ainda não respondeu. */
    @Transactional(readOnly = true)
    public List<TeamInvitationDto> listPendingByTeam(Long teamId, User requester) {
        teamAccessService.requireMaster(teamId, requester.getId());

        return invitationRepository
                .findAllByTeamIdAndStatusOrderByCreatedAtDesc(teamId, InvitationStatus.PENDING)
                .stream()
                .map(TeamInvitationMapper::toDto)
                .toList();
    }

    /** Retorna a equipe para o front já poder abrir o board. */
    @Transactional
    public TeamDto accept(Long invitationId, User user) {
        TeamInvitation invitation = findPendingInvitation(invitationId, user);
        Team team = invitation.getTeam();

        // Pode já ser membro se aceitou outro convite da mesma equipe antes
        if (!teamMemberRepository.existsByTeamIdAndUserId(team.getId(), user.getId())) {
            teamMemberRepository.save(TeamMember.builder()
                    .team(team)
                    .user(user)
                    .joinedAt(LocalDateTime.now())
                    .build());
        }

        respond(invitation, InvitationStatus.ACCEPTED);
        notifyInviter(invitation, user.getName() + " aceitou o convite para a equipe " + team.getName() + ".");

        return TeamMapper.toDto(team);
    }

    @Transactional
    public void decline(Long invitationId, User user) {
        TeamInvitation invitation = findPendingInvitation(invitationId, user);

        respond(invitation, InvitationStatus.DECLINED);
        notifyInviter(invitation,
                user.getName() + " recusou o convite para a equipe " + invitation.getTeam().getName() + ".");
    }

    // Convite de outra pessoa ou inexistente dá 404: não revela que o id existe
    private TeamInvitation findPendingInvitation(Long invitationId, User user) {
        TeamInvitation invitation = invitationRepository.findByIdAndInvitedUserId(invitationId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Convite não encontrado."));

        if (invitation.getStatus() != InvitationStatus.PENDING) {
            throw new DuplicateResourceException("Este convite já foi respondido.");
        }
        return invitation;
    }

    private void respond(TeamInvitation invitation, InvitationStatus status) {
        invitation.setStatus(status);
        invitation.setRespondedAt(LocalDateTime.now());
        invitationRepository.save(invitation);
    }

    private void notifyInviter(TeamInvitation invitation, String message) {
        notificationService.createNotification(
                NotificationDto.builder()
                        .userId(invitation.getInvitedBy().getId())
                        .message(message)
                        .build()
        );
    }
}
