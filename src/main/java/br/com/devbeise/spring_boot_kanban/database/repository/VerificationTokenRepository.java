package br.com.devbeise.spring_boot_kanban.database.repository;

import br.com.devbeise.spring_boot_kanban.database.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    Optional<VerificationToken> findByToken(String token);

    List<VerificationToken> findAllByType(String type);

    List<VerificationToken> findAllByExpiresAt(LocalDateTime expiresAt);

    List<VerificationToken> findAllByUserIdAndType(Long userId, String type);

    Optional<VerificationToken> findByTokenAndUsedFalseAndExpiresAtAfter(String token, LocalDateTime now);

    void deleteByToken(String token);

    void deleteByUserIdAndType(Long userId, String type);
}