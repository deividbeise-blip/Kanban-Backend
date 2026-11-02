package br.com.devbeise.spring_boot_kanban.service;

import br.com.devbeise.spring_boot_kanban.database.model.Team;
import br.com.devbeise.spring_boot_kanban.database.model.TeamMember;
import br.com.devbeise.spring_boot_kanban.database.model.User;
import br.com.devbeise.spring_boot_kanban.database.repository.TeamMemberRepository;
import br.com.devbeise.spring_boot_kanban.database.repository.TeamRepository;
import br.com.devbeise.spring_boot_kanban.dto.TeamDto;
import br.com.devbeise.spring_boot_kanban.mapper.TeamMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;

    /** Quem cria a equipe vira o master e já entra como membro. */
    @Transactional
    public TeamDto createTeam(TeamDto dto, User master) {
        Team savedTeam = teamRepository.save(Team.builder()
                .name(dto.getName())
                .master(master)
                .build());

        teamMemberRepository.save(TeamMember.builder()
                .team(savedTeam)
                .user(master)
                .joinedAt(LocalDateTime.now())
                .build());

        return TeamMapper.toDto(savedTeam);
    }

    @Transactional(readOnly = true)
    public List<TeamDto> listMyTeams(User user) {
        return teamMemberRepository.findAllByUserId(user.getId()).stream()
                .map(member -> TeamMapper.toDto(member.getTeam()))
                .toList();
    }
}
