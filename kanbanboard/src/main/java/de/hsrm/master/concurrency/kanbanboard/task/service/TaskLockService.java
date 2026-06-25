package de.hsrm.master.concurrency.kanbanboard.task.service;

import de.hsrm.master.concurrency.kanbanboard.lock.LockAction;
import de.hsrm.master.concurrency.kanbanboard.lock.LockEvent;
import de.hsrm.master.concurrency.kanbanboard.exception.TaskLockedException;
import de.hsrm.master.concurrency.kanbanboard.stomp.StompService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
@EnableScheduling
@Slf4j
public class TaskLockService implements ITaskLockService {

    private static final int LOCK_TTL_SECONDS = 30;

    private final Map<Long, LockEntry> locks = new ConcurrentHashMap<>();
    @Autowired
    private StompService stompService;

    @Override
    public void acquireLock(Long taskId, String sessionId, String displayName) {
        LockEntry existing = locks.get(taskId);

        if (existing != null && !existing.isExpired() && !existing.sessionId().equals(sessionId)) {
            throw new TaskLockedException(taskId, existing.sessionId());
        }

        LockEntry newLock = new LockEntry(sessionId, displayName, Instant.now().plusSeconds(LOCK_TTL_SECONDS));
        locks.put(taskId, newLock);

        log.debug("Lock gesetzt: Task {} → Session {}", taskId, sessionId);
        stompService.lockChanged(new LockEvent(taskId, sessionId, LockAction.LOCKED, displayName));
    }

    @Override
    public void releaseLock(Long taskId, String sessionId) {
        LockEntry existing = locks.get(taskId);
        if (existing != null && existing.sessionId().equals(sessionId)) {
            locks.remove(taskId);
            log.debug("Lock freigegeben: Task {} von Session {}", taskId, sessionId);
            stompService.lockChanged(new LockEvent(taskId, sessionId, LockAction.UNLOCKED, null));
        }
    }

    @Override
    public void releaseAllLocksForSession(String sessionId) {
        locks.entrySet().removeIf(entry -> {
            if (entry.getValue().sessionId().equals(sessionId)) {
                Long taskId = entry.getKey();
                log.debug("Lock durch Disconnect freigegeben: Task {} Session {}", taskId, sessionId);
                stompService.lockChanged(new LockEvent(taskId, sessionId, LockAction.UNLOCKED, null));
                return true;
            }
            return false;
        });
    }

    @Override
    public boolean isLocked(Long taskId, String requestingSessionId) {
        LockEntry entry = locks.get(taskId);
        if (entry == null || entry.isExpired()) return false;
        return !entry.sessionId().equals(requestingSessionId);
    }

    @Override
    public Optional<String> getLockOwner(Long taskId) {
        LockEntry entry = locks.get(taskId);
        if (entry == null || entry.isExpired()) return Optional.empty();
        return Optional.of(entry.sessionId());
    }

    @Scheduled(fixedDelay = 10_000)
    @Override
    public void evictExpiredLocks() {
        locks.entrySet().removeIf(entry -> {
            if (entry.getValue().isExpired()) {
                Long taskId = entry.getKey();
                String sessionId = entry.getValue().sessionId();
                log.debug("Lock abgelaufen (TTL): Task {}", taskId);
                stompService.lockChanged(new LockEvent(taskId, sessionId, LockAction.EXPIRED, null));
                return true;
            }
            return false;
        });
    }

    private record LockEntry(String sessionId, String displayName, Instant expiresAt) {
        boolean isExpired() {
            return Instant.now().isAfter(expiresAt);
        }
    }
}
