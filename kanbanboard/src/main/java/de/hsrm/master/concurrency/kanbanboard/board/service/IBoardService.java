package de.hsrm.master.concurrency.kanbanboard.board.service;

import de.hsrm.master.concurrency.kanbanboard.board.dto.BoardCreateRequest;
import de.hsrm.master.concurrency.kanbanboard.board.dto.BoardDetailResponse;
import de.hsrm.master.concurrency.kanbanboard.board.dto.BoardSummaryResponse;
import de.hsrm.master.concurrency.kanbanboard.board.dto.BoardUpdateRequest;

import java.util.List;

public interface IBoardService {
    List<BoardSummaryResponse> getAllBoards();
    BoardDetailResponse getBoardById(Long id);
    BoardDetailResponse createBoard(BoardCreateRequest request);
    BoardDetailResponse updateBoard(Long id, BoardUpdateRequest request);
    void deleteBoard(Long id);

}
