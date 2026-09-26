package br.com.devbeise.spring_boot_kanban.controller;

import br.com.devbeise.spring_boot_kanban.dto.RefreshTokenDto;
import br.com.devbeise.spring_boot_kanban.service.RefreshTokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/refresh-tokens")
@RequiredArgsConstructor
public class RefreshTokenController {

    private final RefreshTokenService refreshTokenService;

    @PostMapping
    public ResponseEntity<RefreshTokenDto> createToken(@RequestBody @Valid RefreshTokenDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(refreshTokenService.createToken(dto));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestParam String token) {
        refreshTokenService.logout(token);
        return ResponseEntity.noContent().build();
    }
}
