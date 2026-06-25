package de.hsrm.master.concurrency.kanbanboard.service;

import de.hsrm.master.concurrency.kanbanboard.dto.board.BoardCreateRequest;
import de.hsrm.master.concurrency.kanbanboard.dto.board.BoardDetailResponse;
import de.hsrm.master.concurrency.kanbanboard.dto.board.BoardSummaryResponse;
import de.hsrm.master.concurrency.kanbanboard.dto.board.BoardUpdateRequest;

import java.util.List;

public interface IBoardService {
    List<BoardSummaryResponse> getAllBoards();
    BoardDetailResponse getBoardById(Long id);
    BoardDetailResponse createBoard(BoardCreateRequest request);
    BoardDetailResponse updateBoard(Long id, BoardUpdateRequest request);
    void deleteBoard(Long id);

}
