package de.hsrm.master.concurrency.kanbanboard.controller;

import de.hsrm.master.concurrency.kanbanboard.service.TaskLockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController("/api/tasks")
public class TaskLockController {

    @Autowired
    private TaskLockService taskLockService;

    @PostMapping("/{taskId}/lock")
    public ResponseEntity<Void> acquireLock(@PathVariable Long taskId, @RequestHeader(value = "X-Session-Id", defaultValue = "anonymous") String sessionId, @RequestHeader(value = "X-Display-Name", defaultValue = "Anonym") String displayName) {
        taskLockService.acquireLock(taskId, sessionId, displayName);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{taskId}/lock")
    public ResponseEntity<Void> releaseLock(@PathVariable Long taskId, @RequestHeader(value = "X-Session-Id", defaultValue = "anonymous") String sessionId) {
        taskLockService.releaseLock(taskId, sessionId);
        return ResponseEntity.noContent().build();
    }
}
