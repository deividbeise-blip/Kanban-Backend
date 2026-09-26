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
import br.com.devbeise.spring_boot_kanban.exception.ResourceNotFoundException;
import br.com.devbeise.spring_boot_kanban.mapper.TeamMapper;

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
                .orElseThrow(() -> new ResourceNotFoundException("Usuário master não encontrado."));

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

        return TeamMapper.toDto(savedTeam);
    }

    @Transactional(readOnly = true)
    public TeamDto findByInviteToken(String token) {
        Team team = teamRepository.findByInviteToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Time não encontrado com este token."));
        return TeamMapper.toDto(team);
    }
    
}