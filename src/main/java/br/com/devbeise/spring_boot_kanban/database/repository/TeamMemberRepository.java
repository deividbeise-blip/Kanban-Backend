package br.com.devbeise.spring_boot_kanban.database.repository;

import br.com.devbeise.spring_boot_kanban.database.model.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {

    List<TeamMember> findAllByJoinedAt(LocalDateTime joinedAt);


    List<TeamMember> findAllByTeamId(Long teamId);


    List<TeamMember> findAllByUserId(Long userId);


    Optional<TeamMember> findByTeamIdAndUserId(Long teamId, Long userId);


    boolean existsByTeamIdAndUserId(Long teamId, Long userId);


    void deleteByTeamIdAndUserId(Long teamId, Long userId);
}
