package de.hsrm.master.concurrency.kanbanboard.presence.dto;

public record PresenceEvent(
        String sessionId,
        PresenceEventType eventType,
        int activeUserCount
) {}
