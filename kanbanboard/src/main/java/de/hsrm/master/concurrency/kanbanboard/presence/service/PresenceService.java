package de.hsrm.master.concurrency.kanbanboard.presence.service;

import de.hsrm.master.concurrency.kanbanboard.presence.dto.PresenceEvent;
import de.hsrm.master.concurrency.kanbanboard.presence.dto.PresenceEventType;
import de.hsrm.master.concurrency.kanbanboard.stomp.StompService;
import de.hsrm.master.concurrency.kanbanboard.task.service.TaskLockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class PresenceService implements IPresenceService {

    private final Set<String> activeSessions = Collections.newSetFromMap(new ConcurrentHashMap<>());

    @Autowired
    private StompService stompService;

    @Autowired
    private TaskLockService taskLockService;

    @Override
    public void sessionConnected(String sessionId) {
        activeSessions.add(sessionId);
        int count = activeSessions.size();
        log.info("Session verbunden: {} | Aktiv: {}", sessionId, count);
        stompService.presenceChanged(new PresenceEvent(sessionId, PresenceEventType.JOINED, count));
    }

    @Override
    public void sessionDisconnected(String sessionId) {
        activeSessions.remove(sessionId);
        int count = activeSessions.size();
        log.info("Session getrennt: {} | Aktiv: {}", sessionId, count);

        taskLockService.releaseAllLocksForSession(sessionId);

        stompService.presenceChanged(new PresenceEvent(sessionId, PresenceEventType.LEFT, count));
    }

    public Set<String> getActiveSessions() {
        return Collections.unmodifiableSet(activeSessions);
    }
}
