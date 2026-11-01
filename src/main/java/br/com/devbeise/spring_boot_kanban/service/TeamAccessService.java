package br.com.devbeise.spring_boot_kanban.service;

import br.com.devbeise.spring_boot_kanban.database.repository.TeamMemberRepository;
import br.com.devbeise.spring_boot_kanban.database.repository.TeamRepository;
import br.com.devbeise.spring_boot_kanban.exception.AccessDeniedBusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TeamAccessService {

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;

    public void requireMaster(Long teamId, Long userId) {
        if (teamId == null || !teamRepository.existsByIdAndMasterId(teamId, userId)) {
            throw new AccessDeniedBusinessException("Apenas o master da equipe pode realizar esta ação.");
        }
    }

    public void requireMember(Long teamId, Long userId) {
        if (teamId == null || !teamMemberRepository.existsByTeamIdAndUserId(teamId, userId)) {
            throw new AccessDeniedBusinessException("Você não faz parte desta equipe.");
        }
    }
}