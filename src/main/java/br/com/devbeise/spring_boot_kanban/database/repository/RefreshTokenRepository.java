package br.com.devbeise.spring_boot_kanban.database.repository;

import br.com.devbeise.spring_boot_kanban.database.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    List<RefreshToken> findAllByUserId(Long userId);

    Optional<RefreshToken> findByTokenAndRevokedFalseAndExpiresAtAfter(String token, LocalDateTime now);

    void deleteByToken(String token);

    void deleteByUserId(Long userId);
}