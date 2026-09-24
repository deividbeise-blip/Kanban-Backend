package br.com.devbeise.spring_boot_kanban.controller;

import br.com.devbeise.spring_boot_kanban.dto.TeamMemberDto;
import br.com.devbeise.spring_boot_kanban.service.TeamMemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/team-members")
@RequiredArgsConstructor
public class TeamMemberController {

    private final TeamMemberService teamMemberService;

    @PostMapping
    public ResponseEntity<TeamMemberDto> addMember(@RequestBody @Valid TeamMemberDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(teamMemberService.addMember(dto));
    }

    @GetMapping("/team/{teamId}")
    public ResponseEntity<List<TeamMemberDto>> listMembersByTeam(@PathVariable Long teamId) {
        return ResponseEntity.ok(teamMemberService.listMembersByTeam(teamId));
    }
}
