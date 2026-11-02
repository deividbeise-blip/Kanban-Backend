package br.com.devbeise.spring_boot_kanban.service;

import br.com.devbeise.spring_boot_kanban.database.model.User;
import br.com.devbeise.spring_boot_kanban.database.repository.TeamMemberRepository;
import br.com.devbeise.spring_boot_kanban.dto.TeamMemberDto;
import br.com.devbeise.spring_boot_kanban.mapper.TeamMemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Consulta de membros. A entrada em uma equipe acontece só pelo
 * convite aceito (TeamInvitationService.accept).
 */
@Service
@RequiredArgsConstructor
public class TeamMemberService {

    private final TeamMemberRepository teamMemberRepository;
    private final TeamAccessService teamAccessService;

    @Transactional(readOnly = true)
    public List<TeamMemberDto> listMembersByTeam(Long teamId, User requester) {
        teamAccessService.requireMember(teamId, requester.getId());

        return teamMemberRepository.findAllByTeamId(teamId).stream()
                .map(TeamMemberMapper::toDto)
                .toList();
    }
}
