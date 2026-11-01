package br.com.devbeise.spring_boot_kanban.service;

import br.com.devbeise.spring_boot_kanban.database.model.Team;
import br.com.devbeise.spring_boot_kanban.database.model.TeamMember;
import br.com.devbeise.spring_boot_kanban.database.model.User;
import br.com.devbeise.spring_boot_kanban.database.repository.TeamMemberRepository;
import br.com.devbeise.spring_boot_kanban.database.repository.TeamRepository;
import br.com.devbeise.spring_boot_kanban.database.repository.UserRepository;
import br.com.devbeise.spring_boot_kanban.dto.AddMemberRequestDto;
import br.com.devbeise.spring_boot_kanban.dto.NotificationDto;
import br.com.devbeise.spring_boot_kanban.dto.TeamMemberDto;
import br.com.devbeise.spring_boot_kanban.exception.DuplicateResourceException;
import br.com.devbeise.spring_boot_kanban.exception.ResourceNotFoundException;
import br.com.devbeise.spring_boot_kanban.mapper.TeamMemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamMemberService {

    private final TeamMemberRepository teamMemberRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final TeamAccessService teamAccessService;
    private final NotificationService notificationService;

    @Transactional
    public TeamMemberDto addMember(AddMemberRequestDto dto, User requester) {
        teamAccessService.requireMaster(dto.getTeamId(), requester.getId());

        Team team = teamRepository.findById(dto.getTeamId())
                .orElseThrow(() -> new ResourceNotFoundException("Time não encontrado."));
        User user = userRepository.findByEmail(dto.getEmail().trim())
                .orElseThrow(() -> new ResourceNotFoundException("Nenhum usuário cadastrado com este e-mail."));

        if (teamMemberRepository.existsByTeamIdAndUserId(team.getId(), user.getId())) {
            throw new DuplicateResourceException("Usuário já é membro deste time.");
        }

        TeamMember member = teamMemberRepository.save(TeamMember.builder()
                .team(team)
                .user(user)
                .joinedAt(LocalDateTime.now())
                .build());

        notificationService.createNotification(
                NotificationDto.builder()
                        .userId(user.getId())
                        .message("Você foi adicionado à equipe " + team.getName() + ".")
                        .build()
        );

        return TeamMemberMapper.toDto(member);
    }

    @Transactional(readOnly = true)
    public List<TeamMemberDto> listMembersByTeam(Long teamId, User requester) {
        teamAccessService.requireMember(teamId, requester.getId());

        return teamMemberRepository.findAllByTeamId(teamId).stream()
                .map(TeamMemberMapper::toDto)
                .toList();
    }
}