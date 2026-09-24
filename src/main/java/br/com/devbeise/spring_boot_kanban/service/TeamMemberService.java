package br.com.devbeise.spring_boot_kanban.service;

import br.com.devbeise.spring_boot_kanban.database.model.Team;
import br.com.devbeise.spring_boot_kanban.database.model.TeamMember;
import br.com.devbeise.spring_boot_kanban.database.model.User;
import br.com.devbeise.spring_boot_kanban.database.repository.TeamMemberRepository;
import br.com.devbeise.spring_boot_kanban.database.repository.TeamRepository;
import br.com.devbeise.spring_boot_kanban.database.repository.UserRepository;
import br.com.devbeise.spring_boot_kanban.dto.TeamMemberDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeamMemberService {

    private final TeamMemberRepository teamMemberRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;

    @Transactional
    public TeamMemberDto addMember(TeamMemberDto dto) {
        if (teamMemberRepository.existsByTeamIdAndUserId(dto.getTeamId(), dto.getUserId())) {
            throw new RuntimeException("Usuário já é membro deste time.");
        }

        Team team = teamRepository.findById(dto.getTeamId())
                .orElseThrow(() -> new RuntimeException("Time não encontrado."));
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        TeamMember member = TeamMember.builder()
                .team(team)
                .user(user)
                .joinedAt(LocalDateTime.now())
                .build();

        return convertToDto(teamMemberRepository.save(member));
    }

    @Transactional(readOnly = true)
    public List<TeamMemberDto> listMembersByTeam(Long teamId) {
        return teamMemberRepository.findAllByTeamId(teamId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private TeamMemberDto convertToDto(TeamMember member) {
        return TeamMemberDto.builder()
                .id(member.getId())
                .teamId(member.getTeam().getId())
                .userId(member.getUser().getId())
                .joinedAt(member.getJoinedAt())
                .build();
    }
}
