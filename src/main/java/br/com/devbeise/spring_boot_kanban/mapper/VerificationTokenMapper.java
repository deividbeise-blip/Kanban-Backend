package br.com.devbeise.spring_boot_kanban.mapper;

import br.com.devbeise.spring_boot_kanban.database.model.VerificationToken;
import br.com.devbeise.spring_boot_kanban.dto.VerificationTokenDto;

public class VerificationTokenMapper {

    private VerificationTokenMapper() {
    }

    public static VerificationTokenDto toDto(VerificationToken token) {
        return VerificationTokenDto.builder()
                .id(token.getId())
                .userId(token.getUser().getId())
                .token(token.getToken())
                .type(token.getType())
                .expiresAt(token.getExpiresAt())
                .used(token.getUsed())
                .createdAt(token.getCreatedAt())
                .build();
    }
}