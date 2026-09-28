package br.com.devbeise.spring_boot_kanban.service;

import br.com.devbeise.spring_boot_kanban.config.TokenProvider;
import br.com.devbeise.spring_boot_kanban.database.model.User;
import br.com.devbeise.spring_boot_kanban.database.repository.UserRepository;
import br.com.devbeise.spring_boot_kanban.dto.*;
import br.com.devbeise.spring_boot_kanban.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final String TYPE_TWO_FACTOR = "TWO_FACTOR";

    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final UserService userService;
    private final VerificationTokenService verificationTokenService;
    private final RefreshTokenService refreshTokenService;
    private final EmailService emailService;

    @Transactional
    public LoginResponseDto login(LoginRequestDto request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));

        boolean needsTwoFactor = user.getLastLoginAt() == null
                || user.getLastLoginAt().isBefore(LocalDateTime.now().minusDays(3));

        if (needsTwoFactor) {
            String code = generateCode();

            verificationTokenService.createToken(
                    VerificationTokenDto.builder()
                            .userId(user.getId())
                            .token(code)
                            .type(TYPE_TWO_FACTOR)
                            .build()
            );

            emailService.sendVerificationCode(user.getEmail(), code);

            return LoginResponseDto.builder()
                    .requiresTwoFactor(true)
                    .message("Verifique o código enviado para seu email.")
                    .build();
        }

        return issueTokens(user);
    }

    @Transactional
    public LoginResponseDto verifyTwoFactor(TwoFactorVerifyDto request) {
        verificationTokenService.validate(request.getCode());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));

        return issueTokens(user);
    }

    private LoginResponseDto issueTokens(User user) {
        userService.updateLoginTimestamp(user);

        String accessToken = tokenProvider.gerarToken(user.getUsername());

        RefreshTokenDto refreshToken = refreshTokenService.createToken(
                RefreshTokenDto.builder()
                        .userId(user.getId())
                        .token(UUID.randomUUID().toString())
                        .build()
        );

        return LoginResponseDto.builder()
                .requiresTwoFactor(false)
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .message("Login realizado com sucesso.")
                .build();
    }

    private String generateCode() {
        return String.valueOf(ThreadLocalRandom.current().nextInt(100000, 999999));
    }
}