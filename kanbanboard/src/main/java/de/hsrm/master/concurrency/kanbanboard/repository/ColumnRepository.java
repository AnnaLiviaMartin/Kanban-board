package de.hsrm.master.concurrency.kanbanboard.repository;

import de.hsrm.master.concurrency.kanbanboard.entity.BoardColumn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ColumnRepository extends JpaRepository<BoardColumn, Long> {

    List<BoardColumn> findByBoardIdOrderByPositionAsc(Long boardId);

    @Query("SELECT COALESCE(MAX(b.position), -1) FROM BoardColumn b WHERE b.board.id = :boardId")
    int findMaxPositionByBoardId(@Param("boardId") Long boardId);

    Optional<BoardColumn> findByIdAndBoardId(Long id, Long boardId);
}
