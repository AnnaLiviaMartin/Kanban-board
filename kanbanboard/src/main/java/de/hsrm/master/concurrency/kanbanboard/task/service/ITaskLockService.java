package de.hsrm.master.concurrency.kanbanboard.task.service;

import java.util.Optional;

public interface ITaskLockService {
    void acquireLock(Long taskId, String sessionId, String displayName);
    void releaseLock(Long taskId, String sessionId);
    void releaseAllLocksForSession(String sessionId);
    boolean isLocked(Long taskId, String requestingSessionId);
    Optional<String> getLockOwner(Long taskId);
    void evictExpiredLocks();
}
