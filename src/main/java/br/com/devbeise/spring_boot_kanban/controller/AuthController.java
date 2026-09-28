package br.com.devbeise.spring_boot_kanban.controller;

import br.com.devbeise.spring_boot_kanban.dto.LoginRequestDto;
import br.com.devbeise.spring_boot_kanban.dto.LoginResponseDto;
import br.com.devbeise.spring_boot_kanban.dto.TwoFactorVerifyDto;
import br.com.devbeise.spring_boot_kanban.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody @Valid LoginRequestDto dto) {
        return ResponseEntity.ok(authService.login(dto));
    }

    @PostMapping("/verify-2fa")
    public ResponseEntity<LoginResponseDto> verifyTwoFactor(@RequestBody @Valid TwoFactorVerifyDto dto) {
        return ResponseEntity.ok(authService.verifyTwoFactor(dto));
    }
}