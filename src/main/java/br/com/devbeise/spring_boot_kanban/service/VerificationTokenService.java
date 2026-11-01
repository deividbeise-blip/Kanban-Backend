package br.com.devbeise.spring_boot_kanban.service;

import br.com.devbeise.spring_boot_kanban.database.model.User;
import br.com.devbeise.spring_boot_kanban.database.model.VerificationToken;
import br.com.devbeise.spring_boot_kanban.database.repository.UserRepository;
import br.com.devbeise.spring_boot_kanban.database.repository.VerificationTokenRepository;
import br.com.devbeise.spring_boot_kanban.dto.VerificationTokenDto;
import br.com.devbeise.spring_boot_kanban.exception.InvalidTokenException;
import br.com.devbeise.spring_boot_kanban.exception.ResourceNotFoundException;
import br.com.devbeise.spring_boot_kanban.exception.TooManyRequestsException;
import br.com.devbeise.spring_boot_kanban.mapper.VerificationTokenMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;

@Service
@RequiredArgsConstructor
public class VerificationTokenService {

    public static final String TYPE_TWO_FACTOR = "TWO_FACTOR";
    public static final String TYPE_PASSWORD_RESET = "PASSWORD_RESET";

    private static final int MAX_ATTEMPTS = 5;
    private static final long RESEND_COOLDOWN_SECONDS = 60;
    private static final String INVALID_CODE_MESSAGE = "Código inválido ou expirado.";
    private static final String INVALID_RESET_MESSAGE = "Link de redefinição inválido ou expirado.";

    private final VerificationTokenRepository tokenRepository;
    private final UserRepository userRepository;

    /**
     * Cria um token novo e apaga os anteriores do mesmo tipo.
     * O banco guarda só o hash SHA-256: quem lê a tabela não consegue usar o código.
     */
    @Transactional
    public VerificationTokenDto createToken(VerificationTokenDto dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));

        tokenRepository.findFirstByUserIdAndTypeOrderByCreatedAtDesc(user.getId(), dto.getType())
                .filter(last -> last.getCreatedAt()
                        .isAfter(LocalDateTime.now().minusSeconds(RESEND_COOLDOWN_SECONDS)))
                .ifPresent(last -> {
                    throw new TooManyRequestsException(
                            "Um código já foi enviado. Aguarde 1 minuto para solicitar outro.");
                });

        tokenRepository.deleteByUserIdAndType(user.getId(), dto.getType());

        VerificationToken verificationToken = VerificationToken.builder()
                .user(user)
                .token(hash(dto.getToken()))
                .type(dto.getType())
                .expiresAt(expirationFor(dto.getType()))
                .build();

        return VerificationTokenMapper.toDto(tokenRepository.save(verificationToken));
    }

    /**
     * Valida um código curto (2FA). Cada erro conta uma tentativa;
     * na quinta o código é queimado.
     */
    @Transactional(noRollbackFor = InvalidTokenException.class)
    public void validate(Long userId, String type, String code) {
        VerificationToken verificationToken = tokenRepository
                .findFirstByUserIdAndTypeAndUsedFalseAndExpiresAtAfterOrderByCreatedAtDesc(
                        userId, type, LocalDateTime.now())
                .orElseThrow(() -> new InvalidTokenException(INVALID_CODE_MESSAGE));

        boolean matches = code != null && MessageDigest.isEqual(
                verificationToken.getToken().getBytes(StandardCharsets.UTF_8),
                hash(code).getBytes(StandardCharsets.UTF_8));

        if (!matches) {
            int attempts = verificationToken.getAttempts() + 1;
            verificationToken.setAttempts(attempts);

            if (attempts >= MAX_ATTEMPTS) {
                verificationToken.setUsed(true);
            }

            tokenRepository.save(verificationToken);
            throw new InvalidTokenException(INVALID_CODE_MESSAGE);
        }

        verificationToken.setUsed(true);
        tokenRepository.save(verificationToken);
    }

    /**
     * Consome um token longo (link de redefinição). O próprio token identifica o
     * usuário, então não há contador de tentativas: adivinhar 256 bits é inviável.
     */
    @Transactional
    public User consume(String type, String rawToken) {
        if (rawToken == null || rawToken.isBlank()) {
            throw new InvalidTokenException(INVALID_RESET_MESSAGE);
        }

        VerificationToken verificationToken = tokenRepository
                .findFirstByTokenAndTypeAndUsedFalseAndExpiresAtAfter(hash(rawToken), type, LocalDateTime.now())
                .orElseThrow(() -> new InvalidTokenException(INVALID_RESET_MESSAGE));

        verificationToken.setUsed(true);
        tokenRepository.save(verificationToken);
        return verificationToken.getUser();
    }

    @Transactional(readOnly = true)
    public boolean hasActiveToken(Long userId, String type) {
        return tokenRepository.existsByUserIdAndTypeAndUsedFalseAndExpiresAtAfter(
                userId, type, LocalDateTime.now());
    }

    private LocalDateTime expirationFor(String type) {
        return switch (type) {
            case TYPE_TWO_FACTOR -> LocalDateTime.now().plusMinutes(15);
            case TYPE_PASSWORD_RESET -> LocalDateTime.now().plusMinutes(30);
            default -> LocalDateTime.now().plusHours(24);
        };
    }

    private String hash(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 indisponível na JVM.", e);
        }
    }
}