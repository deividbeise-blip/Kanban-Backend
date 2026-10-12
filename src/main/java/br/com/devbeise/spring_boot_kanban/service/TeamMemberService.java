package br.com.devbeise.spring_boot_kanban.service;

import br.com.devbeise.spring_boot_kanban.database.model.Team;
import br.com.devbeise.spring_boot_kanban.database.model.TeamMember;
import br.com.devbeise.spring_boot_kanban.database.model.User;
import br.com.devbeise.spring_boot_kanban.database.repository.TeamMemberRepository;
import br.com.devbeise.spring_boot_kanban.database.repository.TeamRepository;
import br.com.devbeise.spring_boot_kanban.database.repository.UserRepository;
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

    @Transactional
    public TeamMemberDto addMember(TeamMemberDto dto, User requester) {
        teamAccessService.requireMaster(dto.getTeamId(), requester.getId());

        if (teamMemberRepository.existsByTeamIdAndUserId(dto.getTeamId(), dto.getUserId())) {
            throw new DuplicateResourceException("Usuário já é membro deste time.");
        }

        Team team = teamRepository.findById(dto.getTeamId())
                .orElseThrow(() -> new ResourceNotFoundException("Time não encontrado."));
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));

        TeamMember member = TeamMember.builder()
                .team(team)
                .user(user)
                .joinedAt(LocalDateTime.now())
                .build();

        return TeamMemberMapper.toDto(teamMemberRepository.save(member));
    }

    @Transactional(readOnly = true)
    public List<TeamMemberDto> listMembersByTeam(Long teamId, User requester) {
        teamAccessService.requireMember(teamId, requester.getId());

        return teamMemberRepository.findAllByTeamId(teamId).stream()
                .map(TeamMemberMapper::toDto)
                .toList();
    }
}