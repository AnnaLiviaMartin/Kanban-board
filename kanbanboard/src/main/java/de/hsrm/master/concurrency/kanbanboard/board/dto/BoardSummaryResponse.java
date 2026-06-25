package de.hsrm.master.concurrency.kanbanboard.board.dto;

import de.hsrm.master.concurrency.kanbanboard.board.entity.Board;

import java.time.LocalDateTime;

public record BoardSummaryResponse(Long id, String name, long version, LocalDateTime createdAt, int columnCount) {
    public static BoardSummaryResponse from(Board b) {
        return new BoardSummaryResponse(b.getId(), b.getName(), b.getVersion(), b.getCreatedAt(), b.getColumns().size());
    }
}