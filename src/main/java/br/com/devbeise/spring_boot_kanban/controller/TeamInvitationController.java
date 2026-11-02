package br.com.devbeise.spring_boot_kanban.controller;

import br.com.devbeise.spring_boot_kanban.database.model.User;
import br.com.devbeise.spring_boot_kanban.dto.InviteMemberRequestDto;
import br.com.devbeise.spring_boot_kanban.dto.TeamDto;
import br.com.devbeise.spring_boot_kanban.dto.TeamInvitationDto;
import br.com.devbeise.spring_boot_kanban.service.TeamInvitationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TeamInvitationController {

    private final TeamInvitationService invitationService;

    /** Só o master. 404 se o e-mail não tem conta; 409 se já é membro ou já tem convite pendente. */
    @PostMapping("/teams/{teamId}/invitations")
    public ResponseEntity<TeamInvitationDto> invite(@PathVariable Long teamId,
                                                    @RequestBody @Valid InviteMemberRequestDto dto,
                                                    @AuthenticationPrincipal User user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(invitationService.invite(teamId, dto.getEmail(), user));
    }

    @GetMapping("/teams/{teamId}/invitations")
    public ResponseEntity<List<TeamInvitationDto>> listPendingByTeam(@PathVariable Long teamId,
                                                                     @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(invitationService.listPendingByTeam(teamId, user));
    }

    @GetMapping("/invitations/mine")
    public ResponseEntity<List<TeamInvitationDto>> myInvitations(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(invitationService.listMine(user));
    }

    @PostMapping("/invitations/{invitationId}/accept")
    public ResponseEntity<TeamDto> accept(@PathVariable Long invitationId,
                                          @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(invitationService.accept(invitationId, user));
    }

    @PostMapping("/invitations/{invitationId}/decline")
    public ResponseEntity<Void> decline(@PathVariable Long invitationId,
                                        @AuthenticationPrincipal User user) {
        invitationService.decline(invitationId, user);
        return ResponseEntity.noContent().build();
    }
}
