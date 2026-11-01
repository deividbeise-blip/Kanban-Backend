package br.com.devbeise.spring_boot_kanban.controller;

import br.com.devbeise.spring_boot_kanban.dto.*;
import br.com.devbeise.spring_boot_kanban.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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

    @PostMapping("/resend-2fa")
    public ResponseEntity<Map<String, String>> resendTwoFactor(@RequestBody @Valid EmailRequestDto dto) {
        authService.resendTwoFactor(dto.getEmail());
        return ResponseEntity.ok(Map.of("message",
                "Se houver um login pendente, um novo código foi enviado para o seu e-mail."));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@RequestBody @Valid EmailRequestDto dto) {
        authService.requestPasswordReset(dto.getEmail());
        return ResponseEntity.ok(Map.of("message",
                "Se o e-mail estiver cadastrado, você receberá um link para redefinir a senha."));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody @Valid ResetPasswordRequestDto dto) {
        authService.resetPassword(dto);
        return ResponseEntity.ok(Map.of("message", "Senha redefinida com sucesso."));
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDto> refresh(@RequestBody @Valid RefreshRequestDto dto) {
        return ResponseEntity.ok(authService.refresh(dto));
    }
}