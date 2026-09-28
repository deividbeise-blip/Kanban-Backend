package br.com.devbeise.spring_boot_kanban.database.repository;

import br.com.devbeise.spring_boot_kanban.database.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findAllByTitle(String title);

    List<Task> findAllByDescription(String description);

    List<Task> findAllByStatus(String status);

    List<Task> findAllByUserId(Long userId);

    List<Task> findAllByUserIdAndStatus(Long userId, String status);
}
