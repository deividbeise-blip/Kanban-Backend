package br.com.devbeise.spring_boot_kanban.database.repository;

import br.com.devbeise.spring_boot_kanban.database.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {

    List<Team> findAllByName(String name);
    
    Optional<Team> findByInviteToken(String inviteToken);

    boolean existsByInviteToken(String inviteToken);

    List<Team> findAllByMasterId(Long masterId);

    boolean existsByIdAndMasterId(Long teamId, Long masterId);
}
