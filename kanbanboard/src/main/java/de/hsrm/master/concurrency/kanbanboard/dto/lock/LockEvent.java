package de.hsrm.master.concurrency.kanbanboard.dto.lock;

public record LockEvent(
        Long taskId,
        String sessionId,
        LockAction action,
        String lockedByUser
) {}

