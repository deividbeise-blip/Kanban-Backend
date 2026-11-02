package br.com.devbeise.spring_boot_kanban.database.repository;

import br.com.devbeise.spring_boot_kanban.database.model.InvitationStatus;
import br.com.devbeise.spring_boot_kanban.database.model.TeamInvitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamInvitationRepository extends JpaRepository<TeamInvitation, Long> {

    boolean existsByTeamIdAndInvitedUserIdAndStatus(Long teamId, Long invitedUserId, InvitationStatus status);

    List<TeamInvitation> findAllByInvitedUserIdAndStatusOrderByCreatedAtDesc(Long invitedUserId, InvitationStatus status);

    List<TeamInvitation> findAllByTeamIdAndStatusOrderByCreatedAtDesc(Long teamId, InvitationStatus status);

    // Garante que só o próprio convidado encontra o convite (os outros recebem 404)
    Optional<TeamInvitation> findByIdAndInvitedUserId(Long id, Long invitedUserId);
}
