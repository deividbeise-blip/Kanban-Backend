package br.com.devbeise.spring_boot_kanban.database.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "teams")
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "invite_token", unique = true)
    private String inviteToken;

    @ManyToOne
    @JoinColumn(name = "master_id")
    private User master;
}