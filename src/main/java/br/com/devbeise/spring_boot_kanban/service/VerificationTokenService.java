package br.com.devbeise.spring_boot_kanban.service;

import br.com.devbeise.spring_boot_kanban.database.model.User;
import br.com.devbeise.spring_boot_kanban.database.model.VerificationToken;
import br.com.devbeise.spring_boot_kanban.database.repository.UserRepository;
import br.com.devbeise.spring_boot_kanban.database.repository.VerificationTokenRepository;
import br.com.devbeise.spring_boot_kanban.dto.VerificationTokenDto;
import br.com.devbeise.spring_boot_kanban.exception.InvalidTokenException;
import br.com.devbeise.spring_boot_kanban.exception.ResourceNotFoundException;
import br.com.devbeise.spring_boot_kanban.mapper.VerificationTokenMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class VerificationTokenService {

    private static final String TYPE_TWO_FACTOR = "TWO_FACTOR";
    private static final int MAX_ATTEMPTS = 5;
    private static final String INVALID_CODE_MESSAGE = "Código inválido ou expirado.";

    private final VerificationTokenRepository tokenRepository;
    private final UserRepository userRepository;

    @Transactional
    public VerificationTokenDto createToken(VerificationTokenDto dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));

        tokenRepository.deleteByUserIdAndType(user.getId(), dto.getType());

        LocalDateTime expiration = TYPE_TWO_FACTOR.equals(dto.getType())
                ? LocalDateTime.now().plusMinutes(15)
                : LocalDateTime.now().plusHours(24);

        VerificationToken verificationToken = VerificationToken.builder()
                .user(user)
                .token(dto.getToken())
                .type(dto.getType())
                .expiresAt(expiration)
                .build();

        return VerificationTokenMapper.toDto(tokenRepository.save(verificationToken));
    }

    // noRollbackFor é essencial: sem ele, a exceção desfaz a transação
    // e o incremento de "attempts" nunca chega ao banco.
    @Transactional(noRollbackFor = InvalidTokenException.class)
    public void validate(Long userId, String type, String code) {
        VerificationToken verificationToken = tokenRepository
                .findFirstByUserIdAndTypeAndUsedFalseAndExpiresAtAfterOrderByCreatedAtDesc(
                        userId, type, LocalDateTime.now())
                .orElseThrow(() -> new InvalidTokenException(INVALID_CODE_MESSAGE));

        // Comparação em tempo constante (evita ataque de timing)
        boolean matches = code != null && MessageDigest.isEqual(
                verificationToken.getToken().getBytes(StandardCharsets.UTF_8),
                code.getBytes(StandardCharsets.UTF_8));

        if (!matches) {
            int attempts = verificationToken.getAttempts() + 1;
            verificationToken.setAttempts(attempts);

            if (attempts >= MAX_ATTEMPTS) {
                verificationToken.setUsed(true); // queima o código: precisa pedir outro
            }

            tokenRepository.save(verificationToken);
            throw new InvalidTokenException(INVALID_CODE_MESSAGE);
        }

        verificationToken.setUsed(true);
        tokenRepository.save(verificationToken);
    }
}