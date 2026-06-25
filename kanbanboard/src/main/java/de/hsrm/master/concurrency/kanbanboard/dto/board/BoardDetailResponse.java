package de.hsrm.master.concurrency.kanbanboard.dto.board;

import de.hsrm.master.concurrency.kanbanboard.dto.column.ColumnResponse;
import de.hsrm.master.concurrency.kanbanboard.entity.Board;

import java.time.LocalDateTime;
import java.util.List;

public record BoardDetailResponse(Long id, String name, long version, LocalDateTime createdAt,
                                  List<ColumnResponse> columns) {
    public static BoardDetailResponse from(Board b) {
        return new BoardDetailResponse(b.getId(), b.getName(), b.getVersion(), b.getCreatedAt(), b.getColumns().stream().map(ColumnResponse::from).toList());
    }
}
