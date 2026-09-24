package br.com.devbeise.spring_boot_kanban.service;

import br.com.devbeise.spring_boot_kanban.database.model.RefreshToken;
import br.com.devbeise.spring_boot_kanban.database.model.User;
import br.com.devbeise.spring_boot_kanban.database.repository.RefreshTokenRepository;
import br.com.devbeise.spring_boot_kanban.database.repository.UserRepository;
import br.com.devbeise.spring_boot_kanban.dto.RefreshTokenDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    @Transactional
    public RefreshTokenDto createToken(RefreshTokenDto dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        // Não removemos os tokens antigos do usuário aqui: cada dispositivo/sessão
        // mantém seu próprio refresh token, permitindo login simultâneo em vários lugares.
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(dto.getToken())
                .expiresAt(LocalDateTime.now().plusDays(7))
                .build();

        return convertToDto(refreshTokenRepository.save(refreshToken));
    }

    @Transactional
    public void logout(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token não encontrado."));
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);
    }

    private RefreshTokenDto convertToDto(RefreshToken token) {
        return RefreshTokenDto.builder()
                .id(token.getId())
                .userId(token.getUser().getId())
                .token(token.getToken())
                .expiresAt(token.getExpiresAt())
                .revoked(token.getRevoked())
                .createdAt(token.getCreatedAt())
                .build();
    }
}