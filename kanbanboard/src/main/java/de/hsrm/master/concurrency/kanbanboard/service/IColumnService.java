package de.hsrm.master.concurrency.kanbanboard.service;

import de.hsrm.master.concurrency.kanbanboard.dto.column.ColumnCreateRequest;
import de.hsrm.master.concurrency.kanbanboard.dto.column.ColumnResponse;
import de.hsrm.master.concurrency.kanbanboard.dto.column.ColumnUpdateRequest;

import java.util.List;

public interface IColumnService {
    List<ColumnResponse> getColumnsForBoard(Long boardId);
    ColumnResponse getColumn(Long boardId, Long columnId);
    ColumnResponse createColumn(Long boardId, ColumnCreateRequest request);
    ColumnResponse updateColumn(Long boardId, Long columnId, ColumnUpdateRequest request);
    void deleteColumn(Long boardId, Long columnId);
}
