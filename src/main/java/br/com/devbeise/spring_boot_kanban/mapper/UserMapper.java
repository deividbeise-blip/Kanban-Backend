package br.com.devbeise.spring_boot_kanban.mapper;

import br.com.devbeise.spring_boot_kanban.database.model.User;
import br.com.devbeise.spring_boot_kanban.dto.UserDto;

public class UserMapper {

    private UserMapper() {
    }

    public static UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .lastLoginAt(user.getLastLoginAt())
                .build();
    }
}
