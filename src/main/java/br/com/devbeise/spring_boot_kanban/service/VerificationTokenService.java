package br.com.devbeise.spring_boot_kanban.service;

import br.com.devbeise.spring_boot_kanban.database.model.User;
import br.com.devbeise.spring_boot_kanban.database.model.VerificationToken;
import br.com.devbeise.spring_boot_kanban.database.repository.UserRepository;
import br.com.devbeise.spring_boot_kanban.database.repository.VerificationTokenRepository;
import br.com.devbeise.spring_boot_kanban.dto.VerificationTokenDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.com.devbeise.spring_boot_kanban.exception.ResourceNotFoundException;
import br.com.devbeise.spring_boot_kanban.exception.InvalidTokenException;
import br.com.devbeise.spring_boot_kanban.mapper.VerificationTokenMapper;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class VerificationTokenService {

    private static final String TYPE_TWO_FACTOR = "TWO_FACTOR";
    private static final String TYPE_PASSWORD_RESET = "PASSWORD_RESET";

    private final VerificationTokenRepository tokenRepository;
    private final UserRepository userRepository;

    @Transactional
    public VerificationTokenDto createToken(VerificationTokenDto dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));

        // Remove apenas tokens antigos do MESMO tipo, sem afetar outros fluxos
        // (ex: gerar um código de 2FA não apaga um token de reset de senha pendente).
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

        verificationToken.setUsed(true);
        tokenRepository.save(verificationToken);
    }
    
}