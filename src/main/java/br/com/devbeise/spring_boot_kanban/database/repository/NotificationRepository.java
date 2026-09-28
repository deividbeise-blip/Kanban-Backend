package br.com.devbeise.spring_boot_kanban.database.repository;

import br.com.devbeise.spring_boot_kanban.database.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findAllByMessage(String message);

    List<Notification> findAllByIsRead(Boolean isRead);

    List<Notification> findAllByCreatedAt(LocalDateTime createdAt);


    List<Notification> findAllByUserId(Long userId);


    List<Notification> findAllByUserIdAndIsRead(Long userId, Boolean isRead);


    List<Notification> findAllByTaskId(Long taskId);


    List<Notification> findAllByUserIdAndTaskIdAndIsRead(Long userId, Long taskId, Boolean isRead);
}
