package de.hsrm.master.concurrency.kanbanboard.boardColumn.controller;

import de.hsrm.master.concurrency.kanbanboard.boardColumn.dto.ColumnCreateRequest;
import de.hsrm.master.concurrency.kanbanboard.boardColumn.dto.ColumnResponse;
import de.hsrm.master.concurrency.kanbanboard.boardColumn.dto.ColumnUpdateRequest;
import de.hsrm.master.concurrency.kanbanboard.boardColumn.service.ColumnService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/boards/{boardId}/columns")
public class ColumnController {

    @Autowired
    private ColumnService columnService;

    @GetMapping
    public ResponseEntity<List<ColumnResponse>> getColumns(@PathVariable Long boardId) {
        return ResponseEntity.ok(columnService.getColumnsForBoard(boardId));
    }

    @GetMapping("/{columnId}")
    public ResponseEntity<ColumnResponse> getColumn(@PathVariable Long boardId, @PathVariable Long columnId) {
        return ResponseEntity.ok(columnService.getColumn(boardId, columnId));
    }

    @PostMapping
    public ResponseEntity<ColumnResponse> createColumn(@PathVariable Long boardId, @Valid @RequestBody ColumnCreateRequest request) {
        ColumnResponse created = columnService.createColumn(boardId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{columnId}")
    public ResponseEntity<ColumnResponse> updateColumn(@PathVariable Long boardId, @PathVariable Long columnId, @Valid @RequestBody ColumnUpdateRequest request) {
        return ResponseEntity.ok(columnService.updateColumn(boardId, columnId, request));
    }

    @DeleteMapping("/{columnId}")
    public ResponseEntity<Void> deleteColumn(@PathVariable Long boardId, @PathVariable Long columnId) {
        columnService.deleteColumn(boardId, columnId);
        return ResponseEntity.noContent().build();
    }
}
