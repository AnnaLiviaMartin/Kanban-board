package de.hsrm.master.concurrency.kanbanboard.board.dto;

import de.hsrm.master.concurrency.kanbanboard.boardColumn.dto.ColumnResponse;
import de.hsrm.master.concurrency.kanbanboard.board.entity.Board;

import java.time.LocalDateTime;
import java.util.List;

public record BoardDetailResponse(Long id, String name, long version, LocalDateTime createdAt,
                                  List<ColumnResponse> columns) {
    public static BoardDetailResponse from(Board b) {
        return new BoardDetailResponse(b.getId(), b.getName(), b.getVersion(), b.getCreatedAt(), b.getColumns().stream().map(ColumnResponse::from).toList());
    }
}
