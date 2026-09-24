package br.com.devbeise.spring_boot_kanban.service;

import br.com.devbeise.spring_boot_kanban.database.model.Team;
import br.com.devbeise.spring_boot_kanban.database.model.TeamMember;
import br.com.devbeise.spring_boot_kanban.database.model.User;
import br.com.devbeise.spring_boot_kanban.database.repository.TeamMemberRepository;
import br.com.devbeise.spring_boot_kanban.database.repository.TeamRepository;
import br.com.devbeise.spring_boot_kanban.database.repository.UserRepository;
import br.com.devbeise.spring_boot_kanban.dto.TeamDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final TeamMemberRepository teamMemberRepository;

    @Transactional
    public TeamDto createTeam(TeamDto dto) {
        User master = userRepository.findById(dto.getMasterId())
                .orElseThrow(() -> new RuntimeException("Usuário master não encontrado."));

        Team team = Team.builder()
                .name(dto.getName())
                .inviteToken(UUID.randomUUID().toString().substring(0, 8))
                .master(master)
                .build();

        Team savedTeam = teamRepository.save(team);

        TeamMember masterMembership = TeamMember.builder()
                .team(savedTeam)
                .user(master)
                .joinedAt(LocalDateTime.now())
                .build();
        teamMemberRepository.save(masterMembership);

        return convertToDto(savedTeam);
    }

    @Transactional(readOnly = true)
    public TeamDto findByInviteToken(String token) {
        Team team = teamRepository.findByInviteToken(token)
                .orElseThrow(() -> new RuntimeException("Time não encontrado com este token."));
        return convertToDto(team);
    }

    private TeamDto convertToDto(Team team) {
        return TeamDto.builder()
                .id(team.getId())
                .name(team.getName())
                .inviteToken(team.getInviteToken())
                .masterId(team.getMaster().getId())
                .build();
    }
}