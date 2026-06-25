package de.hsrm.master.concurrency.kanbanboard.boardColumn.service;

import de.hsrm.master.concurrency.kanbanboard.boardColumn.dto.ColumnCreateRequest;
import de.hsrm.master.concurrency.kanbanboard.boardColumn.dto.ColumnResponse;
import de.hsrm.master.concurrency.kanbanboard.boardColumn.dto.ColumnUpdateRequest;

import java.util.List;

public interface IColumnService {
    List<ColumnResponse> getColumnsForBoard(Long boardId);
    ColumnResponse getColumn(Long boardId, Long columnId);
    ColumnResponse createColumn(Long boardId, ColumnCreateRequest request);
    ColumnResponse updateColumn(Long boardId, Long columnId, ColumnUpdateRequest request);
    void deleteColumn(Long boardId, Long columnId);
}
