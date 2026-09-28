package br.com.devbeise.spring_boot_kanban.controller;

import br.com.devbeise.spring_boot_kanban.dto.TeamDto;
import br.com.devbeise.spring_boot_kanban.service.TeamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

    @PostMapping
    public ResponseEntity<TeamDto> createTeam(@RequestBody @Valid TeamDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(teamService.createTeam(dto));
    }

    @GetMapping("/invite")
    public ResponseEntity<TeamDto> findByInviteToken(@RequestParam String token) {
        return ResponseEntity.ok(teamService.findByInviteToken(token));
    }
}
