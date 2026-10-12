package br.com.devbeise.spring_boot_kanban.service;

import br.com.devbeise.spring_boot_kanban.database.model.Team;
import br.com.devbeise.spring_boot_kanban.database.model.TeamMember;
import br.com.devbeise.spring_boot_kanban.database.model.User;
import br.com.devbeise.spring_boot_kanban.database.repository.TeamMemberRepository;
import br.com.devbeise.spring_boot_kanban.database.repository.TeamRepository;
import br.com.devbeise.spring_boot_kanban.dto.TeamDto;
import br.com.devbeise.spring_boot_kanban.exception.DuplicateResourceException;
import br.com.devbeise.spring_boot_kanban.exception.ResourceNotFoundException;
import br.com.devbeise.spring_boot_kanban.mapper.TeamMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamService {

    private static final String INVITE_ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789";
    private static final int INVITE_TOKEN_LENGTH = 20;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;

    @Transactional
    public TeamDto createTeam(TeamDto dto, User master) {
        Team savedTeam = teamRepository.save(Team.builder()
                .name(dto.getName())
                .inviteToken(generateUniqueInviteToken())
                .master(master)
                .build());

        teamMemberRepository.save(TeamMember.builder()
                .team(savedTeam)
                .user(master)
                .joinedAt(LocalDateTime.now())
                .build());

        return TeamMapper.toDto(savedTeam);
    }

    @Transactional
    public TeamDto joinByInviteToken(String inviteToken, User user) {
        Team team = teamRepository.findByInviteToken(inviteToken)
                .orElseThrow(() -> new ResourceNotFoundException("Código de convite inválido."));

        if (teamMemberRepository.existsByTeamIdAndUserId(team.getId(), user.getId())) {
            throw new DuplicateResourceException("Você já faz parte desta equipe.");
        }

        teamMemberRepository.save(TeamMember.builder()
                .team(team)
                .user(user)
                .joinedAt(LocalDateTime.now())
                .build());

        TeamDto result = TeamMapper.toDto(team);
        result.setInviteToken(null);
        return result;
    }

    @Transactional(readOnly = true)
    public List<TeamDto> listMyTeams(User user) {
        return teamMemberRepository.findAllByUserId(user.getId()).stream()
                .map(member -> {
                    Team team = member.getTeam();
                    TeamDto dto = TeamMapper.toDto(team);
                    if (!team.getMaster().getId().equals(user.getId())) {
                        dto.setInviteToken(null);
                    }
                    return dto;
                })
                .toList();
    }

    private String generateUniqueInviteToken() {
        String token;
        do {
            StringBuilder sb = new StringBuilder(INVITE_TOKEN_LENGTH);
            for (int i = 0; i < INVITE_TOKEN_LENGTH; i++) {
                sb.append(INVITE_ALPHABET.charAt(SECURE_RANDOM.nextInt(INVITE_ALPHABET.length())));
            }
            token = sb.toString();
        } while (teamRepository.existsByInviteToken(token));
        return token;
    }
}