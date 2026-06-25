package de.hsrm.master.concurrency.kanbanboard.exception;


import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.LOCKED)
@Getter
public class TaskLockedException extends RuntimeException {
    private final Long taskId;
    private final String lockedBySession;

    public TaskLockedException(Long taskId, String lockedBySession) {
        super(String.format("Task %d ist bereits gesperrt (Session: %s).", taskId, lockedBySession));
        this.taskId = taskId;
        this.lockedBySession = lockedBySession;
    }
}