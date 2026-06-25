package de.hsrm.master.concurrency.kanbanboard.board.controller;

import de.hsrm.master.concurrency.kanbanboard.board.dto.BoardCreateRequest;
import de.hsrm.master.concurrency.kanbanboard.board.dto.BoardDetailResponse;
import de.hsrm.master.concurrency.kanbanboard.board.dto.BoardSummaryResponse;
import de.hsrm.master.concurrency.kanbanboard.board.dto.BoardUpdateRequest;
import de.hsrm.master.concurrency.kanbanboard.board.service.BoardService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/boards")
public class BoardController {

    @Autowired
    private BoardService boardService;

    @GetMapping
    public ResponseEntity<List<BoardSummaryResponse>> getAllBoards() {
        return ResponseEntity.ok(boardService.getAllBoards());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BoardDetailResponse> getBoard(@PathVariable Long id) {
        return ResponseEntity.ok(boardService.getBoardById(id));
    }

    @PostMapping
    public ResponseEntity<BoardDetailResponse> createBoard(@Valid @RequestBody BoardCreateRequest request) {
        BoardDetailResponse created = boardService.createBoard(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BoardDetailResponse> updateBoard(@PathVariable Long id, @Valid @RequestBody BoardUpdateRequest request) {
        return ResponseEntity.ok(boardService.updateBoard(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBoard(@PathVariable Long id) {
        boardService.deleteBoard(id);
        return ResponseEntity.noContent().build();
    }
}
