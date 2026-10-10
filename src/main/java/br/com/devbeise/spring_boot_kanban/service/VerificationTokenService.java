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

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class VerificationTokenService {

    private static final String TYPE_TWO_FACTOR = "TWO_FACTOR";
    private static final int MAX_ATTEMPTS = 5;

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

    @Transactional
    public void validate(String token) {
        VerificationToken verificationToken = tokenRepository
                .findByTokenAndUsedFalseAndExpiresAtAfter(token, LocalDateTime.now())
                .orElseThrow(() -> new InvalidTokenException("Token inválido ou expirado."));

        if (verificationToken.getAttempts() >= MAX_ATTEMPTS) {
            throw new InvalidTokenException("Número máximo de tentativas excedido. Solicite um novo código.");
        }

        verificationToken.setAttempts(verificationToken.getAttempts() + 1);
        verificationToken.setUsed(true);
        tokenRepository.save(verificationToken);
    }
}