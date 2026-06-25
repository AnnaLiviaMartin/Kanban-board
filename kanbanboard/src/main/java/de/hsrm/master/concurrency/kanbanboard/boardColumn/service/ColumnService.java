package de.hsrm.master.concurrency.kanbanboard.boardColumn.service;

import de.hsrm.master.concurrency.kanbanboard.boardColumn.dto.ColumnCreateRequest;
import de.hsrm.master.concurrency.kanbanboard.boardColumn.dto.ColumnResponse;
import de.hsrm.master.concurrency.kanbanboard.boardColumn.dto.ColumnUpdateRequest;
import de.hsrm.master.concurrency.kanbanboard.board.entity.Board;
import de.hsrm.master.concurrency.kanbanboard.boardColumn.entity.BoardColumn;
import de.hsrm.master.concurrency.kanbanboard.exception.ResourceNotFoundException;
import de.hsrm.master.concurrency.kanbanboard.board.entity.BoardRepository;
import de.hsrm.master.concurrency.kanbanboard.boardColumn.entity.ColumnRepository;
import de.hsrm.master.concurrency.kanbanboard.stomp.StompService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ColumnService implements IColumnService {

    @Autowired
    private ColumnRepository columnRepository;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private StompService stompService;

    @Transactional(readOnly = true)
    @Override
    public List<ColumnResponse> getColumnsForBoard(Long boardId) {
        findBoardOrThrow(boardId);
        return columnRepository.findByBoardIdOrderByPositionAsc(boardId).stream().map(ColumnResponse::from).toList();
    }

    @Transactional(readOnly = true)
    @Override
    public ColumnResponse getColumn(Long boardId, Long columnId) {
        BoardColumn col = findColumnOrThrow(boardId, columnId);
        return ColumnResponse.from(col);
    }

    @Override
    public ColumnResponse createColumn(Long boardId, ColumnCreateRequest request) {
        Board board = findBoardOrThrow(boardId);

        int nextPosition = columnRepository.findMaxPositionByBoardId(boardId) + 1;
        BoardColumn column = new BoardColumn(request.name(), request.wipLimit(), nextPosition);
        board.addColumn(column);
        column = columnRepository.save(column);

        ColumnResponse response = ColumnResponse.from(column);
        stompService.columnCreated(response);
        return response;
    }

    @Override
    public ColumnResponse updateColumn(Long boardId, Long columnId, ColumnUpdateRequest request) {
        BoardColumn col = findColumnOrThrow(boardId, columnId);
        col.setName(request.name());
        col.setWipLimit(request.wipLimit());
        col = columnRepository.save(col);

        ColumnResponse response = ColumnResponse.from(col);
        stompService.columnUpdated(response);
        return response;
    }

    @Override
    public void deleteColumn(Long boardId, Long columnId) {
        BoardColumn col = findColumnOrThrow(boardId, columnId);
        Board board = col.getBoard();
        board.removeColumn(col);
        columnRepository.delete(col);

        List<BoardColumn> remaining = columnRepository.findByBoardIdOrderByPositionAsc(boardId);
        for (int i = 0; i < remaining.size(); i++) {
            remaining.get(i).setPosition(i);
        }
        columnRepository.saveAll(remaining);

        stompService.columnDeleted(columnId);
    }

    private Board findBoardOrThrow(Long boardId) {
        return boardRepository.findById(boardId).orElseThrow(() -> new ResourceNotFoundException("Board nicht gefunden: " + boardId));
    }

    BoardColumn findColumnOrThrow(Long boardId, Long columnId) {
        return columnRepository.findByIdAndBoardId(columnId, boardId).orElseThrow(() -> new ResourceNotFoundException("Column " + columnId + " nicht gefunden in Board " + boardId));
    }
}
