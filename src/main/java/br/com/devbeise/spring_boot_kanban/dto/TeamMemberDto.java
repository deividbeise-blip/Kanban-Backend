package br.com.devbeise.spring_boot_kanban.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TeamMemberDto {

    private Long id;

    private Long userId;

    private Long teamId;

    private LocalDateTime joinedAt;

    private String userName;

    private String userEmail;
}