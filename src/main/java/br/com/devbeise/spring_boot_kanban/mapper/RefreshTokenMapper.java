package br.com.devbeise.spring_boot_kanban.mapper;

import br.com.devbeise.spring_boot_kanban.database.model.RefreshToken;
import br.com.devbeise.spring_boot_kanban.dto.RefreshTokenDto;

public class RefreshTokenMapper {

    private RefreshTokenMapper() {
    }

    public static RefreshTokenDto toDto(RefreshToken token) {
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