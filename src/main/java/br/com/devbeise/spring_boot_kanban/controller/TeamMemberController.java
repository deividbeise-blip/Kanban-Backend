package br.com.devbeise.spring_boot_kanban.controller;

import br.com.devbeise.spring_boot_kanban.database.model.User;
import br.com.devbeise.spring_boot_kanban.dto.TeamMemberDto;
import br.com.devbeise.spring_boot_kanban.service.TeamMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/team-members")
@RequiredArgsConstructor
public class TeamMemberController {

    private final TeamMemberService teamMemberService;

    @GetMapping("/team/{teamId}")
    public ResponseEntity<List<TeamMemberDto>> listMembersByTeam(@PathVariable Long teamId,
                                                                 @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(teamMemberService.listMembersByTeam(teamId, user));
    }
}
