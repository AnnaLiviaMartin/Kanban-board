package de.hsrm.master.concurrency.kanbanboard.repository;

import de.hsrm.master.concurrency.kanbanboard.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
}
