package de.hsrm.master.concurrency.kanbanboard.repository;

import de.hsrm.master.concurrency.kanbanboard.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByColumnIdOrderByPositionAsc(Long columnId);

    int countByColumnId(Long columnId);

    @Query("SELECT COALESCE(MAX(t.position), -1) FROM Task t WHERE t.column.id = :columnId")
    int findMaxPositionByColumnId(@Param("columnId") Long columnId);
}
