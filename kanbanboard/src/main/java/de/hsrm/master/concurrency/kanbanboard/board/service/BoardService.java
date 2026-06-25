package de.hsrm.master.concurrency.kanbanboard.board.service;

import de.hsrm.master.concurrency.kanbanboard.board.dto.BoardCreateRequest;
import de.hsrm.master.concurrency.kanbanboard.board.dto.BoardDetailResponse;
import de.hsrm.master.concurrency.kanbanboard.board.dto.BoardSummaryResponse;
import de.hsrm.master.concurrency.kanbanboard.board.dto.BoardUpdateRequest;
import de.hsrm.master.concurrency.kanbanboard.board.entity.Board;
import de.hsrm.master.concurrency.kanbanboard.exception.ResourceNotFoundException;
import de.hsrm.master.concurrency.kanbanboard.board.entity.BoardRepository;
import de.hsrm.master.concurrency.kanbanboard.stomp.StompService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class BoardService implements IBoardService {

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private StompService stompService;

    @Transactional(readOnly = true)
    @Override
    public List<BoardSummaryResponse> getAllBoards() {
        return boardRepository.findAll().stream().map(BoardSummaryResponse::from).toList();
    }

    @Transactional(readOnly = true)
    @Override
    public BoardDetailResponse getBoardById(Long id) {
        Board board = findBoardOrThrow(id);
        return BoardDetailResponse.from(board);
    }

    @Override
    public BoardDetailResponse createBoard(BoardCreateRequest request) {
        Board board = new Board(request.name());
        board = boardRepository.save(board);

        BoardDetailResponse response = BoardDetailResponse.from(board);
        stompService.boardCreated(response);
        return response;
    }

    @Override
    public BoardDetailResponse updateBoard(Long id, BoardUpdateRequest request) {
        Board board = findBoardOrThrow(id);
        board.setName(request.name());
        board = boardRepository.save(board);

        BoardDetailResponse response = BoardDetailResponse.from(board);
        stompService.boardUpdated(response);
        return response;
    }

    @Override
    public void deleteBoard(Long id) {
        Board board = findBoardOrThrow(id);
        boardRepository.delete(board);
        stompService.boardDeleted(id);
    }

    private Board findBoardOrThrow(Long id) {
        return boardRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Board nicht gefunden: " + id));
    }
}
