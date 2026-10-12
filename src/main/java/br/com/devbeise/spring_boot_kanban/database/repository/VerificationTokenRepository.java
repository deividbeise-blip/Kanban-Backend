package br.com.devbeise.spring_boot_kanban.database.repository;

import br.com.devbeise.spring_boot_kanban.database.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    Optional<VerificationToken> findFirstByUserIdAndTypeAndUsedFalseAndExpiresAtAfterOrderByCreatedAtDesc(
            Long userId, String type, LocalDateTime now);

    Optional<VerificationToken> findFirstByUserIdAndTypeOrderByCreatedAtDesc(Long userId, String type);

    void deleteByUserIdAndType(Long userId, String type);
}