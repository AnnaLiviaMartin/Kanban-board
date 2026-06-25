package de.hsrm.master.concurrency.kanbanboard.controller;

import de.hsrm.master.concurrency.kanbanboard.dto.task.TaskCreateRequest;
import de.hsrm.master.concurrency.kanbanboard.dto.task.TaskMoveRequest;
import de.hsrm.master.concurrency.kanbanboard.dto.task.TaskResponse;
import de.hsrm.master.concurrency.kanbanboard.dto.task.TaskUpdateRequest;
import de.hsrm.master.concurrency.kanbanboard.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("/api/boards/{boardId}/columns/{columnId}/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @GetMapping
    public ResponseEntity<List<TaskResponse>> getTasks(@PathVariable Long boardId, @PathVariable Long columnId) {
        return ResponseEntity.ok(taskService.getTasksForColumn(columnId));
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<TaskResponse> getTask(@PathVariable Long boardId, @PathVariable Long columnId, @PathVariable Long taskId) {
        return ResponseEntity.ok(taskService.getTask(taskId));
    }

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@PathVariable Long boardId, @PathVariable Long columnId, @Valid @RequestBody TaskCreateRequest request) {
        TaskResponse created = taskService.createTask(columnId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<TaskResponse> updateTask(@PathVariable Long boardId, @PathVariable Long columnId, @PathVariable Long taskId, @Valid @RequestBody TaskUpdateRequest request, @RequestHeader(value = "X-Session-Id", defaultValue = "anonymous") String sessionId) {
        return ResponseEntity.ok(taskService.updateTask(taskId, request, sessionId));
    }

    @PatchMapping("/{taskId}/move")
    public ResponseEntity<TaskResponse> moveTask(@PathVariable Long boardId, @PathVariable Long columnId, @PathVariable Long taskId, @Valid @RequestBody TaskMoveRequest request, @RequestHeader(value = "X-Session-Id", defaultValue = "anonymous") String sessionId) {
        return ResponseEntity.ok(taskService.moveTask(taskId, request, sessionId));
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long boardId, @PathVariable Long columnId, @PathVariable Long taskId, @RequestHeader(value = "X-Session-Id", defaultValue = "anonymous") String sessionId) {
        taskService.deleteTask(taskId, sessionId);
        return ResponseEntity.noContent().build();
    }
}
