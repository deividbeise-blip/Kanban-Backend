package br.com.devbeise.spring_boot_kanban.controller;

import br.com.devbeise.spring_boot_kanban.dto.VerificationTokenDto;
import br.com.devbeise.spring_boot_kanban.service.VerificationTokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/verification-tokens")
@RequiredArgsConstructor
public class VerificationTokenController {

    private final VerificationTokenService verificationTokenService;

    @PostMapping
    public ResponseEntity<VerificationTokenDto> createToken(@RequestBody @Valid VerificationTokenDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(verificationTokenService.createToken(dto));
    }

    @PostMapping("/validate")
    public ResponseEntity<Void> validate(@RequestParam String token) {
        verificationTokenService.validate(token);
        return ResponseEntity.noContent().build();
    }
}
