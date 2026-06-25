package de.hsrm.master.concurrency.kanbanboard.dto.presence;

public record PresenceEvent(
        String sessionId,
        PresenceEventType eventType,
        int activeUserCount
) {}
