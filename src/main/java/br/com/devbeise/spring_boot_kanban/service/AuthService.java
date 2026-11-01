package br.com.devbeise.spring_boot_kanban.service;

import br.com.devbeise.spring_boot_kanban.config.TokenProvider;
import br.com.devbeise.spring_boot_kanban.database.model.User;
import br.com.devbeise.spring_boot_kanban.database.repository.UserRepository;
import br.com.devbeise.spring_boot_kanban.dto.*;
import br.com.devbeise.spring_boot_kanban.exception.InvalidCredentialsException;
import br.com.devbeise.spring_boot_kanban.exception.InvalidTokenException;
import br.com.devbeise.spring_boot_kanban.exception.TooManyRequestsException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

import static br.com.devbeise.spring_boot_kanban.service.VerificationTokenService.TYPE_PASSWORD_RESET;
import static br.com.devbeise.spring_boot_kanban.service.VerificationTokenService.TYPE_TWO_FACTOR;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final String GENERIC_LOGIN_ERROR = "Email ou senha incorretos.";
    private static final String INVALID_REFRESH_ERROR = "Refresh token inválido ou expirado.";
    private static final String INVALID_CODE_ERROR = "Código inválido ou expirado.";
    private static final int TWO_FACTOR_INTERVAL_DAYS = 3;
    private static final int CODE_BOUND = 1_000_000;
    private static final int RESET_TOKEN_BYTES = 32;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final UserService userService;
    private final VerificationTokenService verificationTokenService;
    private final EmailService emailService;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Transactional
    public LoginResponseDto login(LoginRequestDto request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (AuthenticationException e) {

            throw new InvalidCredentialsException(GENERIC_LOGIN_ERROR);
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException(GENERIC_LOGIN_ERROR));

        if (needsTwoFactor(user)) {
            sendTwoFactorCode(user);

            return LoginResponseDto.builder()
                    .requiresTwoFactor(true)
                    .message("Verifique o código enviado para seu email.")
                    .build();
        }

        return issueTokens(user);
    }

    public LoginResponseDto verifyTwoFactor(TwoFactorVerifyDto request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidTokenException(INVALID_CODE_ERROR));

        verificationTokenService.validate(user.getId(), TYPE_TWO_FACTOR, request.getCode());

        return issueTokens(user);
    }

    /**
     * Reenvia o código só se existe um 2FA pendente (válido e não usado).
     * Isso exige que a senha tenha sido acertada há menos de 15 minutos,
     * então ninguém consegue disparar e-mails para um endereço qualquer.
     * Se o código foi queimado por 5 erros, não há pendente: o usuário precisa logar de novo.
     */
    public void resendTwoFactor(String email) {
        userRepository.findByEmail(email)
                .filter(user -> verificationTokenService.hasActiveToken(user.getId(), TYPE_TWO_FACTOR))
                .ifPresent(this::sendTwoFactorCode);
    }

    /**
     * Sem @Transactional de propósito: o createToken roda na própria transação.
     * Se este método fosse transacional, o TooManyRequestsException lançado lá dentro
     * marcaria a transação externa como rollback-only, mesmo sendo capturado aqui.
     * A resposta é sempre a mesma para não revelar quais e-mails estão cadastrados.
     */
    public void requestPasswordReset(String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return;
        }

        String rawToken = generateResetToken();
        try {
            verificationTokenService.createToken(
                    VerificationTokenDto.builder()
                            .userId(user.getId())
                            .token(rawToken)
                            .type(TYPE_PASSWORD_RESET)
                            .build()
            );
        } catch (TooManyRequestsException e) {
            return;
        }

        emailService.sendPasswordResetLink(user.getEmail(), frontendUrl + "/reset-password?token=" + rawToken);
    }

    @Transactional
    public void resetPassword(ResetPasswordRequestDto request) {
        User user = verificationTokenService.consume(TYPE_PASSWORD_RESET, request.getToken());
        userService.changePassword(user, request.getNewPassword());
    }

    @Transactional(readOnly = true)
    public LoginResponseDto refresh(RefreshRequestDto request) {
        String refreshToken = request.getRefreshToken();

        if (!tokenProvider.isRefreshTokenValid(refreshToken)) {
            throw new InvalidTokenException(INVALID_REFRESH_ERROR);
        }

        User user = userRepository.findByEmail(tokenProvider.getUsername(refreshToken))
                .orElseThrow(() -> new InvalidTokenException(INVALID_REFRESH_ERROR));

        if (needsTwoFactor(user)) {
            throw new InvalidTokenException(INVALID_REFRESH_ERROR);
        }

        return LoginResponseDto.builder()
                .requiresTwoFactor(false)
                .accessToken(tokenProvider.gerarToken(user.getUsername()))
                .message("Token renovado com sucesso.")
                .build();
    }

    private void sendTwoFactorCode(User user) {
        String code = generateCode();

        verificationTokenService.createToken(
                VerificationTokenDto.builder()
                        .userId(user.getId())
                        .token(code)
                        .type(TYPE_TWO_FACTOR)
                        .build()
        );

        emailService.sendVerificationCode(user.getEmail(), code);
    }

    private boolean needsTwoFactor(User user) {
        return user.getLastLoginAt() == null
                || user.getLastLoginAt().isBefore(LocalDateTime.now().minusDays(TWO_FACTOR_INTERVAL_DAYS));
    }

    private LoginResponseDto issueTokens(User user) {
        userService.updateLoginTimestamp(user);

        return LoginResponseDto.builder()
                .requiresTwoFactor(false)
                .accessToken(tokenProvider.gerarToken(user.getUsername()))
                .refreshToken(tokenProvider.gerarRefreshToken(user.getUsername()))
                .message("Login realizado com sucesso.")
                .build();
    }

    private String generateCode() {
        return String.format("%06d", SECURE_RANDOM.nextInt(CODE_BOUND));
    }

    private String generateResetToken() {
        byte[] bytes = new byte[RESET_TOKEN_BYTES];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}